package org.gol.gphotosync.domain.local.model;

import java.io.File;

/**
 * Represents the local image aggregate root.
 */
public interface LocalImage {

    /**
     * @return the name of the image file
     */
    String getFileName();

    /**
     * @return the mie type of the image e.g: image/bmp
     */
    String getMimeType();

    /**
     * @return the {@link File} object containing the image binary data
     */
    File getFile();

    /**
     * @return the description of the image, used when uploading to remote system
     */
    String getDescription();
}
