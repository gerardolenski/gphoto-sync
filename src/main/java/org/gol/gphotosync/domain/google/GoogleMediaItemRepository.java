package org.gol.gphotosync.domain.google;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.types.proto.MediaItem;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public interface GoogleMediaItemRepository {
    Stream<MediaItem> streamAlbumItems(PhotosLibraryClient client, String albumId);

    UploadMediaItemResponse uploadImage(PhotosLibraryClient client, File image, String mimeType) throws IOException;
}
