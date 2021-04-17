package org.gol.gphotosync.infrastructure.album;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.types.proto.Album;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.google.GoogleAlbumRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Repository
class GoogleAlbumRepositoryService implements GoogleAlbumRepository {

    @Override
    public Stream<Album> streamAlbums(PhotosLibraryClient client) {
        var listAlbumsPagedResponse = client.listAlbums(true);
        return StreamSupport.stream(listAlbumsPagedResponse.iterateAll().spliterator(), false);
    }

    @Override
    public Album createAlbum(PhotosLibraryClient client, String title) {
        return client.createAlbum(title);
    }
}
