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
import org.gol.gphotosync.domain.remote.RemoteAlbumPort;
import org.gol.gphotosync.domain.remote.RemoteImagePort;
import org.gol.gphotosync.domain.sync.Synchronizer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

import static com.google.common.collect.Lists.partition;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.*;
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
    public Future<AlbumSyncResult> invoke() {
        return albumSyncExecutor.submit(this);
    }

    @Override
    public AlbumSyncResult call() {
        log.info("Synchronizing album: title={}, path={}", localAlbum.getTitle(), localAlbum.getPath());
        loadLocalAlbum();
        loadRemoteAlbum();
        locateMissingImages();
        var uploadResult = missingImagesPartitions.stream()
                .map(this::uploadMissingImages)
                .map(this::linkUploadedImagesToAlbum)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(groupingBy(Map.Entry::getKey, summingLong(Map.Entry::getValue)));
        albumSyncResultBuilder.uploadStats(uploadResult);
        return albumSyncResultBuilder.build();
    }

    private void loadLocalAlbum() {
        localImages = localLibrary.getAlbumImages(localAlbum);
        albumSyncResultBuilder
                .title(localAlbum.getTitle())
                .imagesCount(localImages.size());
    }

    private void loadRemoteAlbum() {
        googleAlbum = remoteAlbum.getOrCreate(localAlbum.getTitle());
        googleImages = remoteImage.listAlbumImages(googleAlbum.getId());
    }

    private void locateMissingImages() {
        var missingImages = localImages.stream()
                .filter(not(img -> googleImages.contains(img.getFileName())))
                .collect(toList());
        albumSyncResultBuilder.missingImages(missingImages.size());
        missingImagesPartitions = partition(missingImages, partitionSize);
        if (isEmpty(missingImages)) {
            log.info("The album is up to date: album={}", localAlbum.getTitle());
        } else {
            log.debug("New images to upload: album={}, images={}, partitions={}",
                    localAlbum.getTitle(),
                    missingImages.stream()
                            .map(LocalImage::getFileName)
                            .collect(joining(", ")),
                    missingImagesPartitions.size());
        }
    }

    private List<NewMediaItem> uploadMissingImages(List<LocalImage> images) {
        Function<LocalImage, Callable<NewMediaItem>> toUploadTask = localImage ->
                () -> remoteImage.uploadImage(localImage);
        var uploadTasks = images.stream()
                .map(toUploadTask)
                .collect(toList());
        return Try.of(() -> uploadExecutor.invokeAll(uploadTasks))
                .get()
                .stream()
                .map(f -> Try.of(f::get).get())
                .collect(toList());
    }

    private Map<String, Long> linkUploadedImagesToAlbum(List<NewMediaItem> uploadedImages) {
        return remoteAlbum.addElements(googleAlbum, uploadedImages)
                .getNewMediaItemResultsList().stream()
                .map(m -> m.hasStatus() ? m.getStatus().getMessage() : "Unknown")
                .collect(groupingBy(identity(), counting()));
    }
}
