package org.gol.photosync.domain.google.client;

import com.google.photos.library.v1.PhotosLibraryClient;

public interface ClientFactory {

    PhotosLibraryClient getClient();
}
