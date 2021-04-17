package org.gol.photosync.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.gol.photosync.domain.library.LocalLibrary;
import org.gol.photosync.domain.model.AlbumSyncResult;
import org.gol.photosync.domain.sync.SyncPort;
import org.gol.photosync.domain.sync.Synchronizer;
import org.gol.photosync.domain.sync.album.AlbumSynchronizerFactory;
import org.gol.photosync.domain.util.LoggerUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncAdapter implements SyncPort {

    private final LocalLibrary localLibrary;
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
                .map(localLibrary::getAlbum)
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
