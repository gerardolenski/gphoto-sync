package org.gol.gphotosync.infrastructure.media;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.upload.UploadMediaItemRequest;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.types.proto.MediaItem;
import org.gol.gphotosync.domain.google.GoogleMediaItemRepository;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository
class GoogleMediaItemRepositoryService implements GoogleMediaItemRepository {

    @Override
    public Stream<MediaItem> streamAlbumItems(PhotosLibraryClient client, String albumId) {
        return StreamSupport.stream(client.searchMediaItems(albumId)
                .iterateAll()
                .spliterator(), false);
    }

    @Override
    public UploadMediaItemResponse uploadImage(PhotosLibraryClient client, File image, String mimeType) throws IOException {
        try (var file = new RandomAccessFile(image, "r")) {
            var uploadRequest = UploadMediaItemRequest.newBuilder()
                    .setMimeType(mimeType)
                    .setDataFile(file)
                    .build();
            return client.uploadMediaItem(uploadRequest);
        }
    }
}
