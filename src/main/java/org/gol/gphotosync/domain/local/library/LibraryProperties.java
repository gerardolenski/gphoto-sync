package org.gol.gphotosync.domain.local.library;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@Slf4j
@Getter
@ToString
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "gphotosync.library")
class LibraryProperties {

    private final Path path;

    @PostConstruct
    void init() {
        log.info("Initialized LOCAL library properties: {}", this);
    }
}
