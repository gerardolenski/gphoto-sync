package org.gol.photosync.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.io.File;

@With
@Getter
@Builder
@ToString
public class LocalImage {
    private final String fileName;
    private final String mimeType;
    private final File file;
    private final String description;
}
