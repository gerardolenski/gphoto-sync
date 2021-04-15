package org.gol.photosync.domain.google.album;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.types.proto.Album;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.photosync.domain.google.client.ClientFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static io.vavr.control.Try.withResources;
import static java.util.stream.Collectors.toList;
import static org.gol.photosync.domain.util.LoggerUtils.formatEx;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
class AlbumService implements AlbumOperation {

    private final ClientFactory clientFactory;
    private final AlbumRepository albumRepository;

    @Override
    public Album getOrCreate(String title) {
        log.debug("Getting (or creating) album: title={}", title);
        return withResources(clientFactory::getClient)
                .of(client -> getAlbum(client, title)
                        .orElseGet(() -> createAlbum(client, title)))
                .get();
    }

    @Override
    public void addElements(Album album, List<NewMediaItem> images) {
        if (isEmpty(images)) {
            log.info("The album is up to date: album={}", album.getTitle());
            return;
        }
        log.info("Linking album images: album={}", album.getTitle());
        withResources(clientFactory::getClient)
                .of(client -> client.batchCreateMediaItems(album.getId(), images))
                .onSuccess(result -> this.logCreateMediaItemsResponse(album.getTitle(), result))
                .onFailure(e -> log.error("Linking album images failed: album={}, cause={}", album.getTitle(), formatEx(e)));
    }

    private Optional<Album> getAlbum(PhotosLibraryClient client, String albumTitle) {
        return albumRepository.streamAlbums(client)
                .filter(a -> a.getTitle().equals(albumTitle))
                .peek(album -> log.trace("Album was found: albumTitle={}, id={}", album.getTitle(), album.getId()))
                .findFirst();
    }

    private Album createAlbum(PhotosLibraryClient client, String albumTitle) {
        log.trace("Album does not exist, creating new one: title={}", albumTitle);
        var album = albumRepository.createAlbum(client, albumTitle);
        log.trace("Album was created: title={}, id={}", album.getTitle(), album.getId());
        return album;
    }

    private void logCreateMediaItemsResponse(String albumTitle, BatchCreateMediaItemsResponse response) {
        log.info("Linking album images result: album={}, images={}", albumTitle, response.getNewMediaItemResultsList()
                .stream()
                .map(r -> r.getStatus().getMessage() + ": " + r.getMediaItem().getFilename())
                .collect(toList()));
    }
}
