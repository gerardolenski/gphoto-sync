package org.gol.gphotosync.domain.local.filter;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holds filter configuration.
 */
@Slf4j
@Getter
@ToString
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "gphotosync.filter.album")
class LocalAlbumFilterProperties {

    private final int fromYear;
    private final int toYear;

    @PostConstruct
    void init() {
        log.info("Initialized LOCAL library album filters properties: {}", this);
    }
}
