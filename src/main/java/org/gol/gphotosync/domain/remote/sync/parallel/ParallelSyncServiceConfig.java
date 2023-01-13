package org.gol.gphotosync.domain.remote.sync.parallel;

import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.remote.RemoteAlbumService;
import org.gol.gphotosync.domain.remote.RemoteImageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of components for concurrent albums synchronization.
 */
@Slf4j
@Configuration
class ParallelSyncServiceConfig {

    @Bean
    ParallelSyncService parallelSyncAdapter(ParallelSyncProperties syncProperties,
                                            RemoteAlbumService remoteAlbumService,
                                            RemoteImageService remoteImageService) {
        log.info("Initializing ParallelSyncAdapter with configuration: {}", syncProperties);
        var factory = new ParallelAlbumSynchronizerFactory(remoteAlbumService, remoteImageService, syncProperties);
        return new ParallelSyncService(factory);
    }

}
