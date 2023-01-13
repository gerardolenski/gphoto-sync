package org.gol.gphotosync.domain.primaryport.global;

import com.google.photos.library.v1.PhotosLibraryClient;
import org.gol.gphotosync.domain.google.GoogleCredentialsSupplier;
import org.gol.gphotosync.domain.google.GoogleAlbumRepository;
import org.gol.gphotosync.domain.google.GoogleClientFactory;
import org.gol.gphotosync.domain.google.GoogleMediaItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

/**
 * IT test with full flow verification.
 */
class GlobalConfiguredSyncAppServiceTest extends BaseSyncServiceIT {

    private static final String ALBUM_1 = "2020.01 - album 1";
    private static final String ALBUM_2 = "2020.02 - album 2";
    private static final String ALBUM_3 = "2021.12 - album 3";
    private static final String ALBUM_4 = "album 4";

    @Autowired
    private GlobalConfiguredSyncAppService sut;
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
    @DisplayName("should upload all new albums from local repository [positive]")
    void syncFlowForAllAlbums() {
        //when
        var result = sut.runSyncFlow();

        //then
        assertThat(result.getProcessedAlbumsCount())
                .isEqualTo(4);
        assertThat(result.getAlbumsChangesCount())
                .isEqualTo(4);
        assertThat(result.getSyncDetails())
                .containsExactlyInAnyOrder(
                        createSuccessSyncResult(ALBUM_1, 6, 6),
                        createSuccessSyncResult(ALBUM_2, 3, 3),
                        createSuccessSyncResult(ALBUM_3, 9, 9),
                        createSuccessSyncResult(ALBUM_4, 3, 3));
        assertThat(result.withSyncChangesOnly().getSyncDetails())
                .containsExactlyInAnyOrder(
                        createSuccessSyncResult(ALBUM_1, 6, 6),
                        createSuccessSyncResult(ALBUM_2, 3, 3),
                        createSuccessSyncResult(ALBUM_3, 9, 9),
                        createSuccessSyncResult(ALBUM_4, 3, 3));
    }

    @Test
    @DisplayName("should upload missing images from local repository [positive]")
    void syncFlowForMissingImages() {
        //given
        var album1 = inMemoryAlbumRepository.createAlbum(photosLibraryClient, ALBUM_1);
        inMemoryMediaRepository.addItems(album1.getId(), Set.of("test.bmp", "test.gif"));
        var album2 = inMemoryAlbumRepository.createAlbum(photosLibraryClient, ALBUM_2);
        inMemoryMediaRepository.addItems(album2.getId(), Set.of("img1.jpg", "img2.jpg", "img3.jpg"));
        inMemoryAlbumRepository.createAlbum(photosLibraryClient, ALBUM_3);
        inMemoryAlbumRepository.createAlbum(photosLibraryClient, ALBUM_4);

        //when
        var result = sut.runSyncFlow();

        //then
        assertThat(result.getProcessedAlbumsCount())
                .isEqualTo(4);
        assertThat(result.getAlbumsChangesCount())
                .isEqualTo(3);
        assertThat(result.getSyncDetails())
                .containsExactlyInAnyOrder(
                        createSuccessSyncResult(ALBUM_1, 6, 4),
                        createSuccessSyncResult(ALBUM_2, 3, 0),
                        createSuccessSyncResult(ALBUM_3, 9, 9),
                        createSuccessSyncResult(ALBUM_4, 3, 3));
        assertThat(result.withSyncChangesOnly().getSyncDetails())
                .containsExactlyInAnyOrder(
                        createSuccessSyncResult(ALBUM_1, 6, 4),
                        createSuccessSyncResult(ALBUM_3, 9, 9),
                        createSuccessSyncResult(ALBUM_4, 3, 3));
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