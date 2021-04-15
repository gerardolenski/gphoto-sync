package org.gol.photosync.domain.sync.album;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.gol.photosync.domain.google.album.AlbumOperation;
import org.gol.photosync.domain.google.media.ImageOperation;
import org.gol.photosync.domain.model.LocalAlbum;
import org.gol.photosync.domain.sync.SyncProperties;
import org.gol.photosync.domain.sync.Synchronizer;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
@Service
public class AlbumSynchronizerFactory {

    private final AlbumOperation albumOperation;
    private final ImageOperation imageOperation;
    private final SyncProperties syncProperties;
    private final ExecutorService albumSyncExecutor;
    private final ExecutorService uploadExecutor;

    public AlbumSynchronizerFactory(AlbumOperation albumOperation, ImageOperation imageOperation, SyncProperties syncProperties) {
        this.albumOperation = albumOperation;
        this.imageOperation = imageOperation;
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

    public Synchronizer getSynchronizer(LocalAlbum localAlbum) {
        return new AlbumSynchronizer(localAlbum,
                albumOperation,
                imageOperation,
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
