package org.gol.gphotosync.domain.remote;

import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.types.proto.Album;
import lombok.NonNull;
import org.gol.gphotosync.domain.remote.model.RemoteAlbumTitle;

import java.util.List;

/**
 * Holds contract for Domain Service managing Google remote album.
 */
public interface RemoteAlbumService {

    /**
     * Finds the album or create it when missing.
     *
     * @param title the tile of the album
     * @return {@link Album} object representation in Google Photos
     */
    Album getOrCreate(@NonNull RemoteAlbumTitle title);

    /**
     * Adds elements to the remote album.
     *
     * @param album  the desired album
     * @param images the images representing by {@link NewMediaItem} collection
     * @return the response from Google Photos remote library
     */
    BatchCreateMediaItemsResponse addElements(@NonNull Album album, @NonNull List<NewMediaItem> images);
}
