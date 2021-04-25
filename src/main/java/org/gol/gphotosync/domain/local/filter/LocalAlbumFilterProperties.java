package org.gol.gphotosync.domain.local.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.annotation.PostConstruct;

/**
 * Holds filter configuration.
 */
@Slf4j
@Getter
@ToString
@ConstructorBinding
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
