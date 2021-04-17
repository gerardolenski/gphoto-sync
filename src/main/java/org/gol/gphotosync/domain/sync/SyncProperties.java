package org.gol.gphotosync.domain.sync;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.annotation.PostConstruct;

@Slf4j
@Getter
@ToString
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "photouploader.synchronizer")
public class SyncProperties {

    private final int albumsConcurrency;
    private final int uploadConcurrency;
    private final int uploadBulkSize;

    @PostConstruct
    void init() {
        log.info("Initialize SYNCHRONIZER properties: {}", this);
    }
}
