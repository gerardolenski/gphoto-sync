package org.gol.photosync.domain.auth;

import com.google.auth.Credentials;

/**
 * Supplies the credentials for the connection to google cloud.
 */
@FunctionalInterface
public interface GoogleCredentialsSupplier {

    /**
     * @return {@link Credentials} object
     */
    Credentials getCredentials();

}
