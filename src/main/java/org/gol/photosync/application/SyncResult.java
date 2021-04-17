package org.gol.photosync.application;

import lombok.Getter;
import lombok.ToString;
import org.gol.photosync.domain.model.AlbumSyncResult;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class SyncResult {
    private final List<AlbumSyncResult> albumSyncResults = new ArrayList<>();

    public SyncResult addAlbumSyncResult(AlbumSyncResult albumSyncResult) {
        albumSyncResults.add(albumSyncResult);
        return this;
    }
}
