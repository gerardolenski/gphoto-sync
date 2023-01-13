package org.gol.gphotosync.domain.primaryport.global;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.library.v1.proto.NewMediaItemResult;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.types.proto.MediaItem;
import com.google.rpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.google.GoogleMediaItemRepository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
class InMemoryMediaRepository implements GoogleMediaItemRepository {

    private final Map<String, List<MediaItem>> items = new ConcurrentHashMap<>();

    void reset() {
        items.clear();
    }

    void addItems(String albumId, Set<String> imageNames) {
        items.put(albumId,
                Stream.concat(
                        items.getOrDefault(albumId, List.of()).stream(),
                        imageNames.stream()
                                .map(n -> MediaItem.newBuilder()
                                        .setId(UUID.randomUUID().toString())
                                        .setFilename(n)
                                        .setMimeType("images/jpeg")
                                        .build())
                ).toList());
    }

    @Override
    public Stream<MediaItem> streamAlbumItems(PhotosLibraryClient client, String albumId) {
        var its = items.getOrDefault(albumId, List.of());
        log.trace("Streaming media items: {}", its);
        return its.stream();
    }

    @Override
    public UploadMediaItemResponse uploadImage(PhotosLibraryClient client, File image, String mimeType) {
        var mediaItem = MediaItem.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setFilename(image.getName())
                .setMimeType(mimeType)
                .build();
        log.trace("Adding media item: {}", mediaItem);
        return UploadMediaItemResponse.newBuilder()
                .setUploadToken(mediaItem.getId())
                .build();
    }

    @Override
    public BatchCreateMediaItemsResponse linkImages(PhotosLibraryClient client, String albumId, List<NewMediaItem> images) {
        var results = images.stream()
                .map(im -> NewMediaItemResult.newBuilder()
                        .setStatus(Status.newBuilder()
                                .setCode(0)
                                .setMessage("Success")
                                .build())
                        .setMediaItem(MediaItem.newBuilder()
                                .setId(UUID.randomUUID().toString())
                                .setFilename(im.getSimpleMediaItem().getFileName())
                                .build())
                        .build())
                .toList();
        items.put(albumId,
                Stream.concat(
                        items.getOrDefault(albumId, List.of()).stream(),
                        results.stream().map(NewMediaItemResult::getMediaItem)
                ).toList());
        return BatchCreateMediaItemsResponse.newBuilder()
                .addAllNewMediaItemResults(results)
                .build();
    }
}
