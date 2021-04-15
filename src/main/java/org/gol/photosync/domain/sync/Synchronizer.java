package org.gol.photosync.domain.sync;

import java.util.concurrent.Future;

public interface Synchronizer {
    Future<Void> invoke();
}
