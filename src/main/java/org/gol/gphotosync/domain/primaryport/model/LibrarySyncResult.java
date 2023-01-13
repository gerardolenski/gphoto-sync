package org.gol.gphotosync.domain.primaryport.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.gol.gphotosync.domain.remote.sync.model.AlbumSyncResult;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.List.copyOf;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * Value Object holding result of whole library synchronization.
 */
@Getter
@ToString(doNotUseGetters = true)
@RequiredArgsConstructor(access = PRIVATE)
public class LibrarySyncResult {

    private static final Predicate<AlbumSyncResult> HAS_UPLOAD_STATS = r -> isNotEmpty(r.uploadStats());
    private static final Predicate<AlbumSyncResult> WAS_INTERRUPTED = AlbumSyncResult::syncInterrupted;

    private final long processedAlbumsCount;
    private final long albumsChangesCount;
    private final Duration synchronisationTime;
    @With(PRIVATE)
    private final List<AlbumSyncResult> syncDetails;

    public LibrarySyncResult(long syncTimeInMillis, List<AlbumSyncResult> albumSyncResults) {
        this.syncDetails = copyOf(albumSyncResults);
        this.processedAlbumsCount = syncDetails.size();
        this.albumsChangesCount = syncDetails.stream()
                .filter(HAS_UPLOAD_STATS.or(WAS_INTERRUPTED))
                .count();
        this.synchronisationTime = Duration.of(syncTimeInMillis, MILLIS);
    }

    /**
     * @return only changed album synchronization results comparing to remote library
     */
    public LibrarySyncResult withSyncChangesOnly() {
        return this.withSyncDetails(syncDetails.stream()
                .filter(HAS_UPLOAD_STATS.or(WAS_INTERRUPTED))
                .toList());
    }
}
