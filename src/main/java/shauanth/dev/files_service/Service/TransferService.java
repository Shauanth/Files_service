package shauanth.dev.files_service.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shauanth.dev.files_service.DTO.FileData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class TransferService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    public List<FileData> downloadAsBase64Batch(List<String> urls, Map<String, String> customHeaders) {
        List<FileData> result = new ArrayList<>();
        for (String url : urls) {
            try {
                String driveId = extractDriveId(url);
                String finalUrl = url;
                String fileName = "archivo_descargado";

                // INTERSECCIÓN PARA DOCUMENTOS DE GOOGLE
                if (driveId != null) {
                    log.info("Identificado recurso de Google Drive. ID: {}", driveId);

                    // 1. Obtener el NOMBRE REAL (Petición de Metadatos)
                    fileName = fetchGoogleDriveFileName(driveId, customHeaders);

                    // 2. Transformar a URL de descarga de API
                    finalUrl = "https://www.googleapis.com/drive/v3/files/" + driveId + "?alt=media";
                }

                log.info("Descargando desde: {}", finalUrl);

                HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(finalUrl))
                        .timeout(Duration.ofSeconds(30))
                        .GET();

                if (customHeaders != null) {
                    customHeaders.forEach(requestBuilder::header);
                }

                HttpResponse<byte[]> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() >= 400) {
                    log.error("Error en descarga (HTTP {}): {}", response.statusCode(), new String(response.body()));
                    continue;
                }

                String base64 = Base64.getEncoder().encodeToString(response.body());
                String contentType = response.headers().firstValue("Content-Type").orElse("application/octet-stream");

                // Si no es Drive, intentamos extraer el nombre de la respuesta normal
                if (driveId == null) {
                    fileName = extractFileNameFromResponse(response, url);
                }

                result.add(FileData.builder()
                        .fileName(fileName)
                        .base64Content(base64)
                        .contentType(contentType)
                        .build());

            } catch (Exception e) {
                log.error("Fallo procesando {}: {}", url, e.getMessage());
            }
        }
        return result;
    }

    // NUEVO MÉTODO: Obtiene el nombre real del archivo usando el Token
    private String fetchGoogleDriveFileName(String fileId, Map<String, String> headers) {
        try {
            String metadataUrl = "https://www.googleapis.com/drive/v3/files/" + fileId;
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(metadataUrl))
                    .GET();

            if (headers != null) headers.forEach(builder::header);

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Extraemos el valor del campo "name" del JSON manualmente para no añadir librerías extra
                String body = response.body();
                if (body.contains("\"name\": \"")) {
                    String name = body.split("\"name\": \"")[1].split("\"")[0];
                    return name;
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener el nombre real de Drive: {}", e.getMessage());
        }
        return "google_file_" + fileId; // Fallback
    }

    private String extractDriveId(String url) {
        if (url.contains("drive.google.com")) {
            if (url.contains("/d/")) return url.split("/d/")[1].split("/")[0].split("\\?")[0];
            if (url.contains("id=")) return url.split("id=")[1].split("&")[0];
        }
        return null;
    }

    private String extractFileNameFromResponse(HttpResponse<byte[]> response, String url) {
        return response.headers().firstValue("Content-Disposition")
                .map(cd -> cd.contains("filename=") ? cd.split("filename=")[1].replace("\"", "").split(";")[0] : null)
                .orElseGet(() -> {
                    String[] parts = url.split("/");
                    return parts[parts.length - 1].split("\\?")[0];
                });
    }

    /**
     * Sube archivos detectando automáticamente si el destino es Google Drive o genérico (S3/OneDrive).
     */
    public List<String> uploadBase64Batch(List<FileData> files, String targetUrl, Map<String, String> customHeaders) {
        List<String> results = new ArrayList<>();

        for (FileData file : files) {
            try {
                byte[] fileBytes = Base64.getDecoder().decode(file.getBase64Content());

                // Si la URL contiene googleapis o es un link de carpeta de Drive, usamos Multipart
                if (targetUrl.contains("googleapis.com") || targetUrl.contains("drive.google.com")) {
                    results.add(uploadToGoogleDrive(file, targetUrl, customHeaders, fileBytes));
                } else {
                    // Para OneDrive, S3, Azure, etc.
                    results.add(uploadGeneric(file, targetUrl, customHeaders, fileBytes));
                }
            } catch (Exception e) {
                results.add("ERROR - " + file.getFileName() + ": " + e.getMessage());
            }
        }
        return results;
    }

    private String uploadGeneric(FileData file, String targetUrl, Map<String, String> headers, byte[] bytes) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .timeout(Duration.ofMinutes(5))
                .header("Content-Type", file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .POST(HttpRequest.BodyPublishers.ofByteArray(bytes));

        if (headers != null) headers.forEach(builder::header);

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return (response.statusCode() >= 200 && response.statusCode() < 300)
                ? "SUCCESS - " + file.getFileName()
                : "FAIL - " + file.getFileName() + " (HTTP " + response.statusCode() + "): " + response.body();
    }

    // Reemplaza tu método uploadToGoogleDrive o añádelo a TransferService.java
    private String uploadToGoogleDrive(FileData file, String targetUrl, Map<String, String> headers, byte[] bytes) throws Exception {
        // Endpoint oficial para subir archivos con metadatos (nombre y carpeta)
        String uploadUrl = "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart";

        // Extraemos el folderId si el targetUrl enviado es el link de una carpeta
        String folderId = null;
        if (targetUrl.contains("/folders/")) {
            folderId = targetUrl.split("/folders/")[1].split("\\?")[0];
        }

        String boundary = "bridge_boundary_" + UUID.randomUUID().toString();

        // Construimos la parte de Metadatos (JSON) y la parte de Media (Archivo)
        StringBuilder metadata = new StringBuilder();
        metadata.append("--").append(boundary).append("\r\n")
                .append("Content-Type: application/json; charset=UTF-8\r\n\r\n")
                .append("{ \"name\": \"").append(file.getFileName()).append("\"");

        if (folderId != null) {
            metadata.append(", \"parents\": [\"").append(folderId).append("\"]");
        }
        metadata.append(" }\r\n")
                .append("--").append(boundary).append("\r\n")
                .append("Content-Type: ").append(file.getContentType()).append("\r\n\r\n");

        byte[] head = metadata.toString().getBytes();
        byte[] foot = ("\r\n--" + boundary + "--").getBytes();

        // Unimos todo en un solo body de bytes para el Proxy
        byte[] fullBody = new byte[head.length + bytes.length + foot.length];
        System.arraycopy(head, 0, fullBody, 0, head.length);
        System.arraycopy(bytes, 0, fullBody, head.length, bytes.length);
        System.arraycopy(foot, 0, fullBody, head.length + bytes.length, foot.length);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Content-Type", "multipart/related; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(fullBody));

        // Inyectamos el Token de la organización que viene del PC
        if (headers != null) headers.forEach(builder::header);

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return response.statusCode() < 300 ? "SUCCESS (Drive)" : "FAIL: " + response.body();
    }

    private String extractFileName(HttpResponse<byte[]> response, String url) {
        return response.headers().firstValue("Content-Disposition")
                .map(cd -> cd.contains("filename=") ? cd.split("filename=")[1].replace("\"", "").split(";")[0] : null)
                .orElseGet(() -> {
                    String[] parts = url.split("/");
                    return parts[parts.length - 1].split("\\?")[0];
                });
    }
}