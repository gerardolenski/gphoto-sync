package org.gol.gphotosync.domain.local.filter;

import lombok.Builder;
import org.gol.gphotosync.domain.local.model.LocalAlbum;
import org.gol.gphotosync.domain.local.model.LocalImage;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Builder
record TestLocalAlbum(String title, Year year, List<LocalImage> images) implements LocalAlbum {
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Optional<Year> getYear() {
        return ofNullable(year);
    }

    @Override
    public List<LocalImage> getImages() {
        return images;
    }
}
