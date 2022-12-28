package org.gol.gphotosync.domain.model;

import lombok.Builder;
import lombok.With;

import java.io.File;

@With
@Builder
public record LocalImage(
        String fileName,
        String mimeType,
        File file,
        String description) {
}
