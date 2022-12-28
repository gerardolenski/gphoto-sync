package org.gol.gphotosync.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.gol.gphotosync.domain.local.LocalLibraryPort;
import org.gol.gphotosync.domain.model.AlbumSyncResult;
import org.gol.gphotosync.domain.sync.SyncPort;
import org.gol.gphotosync.domain.sync.Synchronizer;
import org.gol.gphotosync.domain.sync.album.AlbumSynchronizerFactory;
import org.gol.gphotosync.domain.util.AsyncUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncAdapter implements SyncPort {

    private final LocalLibraryPort localLibrary;
    private final ObjectFactory<AlbumSynchronizerFactory> albumSynchronizerFactoryProvider;

    @Override
    public SyncResult sync() {
        log.info("Synchronizing library ...");
        var watch = StopWatch.create();
        watch.start();

        var result = syncLibrary(albumSynchronizerFactoryProvider.getObject());

        watch.stop();
        log.info("Synchronization took: {}", watch.formatTime());
        return result;
    }

    private SyncResult syncLibrary(AlbumSynchronizerFactory albumSynchronizerFactory) {
        var result = localLibrary.findAlbums().stream()
                .map(albumSynchronizerFactory::getSynchronizer)
                .map(Synchronizer::invoke)
                .toList().stream()
                .map(AsyncUtils::getFutureResult)
                .sorted(comparing(AlbumSyncResult::title))
                .reduce(new SyncResult(), SyncResult::addAlbumSyncResult, (r1, r2) -> r1);
        albumSynchronizerFactory.shutdown();
        return result;
    }
}
