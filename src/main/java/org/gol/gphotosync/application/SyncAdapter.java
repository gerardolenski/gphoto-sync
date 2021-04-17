package org.gol.gphotosync.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.gol.gphotosync.domain.local.LocalLibraryPort;
import org.gol.gphotosync.domain.model.AlbumSyncResult;
import org.gol.gphotosync.domain.sync.SyncPort;
import org.gol.gphotosync.domain.sync.Synchronizer;
import org.gol.gphotosync.domain.sync.album.AlbumSynchronizerFactory;
import org.gol.gphotosync.domain.util.LoggerUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

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
                .collect(toList())
                .stream()
                .map(this::getFutureResult)
                .sorted(comparing(AlbumSyncResult::getTitle))
                .reduce(new SyncResult(), SyncResult::addAlbumSyncResult, (r1, r2) -> r1);
        albumSynchronizerFactory.shutdown();
        return result;
    }

    private <T> T getFutureResult(Future<T> future) {
        return Try.of(future::get)
                .onFailure(e -> log.error("Error occurred while retrieving future result: cause={}", LoggerUtils.formatEx(e)))
                .get();
    }
}
