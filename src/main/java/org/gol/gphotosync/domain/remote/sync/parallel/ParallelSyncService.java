package org.gol.gphotosync.domain.remote.sync.parallel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.model.LocalAlbum;
import org.gol.gphotosync.domain.remote.sync.model.AlbumSyncResult;
import org.gol.gphotosync.domain.remote.RemoteSyncService;
import org.gol.gphotosync.domain.remote.sync.model.AlbumSyncCmd;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Comparator.comparing;

/**
 * Implementation of Domain Service which invokes albums synchronization in parallel.
 */
@Slf4j
@RequiredArgsConstructor
class ParallelSyncService implements RemoteSyncService {

    private final ParallelAlbumSynchronizerFactory albumSyncFactory;

    @Override
    public List<AlbumSyncResult> sync(AlbumSyncCmd cmd) {
        var albums = cmd.localAlbums();
        log.info("Synchronizing albums library: albumsSize={}, albumsTitles={}",
                albums.size(),
                albums.stream()
                        .map(LocalAlbum::getTitle)
                        .toList());
        var syncResults = syncLibrary(cmd.localAlbums());
        log.info("Albums library synchronization was finished");
        return syncResults;

    }

    private List<AlbumSyncResult> syncLibrary(List<LocalAlbum> albums) {
        return fireTasks(albums)
                .stream()
                .map(CompletableFuture::join)
                .sorted(comparing(AlbumSyncResult::title))
                .toList();
    }

    private List<CompletableFuture<AlbumSyncResult>> fireTasks(List<LocalAlbum> albums) {
        return albums.stream()
                .map(albumSyncFactory::getInstance)
                .map(ParallelAlbumSynchronizer::invoke)
                .toList();
    }
}
