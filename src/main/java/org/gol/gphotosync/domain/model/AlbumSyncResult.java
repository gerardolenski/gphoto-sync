package org.gol.gphotosync.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@Builder
public class AlbumSyncResult {
    private final String title;
    private final long imagesCount;
    private final long missingImages;
    private final Map<String, Long> uploadStats;
}
