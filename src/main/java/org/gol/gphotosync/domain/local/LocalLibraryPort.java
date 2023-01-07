package org.gol.gphotosync.domain.local;

import java.util.List;

/**
 * Port for query local image albums library.
 */
public interface LocalLibraryPort {

    /**
     * Finds local albums meeting the filtering criteria.
     *
     * @param query the query containing filters
     * @return the list of found albums
     */
    List<LocalAlbum> findAlbums(AlbumFindQuery query);
}
