package org.gol.gphotosync.application;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Year;

/**
 * Holds filter configuration.
 */
@Slf4j
@Getter
@ToString
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "gphotosync.filter.album", ignoreInvalidFields = true)
class LocalAlbumFilterProperties {

    private final Year fromYear;
    private final Year toYear;

    @PostConstruct
    void init() {
        log.info("Initialized LOCAL library album filters properties: {}", this);
    }
}
