package org.gol.gphotosync.infrastructure.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
enum Scope {
    READ("https://www.googleapis.com/auth/photoslibrary.readonly"),
    APPEND("https://www.googleapis.com/auth/photoslibrary.appendonly"),
    READ_AND_APPEND("https://www.googleapis.com/auth/photoslibrary"),
    EDIT("https://www.googleapis.com/auth/photoslibrary.edit.appcreateddata");

    private final String name;
}
