package org.gol.gphotosync.domain.sync.album;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.LocalAlbum;
import org.gol.gphotosync.domain.model.AlbumSyncResult;
import org.gol.gphotosync.domain.remote.RemoteAlbumPort;
import org.gol.gphotosync.domain.remote.RemoteImagePort;
import org.gol.gphotosync.domain.sync.SyncProperties;
import org.gol.gphotosync.domain.sync.Synchronizer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Slf4j
@Service
@Scope(SCOPE_PROTOTYPE)
public class AlbumSynchronizerFactory implements AutoCloseable {

    private final RemoteAlbumPort remoteAlbum;
    private final RemoteImagePort remoteImage;
    private final SyncProperties syncProperties;
    private final ExecutorService albumSyncExecutor;
    private final ExecutorService uploadExecutor;

    public AlbumSynchronizerFactory(
            RemoteAlbumPort remoteAlbum,
            RemoteImagePort remoteImage,
            SyncProperties syncProperties) {
        log.debug("Init AlbumSynchronizerFactory: syncProperties={}", syncProperties);
        this.remoteAlbum = remoteAlbum;
        this.remoteImage = remoteImage;
        this.syncProperties = syncProperties;
        this.albumSyncExecutor = newFixedThreadPool(syncProperties.getAlbumsConcurrency(),
                new ThreadFactoryBuilder()
                        .setNameFormat("album-pool-%d")
                        .build());
        this.uploadExecutor = newFixedThreadPool(syncProperties.getUploadConcurrency(),
                new ThreadFactoryBuilder()
                        .setNameFormat("upload-pool-%d")
                        .build());
    }

    public Synchronizer<AlbumSyncResult> getSynchronizer(LocalAlbum localAlbum) {
        return new AlbumSynchronizer(localAlbum,
                remoteAlbum,
                remoteImage,
                syncProperties.getUploadBulkSize(),
                albumSyncExecutor,
                uploadExecutor);
    }

    public void shutdown() {
        log.info("Shutting down executor services ...");
        albumSyncExecutor.shutdown();
        uploadExecutor.shutdown();
    }

    @Override
    public void close() {
        shutdown();
    }
}
