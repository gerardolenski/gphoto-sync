package org.gol.gphotosync.infrastructure.library.filesystem;

import lombok.Builder;
import org.gol.gphotosync.domain.local.LocalImage;

import java.io.File;

/**
 * DTO working on local file system.
 */
@Builder
record FileSystemImage(
        String fileName,
        String mimeType,
        File file,
        String description) implements LocalImage {

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
