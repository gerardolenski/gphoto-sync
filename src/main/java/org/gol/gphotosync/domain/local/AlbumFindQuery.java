package org.gol.gphotosync.domain.local;

import lombok.NonNull;

import java.util.List;

import static java.util.List.copyOf;

/**
 * The album query containing filters to apply.
 *
 * @param filters the filters which will be applied to album
 */
public record AlbumFindQuery(@NonNull List<LocalAlbumFilter> filters) {

    public AlbumFindQuery {
        filters = copyOf(filters);
    }
}
