package org.gol.gphotosync.infrastructure.google.auth;

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
@ConfigurationProperties(prefix = "gphotosync.google.photolibrary")
class GooglePhotoLibraryProperties {

    private final String credentialDir;
    private final String credentialFile;
    private final int localReceiverPort;
    private final String userId;

    @PostConstruct
    void init() {
        log.info("Initialized GOOGLE photo library properties: {}", this);
    }
}
