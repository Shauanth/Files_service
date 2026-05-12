# Files_Service

Proyecto Spring Boot para subir/descargar archivos desde repositorios en la nube (S3, OneDrive, Google Drive, etc.).

Requisitos
- JDK 17+
- Maven 3.8+

Instrucciones rápidas (Windows PowerShell)

1) Verifica versión de Java y Maven:
```powershell
java -version
mvn -v
```

2) Si `java -version` muestra menor que 17, instala JDK 17 (Adoptium/Temurin, Oracle, Azul). A modo de ejemplo con AdoptOpenJDK/Adoptium:
- Descarga e instala JDK 17 desde https://adoptium.net/
- Establece `JAVA_HOME` (ejemplo):
```powershell
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17"
```
Cierra y vuelve a abrir la terminal.

3) Compilar el proyecto:
```powershell
cd "C:\Users\FABRICIO\Desktop\Pord\Files_Service"
mvn -DskipTests package
```

4) Ejecutar la aplicación:
```powershell
java -jar target\Files_Service-0.0.1-SNAPSHOT.jar
```

Notas sobre el servicio
- `TransferService` provee métodos universales `downloadAsBase64Batch` y `uploadBase64Batch`.
- Se aplican heurísticos para Google Drive (extraer ID), OneDrive (añadir `download=1`) y detección de nombre de archivo desde `Content-Disposition` o la URL.

Siguientes pasos recomendados
- Añadir manejo específico para S3 (presigned URLs, firmas si es necesario).
- Añadir tests de integración contra endpoints mock.
- Proporcionar ejemplos `curl` para `POST /api/v1/bridge/download-to-pc` y `/upload-from-pc`.

Si quieres, puedo agregar soporte explícito para S3/OneDrive/Google Drive (firmas, refresh tokens), o intentar compilar aquí después de que actualices tu JDK a 17.