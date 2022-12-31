package org.gol.gphotosync.domain.google;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.types.proto.Album;

import java.util.stream.Stream;

public interface GoogleAlbumRepository {

    Stream<Album> streamAlbums(PhotosLibraryClient client);

    Album createAlbum(PhotosLibraryClient client, String title);


}
