package org.gol.gphotosync.domain.local;

/**
 * Interface for implementing any filter applied to {@link LocalAlbum} object.
 */
@FunctionalInterface
public interface LocalAlbumFilter {

    /**
     * Decides if the {@link LocalAlbum} object should be processed.
     *
     * @param album the {@link LocalAlbum} object which is
     * @return true if album passes the filter, otherwise false
     */
    boolean shouldBeProcessed(LocalAlbum album);
}
