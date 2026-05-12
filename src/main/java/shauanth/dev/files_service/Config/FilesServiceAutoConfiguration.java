package shauanth.dev.files_service.Config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import shauanth.dev.files_service.Service.TransferService;

@AutoConfiguration
@Import(TransferService.class)
public class FilesServiceAutoConfiguration {
}
