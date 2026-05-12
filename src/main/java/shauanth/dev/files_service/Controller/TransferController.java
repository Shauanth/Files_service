package shauanth.dev.files_service.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shauanth.dev.files_service.DTO.FileData;
import shauanth.dev.files_service.DTO.MultiFileRequest;
import shauanth.dev.files_service.DTO.TransferResponse;
import shauanth.dev.files_service.Service.TransferService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bridge")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    // Tu PC llama aquí para obtener los archivos y firmarlos
    @PostMapping("/download-to-pc")
    public ResponseEntity<TransferResponse> toPc(@RequestBody MultiFileRequest request) {
        // Ahora pasamos los headers que vienen en el JSON
        List<FileData> files = transferService.downloadAsBase64Batch(request.getUrls(), request.getHeaders());
        return ResponseEntity.ok(TransferResponse.builder().status("SUCCESS").data(files).build());
    }

    @PostMapping("/upload-from-pc")
    public ResponseEntity<TransferResponse> fromPc(@RequestBody MultiFileRequest request) {
        if (request.getFiles() == null || request.getTargetUrl() == null) {
            return ResponseEntity.badRequest().body(TransferResponse.builder()
                    .status("ERROR")
                    .data("Faltan archivos o targetUrl")
                    .build());
        }

        // Pasamos la lista, la URL y los headers dinámicos
        List<String> responses = transferService.uploadBase64Batch(
                request.getFiles(),
                request.getTargetUrl(),
                request.getHeaders()
        );

        return ResponseEntity.ok(TransferResponse.builder()
                .status("COMPLETED")
                .data(responses)
                .build());
    }
}