package org.gol.photosync.domain.google.media;

import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.types.proto.MediaItem;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.photosync.domain.google.client.ClientFactory;
import org.gol.photosync.domain.model.LocalImage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.google.photos.library.v1.util.NewMediaItemFactory.createNewMediaItem;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
class ImageService implements ImageOperation {

    private final ClientFactory clientFactory;
    private final MediaItemRepository mediaItemRepository;

    @Override
    public List<String> listAlbumImages(String albumId) {
        try (var client = clientFactory.getClient()) {
            return mediaItemRepository.streamAlbumItems(client, albumId)
                    .map(MediaItem::getFilename)
                    .collect(toList());
        }
    }

    @Override
    public NewMediaItem uploadImage(LocalImage image) {
        log.debug("Uploading image: {}", image.getDescription());
        return Try.withResources(clientFactory::getClient)
                .of(client -> mediaItemRepository.uploadImage(client, image.getFile(), image.getMimeType()))
                .peek(r -> log.trace("Image upload response: token={}, error={}", r.getUploadToken(), r.getError()))
                .map(UploadMediaItemResponse::getUploadToken)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapTry(token -> createNewMediaItem(token, image.getFileName(), image.getDescription()))
                .onFailure(e -> log.error("Cannot create media item: {}", e.getMessage()))
                .getOrElseThrow(() -> new IllegalStateException(format("Image upload failed: image=%s", image)));
    }
}
