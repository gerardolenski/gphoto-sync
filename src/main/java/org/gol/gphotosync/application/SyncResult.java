package org.gol.gphotosync.application;

import org.gol.gphotosync.domain.model.AlbumSyncResult;

import java.util.ArrayList;
import java.util.List;

public record SyncResult(List<AlbumSyncResult> albumSyncResults) {

    public SyncResult() {
        this(new ArrayList<>());
    }

    public SyncResult addAlbumSyncResult(AlbumSyncResult albumSyncResult) {
        albumSyncResults.add(albumSyncResult);
        return this;
    }
}
