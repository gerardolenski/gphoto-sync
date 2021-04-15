package org.gol.photosync.domain.library;

import org.gol.photosync.domain.model.LocalAlbum;

import java.nio.file.Path;
import java.util.List;

public interface LocalLibrary {
    List<Path> findAlbums();
    LocalAlbum getAlbum(Path path);
}
