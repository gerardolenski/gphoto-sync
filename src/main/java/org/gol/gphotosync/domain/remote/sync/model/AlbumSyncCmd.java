package org.gol.gphotosync.domain.remote.sync.model;

import lombok.NonNull;
import org.gol.gphotosync.domain.local.model.LocalAlbum;

import java.util.List;

import static java.util.List.copyOf;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Command containing local albums to synchronize with remote library.
 */
public record AlbumSyncCmd(@NonNull List<LocalAlbum> localAlbums) {

    public AlbumSyncCmd {
        localAlbums = copyOf(localAlbums);
    }

    private void validateNotEmpty(List<LocalAlbum> localAlbums) {
        if (isEmpty(localAlbums)) {
            throw new IllegalArgumentException("Local album list must contain at least one entry.");
        }
    }
}
