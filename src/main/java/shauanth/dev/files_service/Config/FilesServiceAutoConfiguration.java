package shauanth.dev.files_service.Config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import shauanth.dev.files_service.Service.TransferService;

@AutoConfiguration
@ComponentScan(basePackageClasses = TransferService.class)
public class FilesServiceAutoConfiguration {
}
