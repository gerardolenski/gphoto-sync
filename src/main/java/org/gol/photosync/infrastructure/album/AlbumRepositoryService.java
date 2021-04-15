package org.gol.photosync.infrastructure.album;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.types.proto.Album;
import lombok.extern.slf4j.Slf4j;
import org.gol.photosync.domain.google.album.AlbumRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Repository
class AlbumRepositoryService implements AlbumRepository {

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
