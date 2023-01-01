package org.gol.gphotosync.domain.sync;

import java.util.concurrent.CompletableFuture;

public interface Synchronizer<T> {
    CompletableFuture<T> invoke();
}
