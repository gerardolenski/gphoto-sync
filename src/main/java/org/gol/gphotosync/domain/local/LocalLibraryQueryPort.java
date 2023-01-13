package org.gol.gphotosync.domain.local;

import org.gol.gphotosync.domain.local.model.AlbumFindQuery;
import org.gol.gphotosync.domain.local.model.LocalAlbum;

import java.util.List;

/**
 * Secondary port for query local image albums library.
 */
public interface LocalLibraryQueryPort {

    /**
     * Finds local albums meeting the filtering criteria.
     *
     * @param query the query containing filters
     * @return the list of found albums
     */
    List<LocalAlbum> findAlbums(AlbumFindQuery query);
}
