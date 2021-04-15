package org.gol.photosync.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

@Getter
@Builder
public class LocalAlbum {
    private final String title;
    private final Path path;
    private final List<LocalImage> images;
}
