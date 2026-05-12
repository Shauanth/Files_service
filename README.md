# Files Service 🚀

**Files Service** es un microservicio desarrollado en **Spring Boot** que actúa como un puente de transferencia de archivos entre sistemas. Permite descargar archivos desde múltiples fuentes (incluyendo Google Drive) y subirlos a servidores destino, todo mediante codificación en **Base64**.

---

## 📋 Tabla de Contenidos

- [Características](#características)
- [Punto de Inicio](#punto-de-inicio)
- [Guías Disponibles](#guías-disponibles)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Endpoints](#endpoints)
- [Ejemplos de Uso](#ejemplos-de-uso)
- [Despliegue](#despliegue)
- [Estructura del Proyecto](#estructura-del-proyecto)

---

## 🎯 Punto de Inicio

**¿No sabes por dónde empezar?** Lee [INICIO_AQUI.md](INICIO_AQUI.md)

Esta guía te ayudará a encontrar exactamente lo que necesitas:
- 🚀 Publicar el servicio en GitHub
- 📦 Usar como dependencia en otro proyecto
- 🔐 Configurar credenciales de forma segura
- 💡 Ver ejemplos de código
- ✅ Checklist para seguir paso a paso

---

## ✨ Características

- ✅ **Descarga múltiple de archivos** desde URLs y Google Drive
- ✅ **Soporte para Google Drive** con extracción automática de nombres de archivo
- ✅ **Headers personalizados** (Authorization, x-api-key, etc.)
- ✅ **Codificación Base64** para transferencia segura
- ✅ **Carga de archivos** a servidores destino
- ✅ **Manejo robusto de errores** con logging detallado
- ✅ **API RESTful** con respuestas estructuradas

---

## 🔧 Requisitos Previos

- **Java 17** o superior
- **Maven 3.6+**
- **Git**
- Para Google Drive: **Token de autenticación** de Google Drive API

---

## 📦 Instalación y Configuración

### Opción A: Ejecutar el Servicio Directamente

#### 1. Clonar el repositorio

```bash
git clone https://github.com/TU_USUARIO/Files_Service.git
cd Files_Service
```

#### 2. Configurar propiedades de la aplicación

Edita `src/main/resources/application.properties`:

```properties
# Puerto de la aplicación
server.port=8080

# Nombre de la aplicación
spring.application.name=Files_Service

# Logging
logging.level.shauanth.dev.files_service=INFO
logging.level.org.springframework.web=WARN
```

#### 3. Compilar el proyecto

```bash
mvn clean install
```

#### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

---

## 📚 Guías Disponibles

### [🚀 QUICKSTART.md](QUICKSTART.md) - Guía Rápida (5 minutos)
Publicar el servicio en GitHub Packages y usarlo en tus proyectos en 5 pasos simples.

### [🔐 CONFIGURACION_SEGURA.md](CONFIGURACION_SEGURA.md) - Configuración de Seguridad
Cómo configurar credenciales de forma segura sin exponer tokens:
- Variables de entorno
- Archivo settings.xml con placeholders
- GitHub CLI (recomendado)
- Archivo .netrc

### [💡 EJEMPLOS_DE_USO.md](EJEMPLOS_DE_USO.md) - Ejemplos de Código
Ejemplos prácticos de cómo usar Files Service en tus proyectos:
- Controlador simple
- Servicio wrapper
- Endpoints REST
- Tests unitarios
- Manejo de errores
- Configuración segura

---

### Opción B: Usar como Dependencia en Otro Proyecto

#### 1. Configurar acceso a GitHub Packages

**Crear o editar `~/.m2/settings.xml`:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.1.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">
  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/TU_USUARIO/Files_Service</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>TU_USUARIO_GITHUB</username>
      <password>TU_TOKEN_GITHUB</password>
    </server>
  </servers>
</settings>
```

**Generar Token de GitHub:**
1. Ve a [GitHub Settings > Developer settings > Personal access tokens](https://github.com/settings/tokens)
2. Click en "Generate new token (classic)"
3. Dale permisos: `read:packages`
4. Copia el token y úsalo en el `settings.xml`

#### 2. Agregar dependencia a tu proyecto

En tu `pom.xml`:

```xml
<repository>
  <id>github</id>
  <url>https://maven.pkg.github.com/TU_USUARIO/Files_Service</url>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</repository>
```

Luego agrega la dependencia:

```xml
<dependency>
  <groupId>io.github.shauanth</groupId>
  <artifactId>files-service</artifactId>
  <version>1.0.0</version>
</dependency>
```

#### 3. Usar en tu código

```java
import org.springframework.beans.factory.annotation.Autowired;
import shauanth.dev.files_service.Service.TransferService;
import shauanth.dev.files_service.DTO.FileData;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class MyController {

    @Autowired
    private TransferService transferService;

    @PostMapping("/download")
    public ResponseEntity<List<FileData>> downloadFiles(@RequestBody List<String> urls) {
        Map<String, String> headers = Map.of(
            "Authorization", "Bearer mi_token"
        );
        List<FileData> files = transferService.downloadAsBase64Batch(urls, headers);
        return ResponseEntity.ok(files);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(
            @RequestBody List<FileData> files,
            @RequestParam String targetUrl) {
        Map<String, String> headers = Map.of(
            "Authorization", "Bearer token_destino"
        );
        List<String> responses = transferService.uploadBase64Batch(files, targetUrl, headers);
        return ResponseEntity.ok("Archivos cargados: " + responses.size());
    }
}
```

#### 4. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

---

## 🔌 Endpoints

### **Endpoint 1: Descargar Archivos (`/download-to-pc`)**

**Descripción:** Descarga uno o múltiples archivos desde URLs remotas (incluyendo Google Drive) y los convierte a Base64.

**Método HTTP:** `POST`

**URL:** `/api/v1/bridge/download-to-pc`

**Content-Type:** `application/json`

#### Estructura de la Solicitud

```json
{
  "urls": [
    "https://ejemplo.com/archivo1.pdf",
    "https://drive.google.com/file/d/ARCHIVO_ID_GOOGLE/view?usp=sharing"
  ],
  "headers": {
    "Authorization": "Bearer token_aqui",
    "x-api-key": "api_key_opcional"
  }
}
```

#### Descripción de Campos

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `urls` | `Array<String>` | ✅ Sí | Lista de URLs de archivos a descargar |
| `headers` | `Map<String,String>` | ❌ No | Headers personalizados (ej: Authorization, x-api-key) |

#### Estructura de la Respuesta (Éxito - 200)

```json
{
  "status": "SUCCESS",
  "data": [
    {
      "fileName": "documento.pdf",
      "base64Content": "JVBERi0xLjQKJeLj...",
      "contentType": "application/pdf"
    },
    {
      "fileName": "imagen.jpg",
      "base64Content": "/9j/4AAQSkZJRgABA...",
      "contentType": "image/jpeg"
    }
  ]
}
```

#### Descripción de la Respuesta

| Campo | Descripción |
|-------|-------------|
| `status` | Estado de la operación: `SUCCESS` si fue exitosa |
| `data` | Array con los archivos descargados |
| `data[].fileName` | Nombre original del archivo |
| `data[].base64Content` | Contenido del archivo codificado en Base64 |
| `data[].contentType` | Tipo MIME del archivo (ej: `application/pdf`) |

#### Casos de Error

| Status | Descripción |
|--------|-------------|
| 400 | Solicitud mal formada |
| 500 | Error interno del servidor |

**Ejemplo de respuesta de error:**

```json
{
  "status": "ERROR",
  "data": "Error en la descarga o procesamiento del archivo"
}
```

#### Notas Especiales

- ✅ Soporta **URLs directas** de archivos
- ✅ Soporta **Google Drive** con extracción automática del nombre real
- ⏱️ Timeout máximo: **30 segundos** por archivo
- 📤 Los headers personalizados se envían a todas las URLs

---

### **Endpoint 2: Cargar Archivos (`/upload-from-pc`)**

**Descripción:** Carga uno o múltiples archivos (en Base64) a un servidor destino.

**Método HTTP:** `POST`

**URL:** `/api/v1/bridge/upload-from-pc`

**Content-Type:** `application/json`

#### Estructura de la Solicitud

```json
{
  "files": [
    {
      "fileName": "documento.pdf",
      "base64Content": "JVBERi0xLjQKJeLj...",
      "contentType": "application/pdf"
    },
    {
      "fileName": "imagen.jpg",
      "base64Content": "/9j/4AAQSkZJRgABA...",
      "contentType": "image/jpeg"
    }
  ],
  "targetUrl": "https://api.servidor.com/upload",
  "headers": {
    "Authorization": "Bearer token_destino",
    "x-api-key": "api_key_destino",
    "Content-Type": "application/json"
  }
}
```

#### Descripción de Campos

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `files` | `Array<FileData>` | ✅ Sí | Lista de archivos a cargar |
| `files[].fileName` | `String` | ✅ Sí | Nombre del archivo |
| `files[].base64Content` | `String` | ✅ Sí | Contenido en Base64 |
| `files[].contentType` | `String` | ✅ Sí | Tipo MIME del archivo |
| `targetUrl` | `String` | ✅ Sí | URL del servidor destino |
| `headers` | `Map<String,String>` | ❌ No | Headers personalizados para el destino |

#### Estructura de la Respuesta (Éxito - 200)

```json
{
  "status": "COMPLETED",
  "data": [
    "Archivo cargado exitosamente: documento.pdf",
    "Archivo cargado exitosamente: imagen.jpg"
  ]
}
```

#### Descripción de la Respuesta

| Campo | Descripción |
|-------|-------------|
| `status` | Estado: `COMPLETED` si se completó, `ERROR` si falló |
| `data` | Array con mensajes de estado para cada archivo |

#### Casos de Error

| Condición | Status HTTP | Respuesta |
|-----------|-------------|----------|
| Falta `files` o `targetUrl` | 400 | `{"status": "ERROR", "data": "Faltan archivos o targetUrl"}` |
| Error en la carga | 200 | `{"status": "COMPLETED", "data": ["Error al cargar archivo"]}` |
| Servidor destino no disponible | 500 | Error interno |

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Descargar desde URL Directa

```bash
curl -X POST http://localhost:8080/api/v1/bridge/download-to-pc \
  -H "Content-Type: application/json" \
  -d '{
    "urls": ["https://ejemplo.com/archivo.pdf"],
    "headers": {
      "Authorization": "Bearer mi_token"
    }
  }'
```

**Respuesta:**

```json
{
  "status": "SUCCESS",
  "data": [
    {
      "fileName": "archivo.pdf",
      "base64Content": "JVBERi0xLjQKJeLj...",
      "contentType": "application/pdf"
    }
  ]
}
```

---

### Ejemplo 2: Descargar desde Google Drive

```bash
curl -X POST http://localhost:8080/api/v1/bridge/download-to-pc \
  -H "Content-Type: application/json" \
  -d '{
    "urls": ["https://drive.google.com/file/d/1a2b3c4d5e6f7g8h9i0j/view?usp=sharing"],
    "headers": {
      "Authorization": "Bearer google_token"
    }
  }'
```

**Respuesta (extrae automáticamente el nombre):**

```json
{
  "status": "SUCCESS",
  "data": [
    {
      "fileName": "Mi_Documento_Importante",
      "base64Content": "JVBERi0xLjQKJeLj...",
      "contentType": "application/pdf"
    }
  ]
}
```

---

### Ejemplo 3: Cargar Múltiples Archivos

```bash
curl -X POST http://localhost:8080/api/v1/bridge/upload-from-pc \
  -H "Content-Type: application/json" \
  -d '{
    "files": [
      {
        "fileName": "documento.pdf",
        "base64Content": "JVBERi0xLjQKJeLj...",
        "contentType": "application/pdf"
      },
      {
        "fileName": "imagen.png",
        "base64Content": "iVBORw0KGgoAAAA...",
        "contentType": "image/png"
      }
    ],
    "targetUrl": "https://api.servidor.com/guardar-archivos",
    "headers": {
      "Authorization": "Bearer token_destino",
      "x-client-id": "mi_cliente"
    }
  }'
```

**Respuesta:**

```json
{
  "status": "COMPLETED",
  "data": [
    "Archivo cargado exitosamente",
    "Archivo cargado exitosamente"
  ]
}
```

---

## 🚀 Despliegue

### Opción 1: Despliegue Local

```bash
# Compilar
mvn clean package

# Ejecutar JAR
java -jar target/Files_Service-0.0.1-SNAPSHOT.jar
```

### Opción 2: Docker

**Crear `Dockerfile`:**

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/Files_Service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
```

**Construir y ejecutar:**

```bash
docker build -t files-service:1.0 .
docker run -p 8080:8080 files-service:1.0
```

---

### Opción 3: GitHub

#### Configuración de `pom.xml` para GitHub Packages

```xml
<distributionManagement>
  <repository>
    <id>github</id>
    <name>GitHub Packages</name>
    <url>https://maven.pkg.github.com/TU_USUARIO/Files_Service</url>
  </repository>
</distributionManagement>
```

#### Variables de entorno en GitHub Actions

```yaml
GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

#### Comando de publicación

```bash
mvn deploy -DskipTests
```

---

### Opción 4: Maven Central

#### Requisitos previos

1. Crear cuenta en [JIRA](https://issues.sonatype.org)
2. Crear ticket para nuevo proyecto
3. Configurar GPG para firmar

#### Configuración en `pom.xml`

```xml
<distributionManagement>
  <snapshotRepository>
    <id>ossrh</id>
    <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
  </snapshotRepository>
  <repository>
    <id>ossrh</id>
    <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
  </repository>
</distributionManagement>
```

#### Comando de publicación

```bash
mvn clean deploy -P release -DskipTests
```

---

### Opción 5: Artifactory (JFrog)

#### Configuración en `pom.xml`

```xml
<distributionManagement>
  <repository>
    <id>central</id>
    <name>Artifactory-releases</name>
    <url>https://tu-dominio.jfrog.io/artifactory/libs-release</url>
  </repository>
  <snapshotRepository>
    <id>snapshots</id>
    <name>Artifactory-snapshots</name>
    <url>https://tu-dominio.jfrog.io/artifactory/libs-snapshot</url>
  </snapshotRepository>
</distributionManagement>
```

#### Comando de publicación

```bash
mvn deploy
```

---

### Opción 6: GitLab

#### Configuración en `pom.xml`

```xml
<distributionManagement>
  <repository>
    <id>gitlab-maven</id>
    <url>https://gitlab.com/api/v4/projects/ID_PROYECTO/packages/maven</url>
  </repository>
</distributionManagement>
```

#### Variables de entorno

```bash
CI_JOB_TOKEN (proporcionado por GitLab)
```

---

### Opción 7: AWS CodeArtifact

#### Configuración

```bash
aws codeartifact login --tool maven --domain tu-dominio --domain-owner 123456789012 --region us-east-1
```

#### `pom.xml`

```xml
<distributionManagement>
  <repository>
    <id>codeartifact</id>
    <name>CodeArtifact</name>
    <url>https://tu-dominio-123456789012.d.codeartifact.us-east-1.amazonaws.com/maven/tu-repo/</url>
  </repository>
</distributionManagement>
```

---

### Opción 8: Heroku

#### `Procfile`

```
web: java $JAVA_OPTS -jar target/Files_Service-0.0.1-SNAPSHOT.jar -Dserver.port=$PORT
```

#### Comandos de despliegue

```bash
heroku login
heroku create tu-app-name
heroku config:set JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
git push heroku main
```

---

## 📁 Estructura del Proyecto

```
Files_Service/
├── src/
│   ├── main/
│   │   ├── java/shauanth/dev/files_service/
│   │   │   ├── FilesServiceApplication.java (Clase principal)
│   │   │   ├── Controller/
│   │   │   │   └── TransferController.java (Endpoints)
│   │   │   ├── Service/
│   │   │   │   └── TransferService.java (Lógica de negocio)
│   │   │   └── DTO/
│   │   │       ├── FileData.java
│   │   │       ├── MultiFileRequest.java
│   │   │       └── TransferResponse.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/shauanth/dev/files_service/
│           └── FilesServiceApplicationTests.java
├── pom.xml
├── README.md
└── .gitignore
```

---

## 📊 Especificaciones Técnicas

| Componente | Versión |
|-----------|---------|
| Java | 17+ |
| Spring Boot | 3.5.14 |
| Maven | 3.6+ |
| Lombok | 1.18.x |
| HttpClient | Java 11+ (nativo) |

---

## 🔐 Consideraciones de Seguridad

- ⚠️ **Nunca** commits tokens o credenciales en el repositorio
- ✅ Usa variables de entorno para datos sensibles
- ✅ Implementa HTTPS en producción
- ✅ Valida y sanitiza las URLs de entrada
- ✅ Implementa rate limiting en endpoints
- ✅ Usa JWT o OAuth2 para autenticación

---

## 📝 Licencia

Este proyecto está bajo licencia MIT.

---

## 👨‍💻 Autor

**Shauanth.dev**

---

## 💬 Soporte

Para reportar bugs o sugerencias, abre un **Issue** en el repositorio.