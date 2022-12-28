package org.gol.gphotosync.domain.remote.image;

import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.types.proto.MediaItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.google.GoogleClientFactory;
import org.gol.gphotosync.domain.google.GoogleMediaItemRepository;
import org.gol.gphotosync.domain.model.LocalImage;
import org.gol.gphotosync.domain.remote.RemoteImagePort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.google.photos.library.v1.util.NewMediaItemFactory.createNewMediaItem;
import static io.vavr.control.Try.withResources;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.gol.gphotosync.domain.util.LoggerUtils.formatEx;

@Slf4j
@Service
@RequiredArgsConstructor
class RemoteImageService implements RemoteImagePort {

    private final GoogleClientFactory googleClientFactory;
    private final GoogleMediaItemRepository mediaItemRepository;

    @Override
    public List<String> listAlbumImages(String albumId) {
        log.trace("Getting album images: albumId={}", albumId);
        return withResources(googleClientFactory::getClient)
                .of(client -> mediaItemRepository.streamAlbumItems(client, albumId)
                        .map(MediaItem::getFilename)
                        .collect(toList()))
                .onFailure(e -> log.error("Listing album images failed: albumId={}, cause={}", albumId, formatEx(e)))
                .get();
    }

    @Override
    public NewMediaItem uploadImage(LocalImage image) {
        log.info("Uploading image: {}", image.description());
        return withResources(googleClientFactory::getClient)
                .of(client -> mediaItemRepository.uploadImage(client, image.file(), image.mimeType()))
                .onFailure(e -> log.error("Image upload failed: image={}, cause={}", image.fileName(), formatEx(e)))
                .peek(r -> log.trace("Image upload response: token={}, error={}", r.getUploadToken(), r.getError()))
                .map(UploadMediaItemResponse::getUploadToken)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapTry(token -> createNewMediaItem(token, image.fileName(), image.description()))
                .onFailure(e -> log.error("Create media item failed: image={}, cause={}", image.fileName(), formatEx(e)))
                .getOrElseThrow(() -> new IllegalStateException(format("Image upload failed: image=%s", image)));
    }
}
