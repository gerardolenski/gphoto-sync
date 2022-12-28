package org.gol.gphotosync.domain.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsyncUtilsTest {

    @Test
    @DisplayName("should retrieved future results from [positive]")
    void getFutureResultPositive() {
        //given
        var executor = newFixedThreadPool(2);

        //when
        var results = rangeClosed(1, 100)
                .mapToObj(i -> executor.submit(() -> randomAlphabetic(10)))
                .map(AsyncUtils::getFutureResult)
                .collect(toList());

        //then
        assertThat(results)
                .hasSize(100)
                .allSatisfy(s -> assertThat(s).hasSize(10));
    }

    @Test
    @DisplayName("should throw execution exception with correct cause [negative]")
    void getFutureResultNegative() {
        //given
        var executor = newSingleThreadExecutor();
        var future = executor.submit(() -> {
            throw new IOException("error");
        });

        //when, then
        assertThatThrownBy(() -> AsyncUtils.getFutureResult(future))
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IOException.class);
    }
}