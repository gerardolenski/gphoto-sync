package org.gol.gphotosync.domain.remote;

import com.google.photos.library.v1.proto.NewMediaItem;
import org.gol.gphotosync.domain.local.LocalImage;

import java.util.List;

public interface RemoteImagePort {
    List<String> listAlbumImages(String albumId);

    NewMediaItem uploadImage(LocalImage image);
}
