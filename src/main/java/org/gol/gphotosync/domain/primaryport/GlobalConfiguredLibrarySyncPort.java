package org.gol.gphotosync.domain.primaryport;

import org.gol.gphotosync.domain.local.LocalAlbumFilter;
import org.gol.gphotosync.domain.primaryport.model.LibrarySyncResult;

/**
 * Primary port which invokes local-to-remote synchronization using global filter configuration of application.
 */
public interface GlobalConfiguredLibrarySyncPort {

    /**
     * Finds the local albums using global configured filters (see: {@link LocalAlbumFilter}) and synchronizes with
     * remote album library.
     *
     * @return the synchronization result
     */
    LibrarySyncResult runSyncFlow();
}
