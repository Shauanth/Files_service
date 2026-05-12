package shauanth.dev.files_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileData {
    private String fileName;
    private String base64Content;
    private String contentType;
}

