package org.gol.photosync.infrastructure.auth;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.auth.Credentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.photos.library.v1.PhotosLibraryClient;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gol.photosync.domain.auth.GoogleCredentialsSupplier;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

import static java.lang.String.format;
import static org.gol.photosync.domain.util.LoggerUtils.formatEx;
import static org.gol.photosync.infrastructure.auth.Scope.*;

/**
 * Authorizes the installed application to access user's protected data in photo library service. Stores the credentials
 * which can be used to initialize the {@link PhotosLibraryClient} object.
 */
@Slf4j
@Service
class PhotoLibraryAuthService implements GoogleCredentialsSupplier {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GooglePhotoLibraryProperties photoLibraryProps;

    @Getter
    private final Credentials credentials;

    PhotoLibraryAuthService(GooglePhotoLibraryProperties photoLibraryProps) {
        this.photoLibraryProps = photoLibraryProps;
        this.credentials = retrieveCredentials();
    }

    private Credentials retrieveCredentials() {
        log.info("Authorizes the application using OAuth 2.0");

        var secrets = getClientSecrets();
        var flow = getAuthFlow(secrets);
        var receiver = getReceiver();

        return Try.of(() -> new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize(photoLibraryProps.getUserId()))
                .map(c -> UserCredentials.newBuilder()
                        .setClientId(secrets.getDetails().getClientId())
                        .setClientSecret(secrets.getDetails().getClientSecret())
                        .setRefreshToken(c.getRefreshToken())
                        .build())
                .onFailure(e -> log.error("Cannot authorize: {}", formatEx(e)))
                .onSuccess(c -> log.debug("Application was authorized successfully: clientId={}",
                        format("%s***", c.getClientId().substring(0, 3))))
                .get();
    }

    private GoogleClientSecrets getClientSecrets() {
        var credentialFilePath = Path.of(photoLibraryProps.getCredentialDir(), photoLibraryProps.getCredentialFile());
        log.debug("Load client secret: credentialFile={}", credentialFilePath);

        return Try.of(() -> GoogleClientSecrets.load(JSON_FACTORY, new FileReader(credentialFilePath.toFile())))
                .onFailure(e -> log.error("Cannot read client seecret: {}", formatEx(e)))
                .get();
    }

    private LocalServerReceiver getReceiver() {
        log.debug("Create local server receiver: port={}", photoLibraryProps.getLocalReceiverPort());
        return new LocalServerReceiver.Builder()
                .setPort(photoLibraryProps.getLocalReceiverPort())
                .build();
    }

    private GoogleAuthorizationCodeFlow getAuthFlow(GoogleClientSecrets clientSecrets) {
        var storePath = Path.of(photoLibraryProps.getCredentialDir(), "credentials");
        var scopes = List.of(READ.getName(), APPEND.getName(), EDIT.getName());
        log.debug("Init Google authorization flow: dataStoreDir={}, scopes={}", storePath, scopes);
        return Try.of(() ->
                new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JSON_FACTORY,
                        clientSecrets,
                        scopes)
                        .setDataStoreFactory(new FileDataStoreFactory(storePath.toFile()))
                        .setAccessType("offline")
                        .build())
                .onFailure(e -> log.error("Cannot init Google authorization flow: {}", formatEx(e)))
                .get();
    }
}
