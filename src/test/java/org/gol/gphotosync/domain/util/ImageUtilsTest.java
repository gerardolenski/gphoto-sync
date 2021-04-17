package org.gol.gphotosync.domain.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ImageUtilsTest {

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("imageProvider")
    void testImages(String testCase, Path imagePath) {
        assertThat(ImageUtils.isImage(imagePath))
                .isTrue();
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("imageProvider")
    void testImagesMimeType(String testCase, Path imagePath, String expectedMimeType) {
        assertThat(ImageUtils.getImageMimeType(imagePath))
                .isNotEmpty()
                .get()
                .isEqualTo(expectedMimeType);
    }

    private static Stream<Arguments> imageProvider() {
        return Stream.of(
                Arguments.of("bmp", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.bmp"), "image/x-ms-bmp"),
                Arguments.of("gif", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.gif"), "image/gif"),
                Arguments.of("jpeg", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.jpeg"), "image/jpeg"),
                Arguments.of("jpg", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.jpg"), "image/jpeg"),
                Arguments.of("png", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.png"), "image/png"),
                Arguments.of("tiff", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.tiff"), "image/tiff"));
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("notImageProvider")
    void testNotImages(String testCase, Path filePath) {
        assertThat(ImageUtils.isImage(filePath))
                .isFalse();
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("notImageProvider")
    void testNotImagesMimeType(String testCase, Path filePath) {
        assertThat(ImageUtils.getImageMimeType(filePath))
                .isEmpty();
    }

    private static Stream<Arguments> notImageProvider() {
        return Stream.of(
                Arguments.of("txt file 1", Path.of("src/test/resources/library/not-album/file")),
                Arguments.of("txt file 2", Path.of("src/test/resources/library/not-album/file.txt")),
                Arguments.of("binary file", Path.of("src/test/resources/library/not-album/file.bin")));
    }
}