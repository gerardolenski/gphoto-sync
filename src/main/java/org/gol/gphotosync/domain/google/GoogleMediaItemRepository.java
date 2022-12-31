package org.gol.gphotosync.domain.google;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.types.proto.MediaItem;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public interface GoogleMediaItemRepository {
    Stream<MediaItem> streamAlbumItems(PhotosLibraryClient client, String albumId);

    UploadMediaItemResponse uploadImage(PhotosLibraryClient client, File image, String mimeType) throws IOException;

    BatchCreateMediaItemsResponse linkImages(PhotosLibraryClient client, String albumId, List<NewMediaItem> images);
}
