package org.gol.gphotosync.application;

import org.gol.gphotosync.domain.model.AlbumSyncResult;
import org.gol.gphotosync.domain.model.UploadStat;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
abstract class BaseSyncAdapterIT {

    AlbumSyncResult createSuccessSyncResult(String title, long imagesCount, long missingImages) {
        return AlbumSyncResult.builder()
                .title(title)
                .imagesCount(imagesCount)
                .missingImages(missingImages)
                .uploadStats(missingImages > 0 ? List.of(new UploadStat("Success", missingImages)) : List.of())
                .build();
    }
}
