package org.gol.gphotosync.domain.local.library;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.annotation.PostConstruct;
import java.nio.file.Path;

@Slf4j
@Getter
@ToString
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "photouploader.library")
class LibraryProperties {

    private final Path path;

    @PostConstruct
    void init() {
        log.info("Initialize LOCAL library properties: {}", this);
    }
}
