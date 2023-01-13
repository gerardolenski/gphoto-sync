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
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

/**
 * IT test with full flow verification.
 */
@TestPropertySource(properties = {"gphotosync.filter.album.from-year=2021", "gphotosync.filter.album.to-year=2021"})
class GlobalConfiguredSyncAppServiceWithFiltersTest extends BaseSyncServiceIT {

    private static final String ALBUM_3 = "2021.12 - album 3";

    @Autowired
    private GlobalConfiguredSyncAppService syncService;
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
    @DisplayName("should upload only album from 2021 from local repository [positive]")
    void syncFlowForSubstituteOfAlbums() {
        //when
        var result = syncService.runSyncFlow();

        //then
        assertThat(result.getProcessedAlbumsCount())
                .isEqualTo(1);
        assertThat(result.getAlbumsChangesCount())
                .isEqualTo(1);
        assertThat(result.getSyncDetails())
                .containsExactly(createSuccessSyncResult(ALBUM_3, 9, 9));
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