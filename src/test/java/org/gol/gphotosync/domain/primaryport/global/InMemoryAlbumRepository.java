package org.gol.gphotosync.domain.primaryport.global;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.types.proto.Album;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.google.GoogleAlbumRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

@Slf4j
class InMemoryAlbumRepository implements GoogleAlbumRepository {

    private final List<Album> albums = new CopyOnWriteArrayList<>();

    void reset() {
        albums.clear();
    }

    @Override
    public Stream<Album> streamAlbums(PhotosLibraryClient client) {
        log.trace("Streaming albums: {}", albums);
        return albums.stream();
    }

    @Override
    public Album createAlbum(PhotosLibraryClient client, String title) {
        var album = Album.newBuilder()
                .setTitle(title)
                .setId(UUID.randomUUID().toString())
                .build();
        log.trace("Adding album: {}", album);
        albums.add(album);
        return album;
    }
}
