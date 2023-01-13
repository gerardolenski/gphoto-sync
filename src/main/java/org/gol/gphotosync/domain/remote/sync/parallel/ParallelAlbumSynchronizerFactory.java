package org.gol.gphotosync.domain.remote.sync.parallel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.model.LocalAlbum;
import org.gol.gphotosync.domain.remote.RemoteAlbumService;
import org.gol.gphotosync.domain.remote.RemoteImageService;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Factory of {@link ParallelAlbumSynchronizer} aggregates.
 */
@Slf4j
class ParallelAlbumSynchronizerFactory {

    private final RemoteAlbumService remoteAlbumService;
    private final RemoteImageService remoteImageService;
    private final ExecutorService albumSyncExecutor;
    private final ExecutorService uploadExecutor;
    private final int uploadBulkSize;

    public ParallelAlbumSynchronizerFactory(
            RemoteAlbumService remoteAlbumService,
            RemoteImageService remoteImageService,
            ParallelSyncProperties syncProperties) {
        log.debug("Init AlbumSynchronizerFactory: syncProperties={}", syncProperties);
        this.remoteAlbumService = remoteAlbumService;
        this.remoteImageService = remoteImageService;
        this.uploadBulkSize = syncProperties.uploadBulkSize();
        this.albumSyncExecutor = newFixedThreadPool(syncProperties.albumsConcurrency(),
                new ThreadFactoryBuilder()
                        .setNameFormat("album-pool-%d")
                        .build());
        this.uploadExecutor = newFixedThreadPool(syncProperties.uploadConcurrency(),
                new ThreadFactoryBuilder()
                        .setNameFormat("upload-pool-%d")
                        .build());
    }

    /**
     * Creates new instance of synchronizer for given local album.
     */
    public ParallelAlbumSynchronizer getInstance(LocalAlbum localAlbum) {
        return new ParallelAlbumSynchronizer(localAlbum,
                remoteAlbumService,
                remoteImageService,
                uploadBulkSize,
                albumSyncExecutor,
                uploadExecutor);
    }

    @PreDestroy
    void cleanup() {
        log.info("Shutdown executor services ...");
        albumSyncExecutor.shutdown();
        uploadExecutor.shutdown();
    }
}
