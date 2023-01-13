package org.gol.gphotosync.domain.remote;

import org.gol.gphotosync.domain.local.model.LocalAlbum;
import org.gol.gphotosync.domain.remote.sync.model.AlbumSyncCmd;
import org.gol.gphotosync.domain.remote.sync.model.AlbumSyncResult;

import java.util.List;

/**
 * Holds contract for Domain Service synchronizing local album collection to remote library.
 */
public interface RemoteSyncService {

    /**
     * Performs local albums synchronization to remote library.
     *
     * @param cmd command containing {@link LocalAlbum} collection
     * @return the list of synchronization results, each for one album
     */
    List<AlbumSyncResult> sync(AlbumSyncCmd cmd);
}
