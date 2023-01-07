package org.gol.gphotosync.domain.local;

import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Represents the local album aggregate.
 */
public interface LocalAlbum {

    /**
     * @return the tile of the album
     */
    String getTitle();

    /**
     * @return the optional year of the album
     */
    Optional<Year> getYear();

    /**
     * @return list of all images of this album
     */
    List<LocalImage> getImages();
}
