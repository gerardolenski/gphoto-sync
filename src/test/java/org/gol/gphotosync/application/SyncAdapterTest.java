package org.gol.gphotosync.application;

import com.google.photos.library.v1.PhotosLibraryClient;
import org.gol.gphotosync.domain.auth.GoogleCredentialsSupplier;
import org.gol.gphotosync.domain.google.GoogleAlbumRepository;
import org.gol.gphotosync.domain.google.GoogleClientFactory;
import org.gol.gphotosync.domain.google.GoogleMediaItemRepository;
import org.gol.gphotosync.domain.model.AlbumSyncResult;
import org.gol.gphotosync.domain.model.UploadStat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

/**
 * IT test with full flow verification.
 */
@SpringBootTest
@ActiveProfiles("test")
class SyncAdapterTest {

    private static final String ALBUM_1 = "2020.01 - album 1";
    private static final String ALBUM_2 = "2020.02 - album 2";
    private static final String ALBUM_3 = "2021.12 - album 3";
    private static final String ALBUM_4 = "album 4";

    @Autowired
    private SyncAdapter syncAdapter;
    @MockBean
    private GoogleCredentialsSupplier googleCredentialsSupplier;
    @MockBean
    private GoogleClientFactory googleClientFactory;
    @Mock
    private PhotosLibraryClient photosLibraryClient;

    @Autowired
    private InMemoryAlbumRepository inMemoryAlbumRepository;
    @Autowired
    private InMemoryMediaRepository inMemoryMediaRepository;

    @BeforeEach
    void init() {
        inMemoryAlbumRepository.reset();
        inMemoryMediaRepository.reset();
        doReturn(photosLibraryClient).when(googleClientFactory).getClient();
    }


    @Test
    @DisplayName("green flow - should upload all new albums from local repository [positive]")
    void greenFlow() {
        //when
        var result = syncAdapter.sync();

        //then
        assertThat(result.albumSyncResults())
                .containsExactlyInAnyOrder(
                        createSuccessSyncResult("2020.01 - album 1", 6, 6),
                        createSuccessSyncResult(ALBUM_2, 3, 3),
                        createSuccessSyncResult(ALBUM_3, 9, 9),
                        createSuccessSyncResult(ALBUM_4, 3, 3));

    }

    @Test
    @DisplayName("green flow - should upload missing images from local repository [positive]")
    void greenFlow2() {
        //given
        var album1 = inMemoryAlbumRepository.createAlbum(photosLibraryClient, ALBUM_1);
        inMemoryMediaRepository.addItems(album1.getId(), Set.of("test.bmp", "test.gif"));
        var album2 = inMemoryAlbumRepository.createAlbum(photosLibraryClient, ALBUM_2);
        inMemoryMediaRepository.addItems(album2.getId(), Set.of("img1.jpg", "img2.jpg", "img3.jpg"));
        inMemoryAlbumRepository.createAlbum(photosLibraryClient, ALBUM_3);
        inMemoryAlbumRepository.createAlbum(photosLibraryClient, ALBUM_4);

        //when
        var result = syncAdapter.sync();

        //then
        assertThat(result.albumSyncResults())
                .containsExactlyInAnyOrder(
                        createSuccessSyncResult(ALBUM_1, 6, 4),
                        createSuccessSyncResult(ALBUM_2, 3, 0),
                        createSuccessSyncResult(ALBUM_3, 9, 9),
                        createSuccessSyncResult(ALBUM_4, 3, 3));

    }

    private AlbumSyncResult createSuccessSyncResult(String title, long imagesCount, long missingImages) {
        return AlbumSyncResult.builder()
                .title(title)
                .imagesCount(imagesCount)
                .missingImages(missingImages)
                .uploadStats(missingImages > 0 ? List.of(new UploadStat("Success", missingImages)) : List.of())
                .build();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        GoogleAlbumRepository inMemoryAlbumRepository() {
            return new InMemoryAlbumRepository();
        }

        @Bean
        @Primary
        GoogleMediaItemRepository inMemoryMediaRepository() {
            return new InMemoryMediaRepository();
        }
    }
}