package org.gol.gphotosync.domain.config;

import java.time.Year;

/**
 * Holding the configuration of local album filters.
 */
public interface LocalAlbumFiltersConfigPort {

    /**
     * @return the year of album synchronization start; can be null
     */
    Year fromYear();

    /**
     * @return the year of album synchronization end; can be null
     */
    Year toYear();
}
