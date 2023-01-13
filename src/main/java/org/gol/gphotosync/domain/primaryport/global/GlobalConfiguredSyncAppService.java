package org.gol.gphotosync.domain.primaryport.global;

import lombok.RequiredArgsConstructor;
import org.gol.gphotosync.domain.local.model.AlbumFindQuery;
import org.gol.gphotosync.domain.local.LocalLibraryQueryPort;
import org.gol.gphotosync.domain.primaryport.GlobalConfiguredLibrarySyncPort;
import org.gol.gphotosync.domain.primaryport.model.LibrarySyncResult;
import org.gol.gphotosync.domain.config.LocalAlbumFiltersConfigPort;
import org.gol.gphotosync.domain.remote.RemoteSyncService;
import org.gol.gphotosync.domain.remote.sync.model.AlbumSyncCmd;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.time.StopWatch.createStarted;
import static org.gol.gphotosync.domain.local.filter.LocalAlbumFilterFactory.getByYearFilters;

/**
 * Application service invoking full synchronization flow.
 */
@Service
@RequiredArgsConstructor
class GlobalConfiguredSyncAppService implements GlobalConfiguredLibrarySyncPort {

    private final LocalLibraryQueryPort localLibraryQueryPort;
    private final RemoteSyncService remoteSyncService;
    private final LocalAlbumFiltersConfigPort filterPropertyPort;

    /**
     * Runs synchronization flow using global application configuration of local library filters.
     *
     * @return synchronization result
     */

    @Override
    public LibrarySyncResult runSyncFlow() {
        var watch = createStarted();
        var filters = getByYearFilters(filterPropertyPort.fromYear(), filterPropertyPort.toYear());
        var albums = localLibraryQueryPort.findAlbums(new AlbumFindQuery(filters));
        var albumSyncResults = remoteSyncService.sync(new AlbumSyncCmd(albums));
        watch.stop();
        return new LibrarySyncResult(watch.getTime(), albumSyncResults);
    }
}
