package org.gol.gphotosync.application.cmdrunner;

import org.gol.gphotosync.domain.primaryport.GlobalConfiguredLibrarySyncPort;
import org.gol.gphotosync.domain.primaryport.model.LibrarySyncResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OneTimeFlowInvocationAdapterTest {

    @Mock
    private GlobalConfiguredLibrarySyncPort syncPort;
    @Spy
    @InjectMocks
    private OneTimeFlowInvocationAdapter sut;

    @BeforeEach
    void init() {
        doReturn(new LibrarySyncResult(1000, List.of()))
                .when(syncPort)
                .runSyncFlow();
        doNothing().when(sut).terminateApplication();
    }

    @Test
    @DisplayName("should run sync flow then exit application [positive]")
    void shouldRunFlowThenExit() {
        //when
        sut.runThenExit();

        //then
        verify(syncPort).runSyncFlow();
        verify(sut).terminateApplication();
    }
}