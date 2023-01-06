package org.gol.gphotosync.domain.local;

import lombok.NonNull;

import java.util.Set;

import static java.util.Set.copyOf;

/**
 * The album query containing filters to apply.
 *
 * @param filters the filters which will be applied to album
 */
public record AlbumFindQuery(@NonNull Set<LocalAlbumFilter> filters) {

    public AlbumFindQuery {
        filters = copyOf(filters);
    }
}
