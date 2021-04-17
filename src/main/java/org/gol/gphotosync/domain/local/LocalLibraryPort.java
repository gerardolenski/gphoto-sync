package org.gol.gphotosync.domain.local;

import org.gol.gphotosync.domain.model.LocalAlbum;
import org.gol.gphotosync.domain.model.LocalImage;

import java.util.List;

public interface LocalLibraryPort {
    List<LocalAlbum> findAlbums();

    List<LocalImage> getAlbumImages(LocalAlbum album);
}
