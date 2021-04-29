package org.gol.gphotosync.domain.util;

import io.vavr.control.Try;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

import static lombok.AccessLevel.PRIVATE;

/**
 * Utilities for async calls.
 */
@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class AsyncUtils {

    /**
     * Retrieves the result of the future with checked exception handling.
     *
     * @param future the future
     * @param <T>    the type of result
     * @return the result
     */
    public static <T> T getFutureResult(Future<T> future) {
        return Try.of(future::get)
                .onFailure(e -> log.error("Error occurred while retrieving future result.", e))
                .get();
    }
}
