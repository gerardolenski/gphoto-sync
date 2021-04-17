package org.gol.gphotosync.domain.sync;

import org.gol.gphotosync.application.SyncResult;

public interface SyncPort {

    SyncResult sync();
}
