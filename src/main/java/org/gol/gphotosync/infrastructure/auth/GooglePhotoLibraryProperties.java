package org.gol.gphotosync.infrastructure.auth;

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
@ConfigurationProperties(prefix = "photouploader.google.photolibrary")
class GooglePhotoLibraryProperties {

    private final String credentialDir;
    private final String credentialFile;
    private final int localReceiverPort;
    private final String userId;

    @PostConstruct
    void init() {
        log.info("Initialize GOOGLE photo library properties: {}", this);
    }
}
