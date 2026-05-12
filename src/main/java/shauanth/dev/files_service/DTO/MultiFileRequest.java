package shauanth.dev.files_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiFileRequest {
    private List<String> urls;
    private List<FileData> files;
    private String targetUrl;
    private Map<String, String> headers; // <-- Para Authorization, x-api-key, etc.
}
