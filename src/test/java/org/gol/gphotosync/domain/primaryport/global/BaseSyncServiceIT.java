package org.gol.gphotosync.domain.primaryport.global;

import org.gol.gphotosync.domain.remote.sync.model.AlbumSyncResult;
import org.gol.gphotosync.domain.remote.sync.model.UploadStat;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
abstract class BaseSyncServiceIT {

    AlbumSyncResult createSuccessSyncResult(String title, long imagesCount, long missingImages) {
        return AlbumSyncResult.builder()
                .title(title)
                .imagesCount(imagesCount)
                .missingImages(missingImages)
                .uploadStats(missingImages > 0 ? List.of(new UploadStat("Success", missingImages)) : List.of())
                .build();
    }
}
