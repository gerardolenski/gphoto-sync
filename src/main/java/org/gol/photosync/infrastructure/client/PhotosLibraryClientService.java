package org.gol.photosync.infrastructure.client;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.photosync.domain.auth.GoogleCredentialsSupplier;
import org.gol.photosync.domain.google.client.ClientFactory;
import org.springframework.stereotype.Service;
import org.threeten.bp.Duration;

import java.io.IOException;

import static org.gol.photosync.domain.util.LoggerUtils.formatEx;

/**
 * This bean is responsible for initializing the completely configured {@link PhotosLibraryClient} object.
 */
@Slf4j
@Service
@RequiredArgsConstructor
class PhotosLibraryClientService implements ClientFactory {

    private final GoogleCredentialsSupplier authService;

    @Override
    public PhotosLibraryClient getClient() {
        return Try.of(this::prepareSettings)
                .mapTry(PhotosLibraryClient::initialize)
                .onFailure(e -> log.error("Cannot init PhotosLibraryClient: {}", formatEx(e)))
                .get();
    }

    private PhotosLibrarySettings prepareSettings() throws IOException {
        var builder = PhotosLibrarySettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(authService.getCredentials()));

        builder.batchCreateMediaItemsSettings()
                .retrySettings()
                .setMaxAttempts(100)
                .setInitialRetryDelay(Duration.ofSeconds(1))
                .setMaxRetryDelay(Duration.ofMinutes(1))
                .build();

        builder.listAlbumsSettings()
                .retrySettings()
                .setInitialRetryDelay(Duration.ofSeconds(1))
                .setMaxRetryDelay(Duration.ofMinutes(1))
                .setMaxAttempts(100)
                .build();

        builder.uploadMediaItemSettingsBuilder()
                .retrySettings()
                .setMaxAttempts(100)
                .setInitialRetryDelay(Duration.ofSeconds(1))
                .setMaxRetryDelay(Duration.ofMinutes(1))
                .build();

        builder.createAlbumSettings()
                .retrySettings()
                .setMaxAttempts(100)
                .setInitialRetryDelay(Duration.ofSeconds(1))
                .setMaxRetryDelay(Duration.ofMinutes(1))
                .build();

        return builder.build();

    }
}
