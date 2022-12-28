package org.gol.gphotosync.domain.model;

import lombok.Builder;

import java.nio.file.Path;

@Builder
public record LocalAlbum(
        String title,
        Path path) {
}
