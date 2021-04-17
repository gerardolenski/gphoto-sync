package org.gol.photosync.domain.library;

import org.gol.photosync.domain.model.LocalAlbum;
import org.gol.photosync.domain.model.LocalImage;

import java.util.List;

public interface LocalLibraryPort {
    List<LocalAlbum> findAlbums();

    List<LocalImage> getAlbumImages(LocalAlbum album);
}
