package org.gol.gphotosync.domain.remote.album;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.types.proto.Album;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.google.GoogleAlbumRepository;
import org.gol.gphotosync.domain.google.GoogleClientFactory;
import org.gol.gphotosync.domain.google.GoogleMediaItemRepository;
import org.gol.gphotosync.domain.remote.RemoteAlbumPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static io.vavr.control.Try.withResources;
import static java.util.stream.Collectors.toList;
import static org.gol.gphotosync.domain.util.LoggerUtils.formatEx;

@Slf4j
@Service
@RequiredArgsConstructor
class RemoteAlbumService implements RemoteAlbumPort {

    private final GoogleClientFactory googleClientFactory;
    private final GoogleAlbumRepository albumRepository;
    private final GoogleMediaItemRepository mediaItemRepository;

    @Override
    public Album getOrCreate(String title) {
        log.debug("Getting (or creating) album: albumTitle={}", title);
        return withResources(googleClientFactory::getClient)
                .of(client -> getAlbum(client, title)
                        .orElseGet(() -> createAlbum(client, title)))
                .onFailure(e -> log.error("Getting (or creating) album failed: albumTitle={}, cause={}", title, formatEx(e)))
                .get();
    }

    @Override
    public BatchCreateMediaItemsResponse addElements(Album album, List<NewMediaItem> images) {
        log.info("Linking album images: albumTitle={}", album.getTitle());
        return withResources(googleClientFactory::getClient)
                .of(client -> mediaItemRepository.linkImages(client, album.getId(), images))
                .onSuccess(result -> this.logCreateMediaItemsResponse(album.getTitle(), result))
                .onFailure(e -> log.error("Linking album images failed: albumTitle={}, cause={}", album.getTitle(), formatEx(e)))
                .get();
    }

    @SuppressWarnings("java:S3864") // peek is used for trace logging
    private Optional<Album> getAlbum(PhotosLibraryClient client, String albumTitle) {
        return albumRepository.streamAlbums(client)
                .filter(a -> a.getTitle().equals(albumTitle))
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
                .collect(toList()));
    }
}
