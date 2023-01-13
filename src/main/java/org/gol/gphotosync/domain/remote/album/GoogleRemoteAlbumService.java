package org.gol.gphotosync.domain.remote.album;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.types.proto.Album;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.google.GoogleAlbumRepository;
import org.gol.gphotosync.domain.google.GoogleClientFactory;
import org.gol.gphotosync.domain.google.GoogleMediaItemRepository;
import org.gol.gphotosync.domain.remote.RemoteAlbumService;
import org.gol.gphotosync.domain.remote.model.RemoteAlbumTitle;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static io.vavr.control.Try.withResources;
import static org.gol.gphotosync.domain.util.LoggerUtils.formatEx;

/**
 * Domain Service containing logic of Google remote album operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
class GoogleRemoteAlbumService implements RemoteAlbumService {

    private final GoogleClientFactory googleClientFactory;
    private final GoogleAlbumRepository albumRepository;
    private final GoogleMediaItemRepository mediaItemRepository;

    @Override
    public Album getOrCreate(@NonNull RemoteAlbumTitle albumTitle) {
        log.debug("Getting (or creating) album: albumTitle={}", albumTitle);
        return withResources(googleClientFactory::getClient)
                .of(client -> getAlbum(client, albumTitle)
                        .orElseGet(() -> createAlbum(client, albumTitle.value())))
                .onFailure(e -> log.error("Getting (or creating) album failed: albumTitle={}, cause={}", albumTitle, formatEx(e)))
                .get();
    }

    @Override
    public BatchCreateMediaItemsResponse addElements(@NonNull Album album, @NonNull List<NewMediaItem> images) {
        log.info("Linking album images: albumTitle={}", album.getTitle());
        return withResources(googleClientFactory::getClient)
                .of(client -> mediaItemRepository.linkImages(client, album.getId(), images))
                .onSuccess(result -> this.logCreateMediaItemsResponse(album.getTitle(), result))
                .onFailure(e -> log.error("Linking album images failed: albumTitle={}, cause={}", album.getTitle(), formatEx(e)))
                .get();
    }

    @SuppressWarnings("java:S3864") // peek is used for trace logging
    private Optional<Album> getAlbum(PhotosLibraryClient client, RemoteAlbumTitle albumTitle) {
        return albumRepository.streamAlbums(client)
                .filter(a -> albumTitle.sameAs(a.getTitle()))
                .peek(album -> log.trace("Album was found: albumTitle={}, id={}", album.getTitle(), album.getId()))
                .findFirst();
    }

    private Album createAlbum(PhotosLibraryClient client, String albumTitle) {
        log.trace("Album does not exist, creating new one: albumTitle={}", albumTitle);
        var album = albumRepository.createAlbum(client, albumTitle);
        log.trace("Album was created: albumTitle={}, id={}", album.getTitle(), album.getId());
        return album;
    }

    private void logCreateMediaItemsResponse(String albumTitle, BatchCreateMediaItemsResponse response) {
        log.debug("Linking album images result: albumTitle={}, images={}", albumTitle, response.getNewMediaItemResultsList()
                .stream()
                .map(r -> r.getStatus().getMessage() + ": " + r.getMediaItem().getFilename())
                .toList());
    }
}
