package org.gol.gphotosync.infrastructure.library.filesystem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.file.Files.readAllBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.gol.gphotosync.infrastructure.library.filesystem.FileSystemImageFactory.toLocalImage;

class FileSystemImageTest {

    private static final Path IMAGE_PATH = Path.of("src/test/resources/library/2020/2020.01 - album 1/test.bmp");
    private static final Path NOT_IMAGE_PATH = Path.of("src/test/resources/library/album 4/file");
    private static final Path NOT_EXISTENT_PATH = Path.of("not/existent/path");

    @Test
    @DisplayName("should lode image from correct file [positive]")
    void shouldLoadImage() {
        //when
        var imgOptional = toLocalImage(IMAGE_PATH);

        //then
        assertThat(imgOptional)
                .isNotEmpty()
                .get()
                .satisfies(img -> {
                    assertThat(img.getFileName()).isEqualTo("test.bmp");
                    assertThat(img.getMimeType()).isEqualTo("image/bmp");
                    assertThat(img.getDescription()).isEqualTo("2020.01 - album 1: test.bmp");
                    assertThat(img.getFile())
                            .binaryContent()
                            .isEqualTo(readAllBytes(IMAGE_PATH));
                });
    }

    @ParameterizedTest(name = "{index}. {0}")
    @DisplayName("should not load invalid image file")
    @MethodSource("invalidImgSupplier")
    void name(Path path) {
        //when, then
        assertThat(toLocalImage(path))
                .isEmpty();
    }

    public static Stream<Arguments> invalidImgSupplier() {
        return Stream.of(
                Arguments.of("not image file", NOT_IMAGE_PATH),
                Arguments.of("not existent file", NOT_EXISTENT_PATH)
        );
    }
}