package org.gol.gphotosync.domain.local.filter;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

import static lombok.AccessLevel.PRIVATE;

/**
 * Filtering utilities and helpers.
 */
@Slf4j
@NoArgsConstructor(access = PRIVATE)
class FilterUtils {

    static final Predicate<Integer> IS_YEAR_DEFINED = year -> year >= 0;
}
