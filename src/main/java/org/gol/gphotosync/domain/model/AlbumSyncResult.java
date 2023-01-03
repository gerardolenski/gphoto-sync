package org.gol.gphotosync.domain.model;

import lombok.Builder;

import java.util.List;

@Builder
public record AlbumSyncResult(
        String title,
        long imagesCount,
        long missingImages,
        List<UploadStat> uploadStats,
        boolean syncInterrupted,
        String syncInterruptionMessage) {
}
