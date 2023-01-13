package org.gol.gphotosync.domain.remote.sync.model;

import lombok.Builder;

import java.util.List;

/**
 * Value Object holding information of single album synchronization result.
 */
@Builder
public record AlbumSyncResult(
        String title,
        long imagesCount,
        long missingImages,
        List<UploadStat> uploadStats,
        boolean syncInterrupted,
        String syncInterruptionMessage) {
}
