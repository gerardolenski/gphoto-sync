package org.gol.gphotosync.domain.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record AlbumSyncResult(
        String title,
        long imagesCount,
        long missingImages,
        Map<String, Long> uploadStats,
        boolean syncInterrupted,
        String syncInterruptionMessage) {
}
