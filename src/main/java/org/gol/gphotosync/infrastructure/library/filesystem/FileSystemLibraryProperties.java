package org.gol.gphotosync.infrastructure.library.filesystem;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * Holding properties for library on local file system.
 */
@ConfigurationProperties(prefix = "gphotosync.library")
record FileSystemLibraryProperties(Path path) {

}
