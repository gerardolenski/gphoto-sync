package org.gol.gphotosync.domain.sync;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@Getter
@ToString
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "gphotosync.synchronizer")
public class SyncProperties {

    private final int albumsConcurrency;
    private final int uploadConcurrency;
    private final int uploadBulkSize;

    @PostConstruct
    void init() {
        log.info("Initialized SYNCHRONIZER properties: {}", this);
    }
}
