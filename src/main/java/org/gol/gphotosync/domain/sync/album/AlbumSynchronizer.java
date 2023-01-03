package org.gol.gphotosync.domain.sync.album;

import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.types.proto.Album;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.LocalLibraryPort;
import org.gol.gphotosync.domain.model.AlbumSyncResult;
import org.gol.gphotosync.domain.model.LocalAlbum;
import org.gol.gphotosync.domain.model.LocalImage;
import org.gol.gphotosync.domain.model.UploadStat;
import org.gol.gphotosync.domain.remote.RemoteAlbumPort;
import org.gol.gphotosync.domain.remote.RemoteImagePort;
import org.gol.gphotosync.domain.sync.Synchronizer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
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

@Slf4j
@RequiredArgsConstructor
class AlbumSynchronizer implements Synchronizer<AlbumSyncResult>, Callable<AlbumSyncResult> {

    private final LocalAlbum localAlbum;
    private final RemoteAlbumPort remoteAlbum;
    private final RemoteImagePort remoteImage;
    private final LocalLibraryPort localLibrary;
    private final int partitionSize;
    private final ExecutorService albumSyncExecutor;
    private final ExecutorService uploadExecutor;
    private final AlbumSyncResult.AlbumSyncResultBuilder albumSyncResultBuilder = AlbumSyncResult.builder();

    private List<LocalImage> localImages;
    private Album googleAlbum;
    private List<String> googleImages;
    private List<List<LocalImage>> missingImagesPartitions;

    @Override
    public CompletableFuture<AlbumSyncResult> invoke() {
        return supplyAsync(this::call, albumSyncExecutor);
    }

    @Override
    public AlbumSyncResult call() {
        return Try.of(this::synchronizeAlbum)
                .onFailure(e -> log.error("Album synchronization failed: albumTitle={}, cause={}",
                        ofNullable(localAlbum)
                                .map(LocalAlbum::title)
                                .orElse("Unknown"),
                        formatEx(e)))
                .recover(e -> albumSyncResultBuilder
                        .syncInterrupted(true)
                        .syncInterruptionMessage(e.getMessage())
                        .build())
                .get();
    }

    private AlbumSyncResult synchronizeAlbum() {
        log.info("Synchronizing album: albumTitle={}, path={}", localAlbum.title(), localAlbum.title());
        loadLocalAlbum();
        loadRemoteAlbum();
        locateMissingImages();
        var uploadResult = missingImagesPartitions.stream()
                .map(this::uploadMissingImages)
                .map(this::linkUploadedImagesToAlbum)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(groupingBy(Map.Entry::getKey, summingLong(Map.Entry::getValue)))
                .entrySet().stream()
                .map(e -> new UploadStat(e.getKey(), e.getValue()))
                .toList();
        albumSyncResultBuilder.uploadStats(uploadResult);
        return albumSyncResultBuilder.build();
    }

    private void loadLocalAlbum() {
        localImages = localLibrary.getAlbumImages(localAlbum);
        albumSyncResultBuilder
                .title(localAlbum.title())
                .imagesCount(localImages.size());
    }

    private void loadRemoteAlbum() {
        googleAlbum = remoteAlbum.getOrCreate(localAlbum.title());
        googleImages = remoteImage.listAlbumImages(googleAlbum.getId());
    }

    private void locateMissingImages() {
        var missingImages = localImages.stream()
                .filter(not(img -> googleImages.contains(img.fileName())))
                .toList();
        albumSyncResultBuilder.missingImages(missingImages.size());
        missingImagesPartitions = partition(missingImages, partitionSize);
        if (isEmpty(missingImages)) {
            log.info("The album is up to date: albumTitle={}", localAlbum.title());
        } else {
            log.debug("New images to upload: albumTitle={}, images={}, partitions={}",
                    localAlbum.title(),
                    missingImages.stream()
                            .map(LocalImage::fileName)
                            .collect(joining(", ")),
                    missingImagesPartitions.size());
        }
    }

    private List<NewMediaItem> uploadMissingImages(List<LocalImage> images) {
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
