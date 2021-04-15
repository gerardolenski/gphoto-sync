package org.gol.photosync.domain.sync.album;

import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.types.proto.Album;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.photosync.domain.google.album.AlbumOperation;
import org.gol.photosync.domain.google.media.ImageOperation;
import org.gol.photosync.domain.model.LocalAlbum;
import org.gol.photosync.domain.model.LocalImage;
import org.gol.photosync.domain.sync.Synchronizer;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

import static com.google.common.collect.Lists.partition;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
class AlbumSynchronizer implements Synchronizer, Callable<Void> {

    private final LocalAlbum localAlbum;
    private final AlbumOperation albumOperation;
    private final ImageOperation imageOperation;
    private final int partitionSize;
    private final ExecutorService albumSyncExecutor;
    private final ExecutorService uploadExecutor;

    private Album googleAlbum;
    private List<String> existingImages;
    private List<LocalImage> missingImages;
    private List<List<LocalImage>> missingImagesPartitions;

    @Override
    public Future<Void> invoke() {
        return albumSyncExecutor.submit(this);
    }

    @Override
    public Void call() {
        log.info("Synchronize album: title={}, path={}", localAlbum.getTitle(), localAlbum.getPath());
        readRemoteAlbum();
        locateMissingImages();
        missingImagesPartitions.stream()
                .map(this::uploadMissingImages)
                .forEach(this::linkUploadedImagesToAlbum);
        return null;
    }

    private void readRemoteAlbum() {
        googleAlbum = albumOperation.getOrCreate(localAlbum.getTitle());
        existingImages = imageOperation.listAlbumImages(googleAlbum.getId());
    }

    private void locateMissingImages() {
        missingImages = localAlbum.getImages().stream()
                .filter(not(img -> existingImages.contains(img.getFileName())))
                .collect(toList());
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
                () -> imageOperation.uploadImage(localImage);
        var uploadTasks = images.stream()
                .map(toUploadTask)
                .collect(toList());
        return Try.of(() -> uploadExecutor.invokeAll(uploadTasks))
                .get()
                .stream()
                .map(f -> Try.of(f::get).get())
                .collect(toList());
    }

    private void linkUploadedImagesToAlbum(List<NewMediaItem> uploadedImages) {
        albumOperation.addElements(googleAlbum, uploadedImages);
    }
}
