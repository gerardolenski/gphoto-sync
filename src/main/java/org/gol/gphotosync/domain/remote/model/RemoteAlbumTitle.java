package org.gol.gphotosync.domain.remote.model;

import lombok.NonNull;

/**
 * Value Object representing tile of the remote album.
 *
 * @param value the string representation of title
 */
public record RemoteAlbumTitle(@NonNull String value) {

    public boolean sameAs(String title) {
        return value.equals(title);
    }
}
