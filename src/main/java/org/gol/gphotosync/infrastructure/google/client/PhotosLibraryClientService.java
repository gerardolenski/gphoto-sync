package org.gol.gphotosync.infrastructure.google.client;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.google.GoogleCredentialsSupplier;
import org.gol.gphotosync.domain.google.GoogleClientFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.gol.gphotosync.domain.util.LoggerUtils.formatEx;

/**
 * This bean is responsible for initializing the completely configured {@link PhotosLibraryClient} object.
 */
@Slf4j
@Service
@RequiredArgsConstructor
class PhotosLibraryClientService implements GoogleClientFactory {

    private final GoogleCredentialsSupplier authService;

    @Override
    public PhotosLibraryClient getClient() {
        return Try.of(this::prepareSettings)
                .mapTry(PhotosLibraryClient::initialize)
                .onFailure(e -> log.error("Cannot init PhotosLibraryClient: {}", formatEx(e)))
                .get();
    }

    private PhotosLibrarySettings prepareSettings() throws IOException {
        return PhotosLibrarySettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(authService.getCredentials()))
                .build();
    }
}
