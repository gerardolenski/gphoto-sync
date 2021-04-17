package org.gol.photosync.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@Builder
public class LocalAlbum {
    private final String title;
    private final Path path;
}
