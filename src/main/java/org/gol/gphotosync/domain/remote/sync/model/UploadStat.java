package org.gol.gphotosync.domain.remote.sync.model;

/**
 * Value Object holding data of uploading statistics.
 */
public record UploadStat(String status, long count) {
}
