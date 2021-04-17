package org.gol.gphotosync.domain.google;

import com.google.photos.library.v1.PhotosLibraryClient;

public interface GoogleClientFactory {

    PhotosLibraryClient getClient();
}
