package org.gol.photosync;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.gol.photosync.domain.library.LocalLibrary;
import org.gol.photosync.domain.sync.Synchronizer;
import org.gol.photosync.domain.sync.album.AlbumSynchronizerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import static java.util.stream.Collectors.toList;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class PhotoManagerApplication implements CommandLineRunner {

    private final LocalLibrary localLibrary;
    private final AlbumSynchronizerFactory albumSynchronizerFactory;

    public PhotoManagerApplication(
            LocalLibrary localLibrary,
            AlbumSynchronizerFactory albumSynchronizerFactory) {
        this.localLibrary = localLibrary;
        this.albumSynchronizerFactory = albumSynchronizerFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(PhotoManagerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Synchronizing library ...");
        var watch = StopWatch.create();
        watch.start();

        localLibrary.findAlbums().stream()
                .map(localLibrary::getAlbum)
                .map(albumSynchronizerFactory::getSynchronizer)
                .map(Synchronizer::invoke)
                .collect(toList())
                .forEach(f -> Try.of(f::get));
        albumSynchronizerFactory.shutdown();

        watch.stop();
        log.info("Synchronization took: {}", watch.formatTime());
    }
}
