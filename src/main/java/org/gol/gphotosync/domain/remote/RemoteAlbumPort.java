package org.gol.gphotosync.domain.remote;

import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.types.proto.Album;

import java.util.List;

public interface RemoteAlbumPort {

    /**
     * Finds the album or create it when missing.
     *
     * @param title the tile of the album
     * @return {@link Album} object
     */
    Album getOrCreate(String title);

    BatchCreateMediaItemsResponse addElements(Album album, List<NewMediaItem> images);
}
