package org.gol.gphotosync.domain.remote;

import com.google.photos.library.v1.proto.NewMediaItem;
import org.gol.gphotosync.domain.local.model.LocalImage;

import java.util.List;

/**
 * Holds contract for Domain Service implementation managing remote image.
 */
public interface RemoteImageService {
    List<String> listAlbumImages(String albumId);

    NewMediaItem uploadImage(LocalImage image);
}
