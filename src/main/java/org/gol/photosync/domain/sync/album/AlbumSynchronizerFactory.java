package org.gol.photosync.domain.sync.album;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.gol.photosync.domain.google.album.AlbumOperation;
import org.gol.photosync.domain.google.media.ImageOperation;
import org.gol.photosync.domain.library.LocalLibraryPort;
import org.gol.photosync.domain.model.AlbumSyncResult;
import org.gol.photosync.domain.model.LocalAlbum;
import org.gol.photosync.domain.sync.SyncProperties;
import org.gol.photosync.domain.sync.Synchronizer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Slf4j
@Service
@Scope(SCOPE_PROTOTYPE)
public class AlbumSynchronizerFactory {

    private final AlbumOperation albumOperation;
    private final ImageOperation imageOperation;
    private final LocalLibraryPort localLibraryOperation;
    private final SyncProperties syncProperties;
    private final ExecutorService albumSyncExecutor;
    private final ExecutorService uploadExecutor;

    public AlbumSynchronizerFactory(
            AlbumOperation albumOperation,
            ImageOperation imageOperation,
            LocalLibraryPort localLibraryOperation,
            SyncProperties syncProperties) {
        log.debug("Init AlbumSynchronizerFactory: syncProperties={}", syncProperties);
        this.albumOperation = albumOperation;
        this.imageOperation = imageOperation;
        this.localLibraryOperation = localLibraryOperation;
        this.syncProperties = syncProperties;
        this.albumSyncExecutor = newFixedThreadPool(syncProperties.getAlbumsConcurrency(),
                new BasicThreadFactory.Builder()
                        .namingPattern("album-pool-%d")
                        .build());
        this.uploadExecutor = newFixedThreadPool(syncProperties.getUploadConcurrency(),
                new BasicThreadFactory.Builder()
                        .namingPattern("upload-pool-%d")
                        .build());
    }

    public Synchronizer<AlbumSyncResult> getSynchronizer(LocalAlbum localAlbum) {
        return new AlbumSynchronizer(localAlbum,
                albumOperation,
                imageOperation,
                localLibraryOperation,
                syncProperties.getUploadBulkSize(),
                albumSyncExecutor,
                uploadExecutor);
    }

    public void shutdown() {
        log.info("Shutting down executor services ...");
        albumSyncExecutor.shutdown();
        uploadExecutor.shutdown();
    }
}
