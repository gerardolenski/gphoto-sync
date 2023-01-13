package org.gol.gphotosync.infrastructure.property;

import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.config.LocalAlbumFiltersConfigPort;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Year;

/**
 * Holds global application filter configuration.
 */
@Slf4j
@ConfigurationProperties(prefix = "gphotosync.filter.album", ignoreInvalidFields = true)
record GlobalConfigLocalAlbumFilterProps(Year fromYear, Year toYear) implements LocalAlbumFiltersConfigPort {

    GlobalConfigLocalAlbumFilterProps {
        log.info("Initialized GLOBAL local library album filters properties: fromYear={}, toYear={}", fromYear, toYear);
    }
}
