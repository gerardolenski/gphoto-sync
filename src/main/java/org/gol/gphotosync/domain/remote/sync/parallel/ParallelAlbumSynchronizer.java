package org.gol.gphotosync.domain.remote.sync.parallel;

import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.types.proto.Album;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.model.LocalAlbum;
import org.gol.gphotosync.domain.local.model.LocalImage;
import org.gol.gphotosync.domain.remote.RemoteAlbumService;
import org.gol.gphotosync.domain.remote.RemoteImageService;
import org.gol.gphotosync.domain.remote.model.RemoteAlbumTitle;
import org.gol.gphotosync.domain.remote.sync.model.AlbumSyncResult;
import org.gol.gphotosync.domain.remote.sync.model.UploadStat;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.partition;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.*;
import static org.gol.gphotosync.domain.util.LoggerUtils.formatEx;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Aggregate which synchronizes one local Album.
 */
@Slf4j
@RequiredArgsConstructor
class ParallelAlbumSynchronizer {

    private final LocalAlbum localAlbum;
    private final RemoteAlbumService remoteAlbum;
    private final RemoteImageService remoteImage;
    private final int partitionSize;
    private final ExecutorService albumSyncExecutor;
    private final ExecutorService uploadExecutor;
    private final AlbumSyncResult.AlbumSyncResultBuilder albumSyncResultBuilder = AlbumSyncResult.builder();

    private List<LocalImage> localImages;
    private Album googleAlbum;
    private List<String> googleImages;
    private List<List<LocalImage>> missingImagesPartitions;

    public CompletableFuture<AlbumSyncResult> invoke() {
        return supplyAsync(this::runSyncFlow, albumSyncExecutor);
    }

    /**
     * Runs the album synchronization flow with error handling.
     */
    private AlbumSyncResult runSyncFlow() {
        return Try.of(this::synchronizeAlbum)
                .onFailure(e -> log.error("Album synchronization failed: albumTitle={}, cause={}",
                        ofNullable(localAlbum)
                                .map(LocalAlbum::getTitle)
                                .orElse("Unknown"),
                        formatEx(e)))
                .recover(e -> albumSyncResultBuilder
                        .syncInterrupted(true)
                        .syncInterruptionMessage(e.getMessage())
                        .build())
                .get();
    }

    /**
     * The flow of album synchronization.
     */
    private AlbumSyncResult synchronizeAlbum() {
        log.info("Synchronizing album: {}", localAlbum);
        loadLocalAlbum();
        loadRemoteAlbum();
        locateMissingImages();
        uploadAndLinkMissingImages();
        return albumSyncResultBuilder.build();
    }

    private void loadLocalAlbum() {
        localImages = localAlbum.getImages();
        albumSyncResultBuilder
                .title(localAlbum.getTitle())
                .imagesCount(localImages.size());
    }

    private void loadRemoteAlbum() {
        googleAlbum = remoteAlbum.getOrCreate(new RemoteAlbumTitle(localAlbum.getTitle()));
        googleImages = remoteImage.listAlbumImages(googleAlbum.getId());
    }

    private void locateMissingImages() {
        var missingImages = localImages.stream()
                .filter(not(img -> googleImages.contains(img.getFileName())))
                .toList();
        albumSyncResultBuilder.missingImages(missingImages.size());
        missingImagesPartitions = partition(missingImages, partitionSize);
        if (isEmpty(missingImages)) {
            log.info("The album is up to date: albumTitle={}", localAlbum.getTitle());
        } else {
            log.debug("New images to upload: albumTitle={}, images={}, partitions={}",
                    localAlbum.getTitle(),
                    missingImages.stream()
                            .map(LocalImage::getFileName)
                            .collect(joining(", ")),
                    missingImagesPartitions.size());
        }
    }

    private void uploadAndLinkMissingImages() {
        var uploadResult = missingImagesPartitions.stream()
                .map(this::uploadAndLinkMissingImages)
                .map(this::linkUploadedImagesToAlbum)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(groupingBy(Map.Entry::getKey, summingLong(Map.Entry::getValue)))
                .entrySet().stream()
                .map(e -> new UploadStat(e.getKey(), e.getValue()))
                .toList();
        albumSyncResultBuilder.uploadStats(uploadResult);
    }

    private List<NewMediaItem> uploadAndLinkMissingImages(List<LocalImage> images) {
        Function<LocalImage, Supplier<NewMediaItem>> toUploadTask = localImage ->
                () -> remoteImage.uploadImage(localImage);
        var uploadTasks = images.stream()
                .map(toUploadTask)
                .map(task -> supplyAsync(task, uploadExecutor))
                .toList();
        return uploadTasks.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private Map<String, Long> linkUploadedImagesToAlbum(List<NewMediaItem> uploadedImages) {
        return remoteAlbum.addElements(googleAlbum, uploadedImages)
                .getNewMediaItemResultsList().stream()
                .map(m -> m.hasStatus() ? m.getStatus().getMessage() : "Unknown")
                .collect(groupingBy(identity(), counting()));
    }
}
