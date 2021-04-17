package org.gol.photosync.domain.sync;

import org.gol.photosync.application.SyncResult;

public interface SyncPort {

    SyncResult sync();
}
