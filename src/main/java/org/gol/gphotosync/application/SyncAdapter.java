package org.gol.gphotosync.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.gol.gphotosync.domain.local.AlbumFindQuery;
import org.gol.gphotosync.domain.local.LocalLibraryPort;
import org.gol.gphotosync.domain.local.filter.LocalAlbumFilterFactory;
import org.gol.gphotosync.domain.model.AlbumSyncResult;
import org.gol.gphotosync.domain.sync.SyncPort;
import org.gol.gphotosync.domain.sync.Synchronizer;
import org.gol.gphotosync.domain.sync.album.AlbumSynchronizerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.vavr.control.Try.withResources;
import static java.util.Comparator.comparing;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncAdapter implements SyncPort {

    private final LocalLibraryPort localLibrary;
    private final ObjectFactory<AlbumSynchronizerFactory> albumSynchronizerFactoryProvider;
    private final LocalAlbumFilterProperties filterProperties;

    @Override
    public SyncResult sync() {
        log.info("Synchronizing library ...");
        var watch = StopWatch.create();
        watch.start();

        var result = syncLibrary();

        watch.stop();
        log.info("Synchronization took: {}", watch.formatTime());
        return result;
    }

    private SyncResult syncLibrary() {
        return withResources(albumSynchronizerFactoryProvider::getObject)
                .of(syncFactory -> fireTasks(syncFactory)
                        .stream()
                        .map(CompletableFuture::join)
                        .sorted(comparing(AlbumSyncResult::title))
                        .reduce(new SyncResult(), SyncResult::addAlbumSyncResult, (r1, r2) -> r1))
                .get();
    }

    private List<CompletableFuture<AlbumSyncResult>> fireTasks(AlbumSynchronizerFactory albumSynchronizerFactory) {
        var filters = LocalAlbumFilterFactory.getByYearFilters(filterProperties.getFromYear(), filterProperties.getToYear());
        var query = new AlbumFindQuery(filters);
        return localLibrary.findAlbums(query).stream()
                .map(albumSynchronizerFactory::getSynchronizer)
                .map(Synchronizer::invoke)
                .toList();
    }
}
