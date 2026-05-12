package shauanth.dev.files_service.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferResponse {
    private String status;
    private Object data;
}