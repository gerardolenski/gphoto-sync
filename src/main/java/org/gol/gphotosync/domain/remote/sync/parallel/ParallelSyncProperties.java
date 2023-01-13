package org.gol.gphotosync.domain.remote.sync.parallel;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holding properties for albums synchronization components.
 */
@ConfigurationProperties(prefix = "gphotosync.synchronizer")
record ParallelSyncProperties(int albumsConcurrency, int uploadConcurrency, int uploadBulkSize) {

}
