package org.gol.gphotosync.domain.local.model;

import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Represents the local album aggregate root.
 */
public interface LocalAlbum {

    /**
     * @return the value of the album
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
