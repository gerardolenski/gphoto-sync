package org.gol.gphotosync.application;

import org.gol.gphotosync.domain.model.AlbumSyncResult;

import java.util.List;
import java.util.stream.Stream;

import static java.util.List.copyOf;
import static java.util.stream.Stream.concat;

public record SyncResult(List<AlbumSyncResult> albumSyncResults) {

    public SyncResult() {
        this(List.of());
    }

    public SyncResult(List<AlbumSyncResult> albumSyncResults) {
        this.albumSyncResults = copyOf(albumSyncResults);
    }

    public SyncResult addAlbumSyncResult(AlbumSyncResult albumSyncResult) {
        var results = concat(this.albumSyncResults.stream(), Stream.of(albumSyncResult))
                .toList();
        return new SyncResult(results);
    }
}
