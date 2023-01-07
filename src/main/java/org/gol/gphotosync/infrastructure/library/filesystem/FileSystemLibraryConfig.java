package org.gol.gphotosync.infrastructure.library.filesystem;

import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.LocalLibraryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of file system library components.
 */
@Slf4j
@Configuration
class FileSystemLibraryConfig {

    @Bean
    LocalLibraryPort fileSystemLibraryAdapter(FileSystemLibraryProperties libraryProperties) {
        log.info("Initializing FileSystemLibraryAdapter with parameters: {}", libraryProperties);
        return new FileSystemLibraryAdapter(libraryProperties.path());
    }
}
