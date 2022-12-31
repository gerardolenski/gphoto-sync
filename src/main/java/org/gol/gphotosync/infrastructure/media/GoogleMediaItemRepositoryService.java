package org.gol.gphotosync.infrastructure.media;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient;
import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.library.v1.proto.SearchMediaItemsResponse;
import com.google.photos.library.v1.upload.UploadMediaItemRequest;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.types.proto.MediaItem;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.google.GoogleMediaItemRepository;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Repository
class GoogleMediaItemRepositoryService implements GoogleMediaItemRepository {

    @Override
    @SuppressWarnings("java:S3864") //peek used for logging
    public Stream<MediaItem> streamAlbumItems(PhotosLibraryClient client, String albumId) {
        return StreamSupport.stream(client.searchMediaItems(albumId)
                .iteratePages()
                .spliterator(), false)
                .peek(p -> log.trace("Loaded album images page: albumId={}, imagesCount={}", albumId, p.getPageElementCount()))
                .map(InternalPhotosLibraryClient.SearchMediaItemsPage::getResponse)
                .map(SearchMediaItemsResponse::getMediaItemsList)
                .flatMap(Collection::stream);
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

    @Override
    public BatchCreateMediaItemsResponse linkImages(PhotosLibraryClient client, String albumId, List<NewMediaItem> images) {
        return client.batchCreateMediaItems(albumId, images);
    }
}
