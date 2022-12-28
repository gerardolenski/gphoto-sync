package org.gol.gphotosync.domain.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gol.gphotosync.domain.util.ImageUtils.getImageMimeType;
import static org.gol.gphotosync.domain.util.ImageUtils.isImage;

class ImageUtilsTest {

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("imageProvider")
    @DisplayName("should detect image [positive]")
    void testImages(String testCase, Path imagePath) {
        assertThat(isImage(imagePath))
                .isTrue();
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("imageProvider")
    @DisplayName("should detect correct image MIME type [positive]")
    void testImagesMimeType(String testCase, Path imagePath, String expectedMimeType) {
        assertThat(getImageMimeType(imagePath))
                .isNotEmpty()
                .get()
                .asString()
                .startsWith("image/")
                .contains(expectedMimeType);
    }

    private static Stream<Arguments> imageProvider() {
        return Stream.of(
                Arguments.of("bmp", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.bmp"), "bmp"),
                Arguments.of("gif", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.gif"), "gif"),
                Arguments.of("jpeg", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.jpeg"), "jpeg"),
                Arguments.of("jpg", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.jpg"), "jpeg"),
                Arguments.of("png", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.png"), "png"),
                Arguments.of("tiff", Path.of("src/test/resources/library/2020/2020.01 - album 1/test.tiff"), "tiff"));
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("notImageProvider")
    @DisplayName("should not detect image [negative]")
    void testNotImages(String testCase, Path filePath) {
        assertThat(isImage(filePath))
                .isFalse();
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("notImageProvider")
    @DisplayName("should not found image MIME type [negative]")
    void testNotImagesMimeType(String testCase, Path filePath) {
        assertThat(getImageMimeType(filePath))
                .isEmpty();
    }

    private static Stream<Arguments> notImageProvider() {
        return Stream.of(
                Arguments.of("txt file 1", Path.of("src/test/resources/library/not-album/file")),
                Arguments.of("txt file 2", Path.of("src/test/resources/library/not-album/file.txt")),
                Arguments.of("binary file", Path.of("src/test/resources/library/not-album/file.bin")));
    }
}