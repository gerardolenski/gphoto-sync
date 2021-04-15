package org.gol.photosync.domain.google.media;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.types.proto.MediaItem;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public interface MediaItemRepository {
    Stream<MediaItem> streamAlbumItems(PhotosLibraryClient client, String albumId);

    UploadMediaItemResponse uploadImage(PhotosLibraryClient client, File image, String mimeType) throws IOException;
}
