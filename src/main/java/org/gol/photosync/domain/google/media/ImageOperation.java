package org.gol.photosync.domain.google.media;

import com.google.photos.library.v1.proto.NewMediaItem;
import org.gol.photosync.domain.model.LocalImage;

import java.util.List;

public interface ImageOperation {
    List<String> listAlbumImages(String albumId);

    NewMediaItem uploadImage(LocalImage image);
}
