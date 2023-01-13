package org.gol.gphotosync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@ConfigurationPropertiesScan
public class GooglePhotoSync {

    public static void main(String[] args) {
        SpringApplication.run(GooglePhotoSync.class, args);
    }
}
