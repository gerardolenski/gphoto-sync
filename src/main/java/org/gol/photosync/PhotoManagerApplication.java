package org.gol.photosync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.photosync.domain.sync.SyncPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@ConfigurationPropertiesScan
public class PhotoManagerApplication implements CommandLineRunner {

    private final SyncPort syncPort;

    public static void main(String[] args) {
        SpringApplication.run(PhotoManagerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        var result = syncPort.sync();
        log.info("Library synchronization result: {}", result);
    }
}
