package org.gol.photosync.domain.util;

import lombok.NoArgsConstructor;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;

/**
 * Common Utility for logging
 */
@NoArgsConstructor(access = PRIVATE)
public final class LoggerUtils {

    /**
     * Formats the exception details for logging without the stack trace.
     *
     * @param e the exception object
     * @return the formatted exception as string
     */
    public static String formatEx(Throwable e) {
        return format("%s %s", e.getClass().getSimpleName(), e.getMessage());
    }
}
