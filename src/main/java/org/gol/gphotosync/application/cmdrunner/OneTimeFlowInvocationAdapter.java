package org.gol.gphotosync.application.cmdrunner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.primaryport.GlobalConfiguredLibrarySyncPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static java.lang.System.exit;

/**
 * Runs flow only once when application context is ready then exits with status 0.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "gphotosync", name = "single-runner-activated", havingValue = "true")
class OneTimeFlowInvocationAdapter {

    private final GlobalConfiguredLibrarySyncPort flowPort;

    @EventListener(ApplicationReadyEvent.class)
    void runThenExit() {
        log.info("Running flow one time: the flow will be invoked once then application will exit.");
        var result = flowPort.runSyncFlow();
        log.info("Library synchronization result: {}", result.withSyncChangesOnly());
        terminateApplication();
    }

    void terminateApplication() {
        exit(0);
    }
}
