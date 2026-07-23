package org.itsallcode.openfasttrace.importer.tag.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestLineReader {
    private static final Path DUMMY_FILE = Paths.get("dummy");
    private static final String TEST_CONTENT_LINE_1 = "testContent äöüß";

    @Mock
    private LineConsumer consumerMock;
    private Path tempDir;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir) {
        this.tempDir = tempDir;
    }

    @Test
    void testCreateForPathAndCharset() throws IOException {
        final Path tempFile = this.tempDir.resolve("test");
        Files.write(tempFile, TEST_CONTENT_LINE_1.getBytes(StandardCharsets.UTF_8));
        LineReader.create(RealFileInput.forPath(tempFile)).readLines(this.consumerMock);
        assertLinesRead(TEST_CONTENT_LINE_1);
    }

    @Test
    void testCreateForPathAndReaderReader() throws IOException {
        final Path tempFile = this.tempDir.resolve("test");
        Files.write(tempFile, TEST_CONTENT_LINE_1.getBytes(StandardCharsets.UTF_8));
        LineReader.create(StreamInput.forReader(DUMMY_FILE, Files.newBufferedReader(tempFile)))
                .readLines(this.consumerMock);
        assertLinesRead(TEST_CONTENT_LINE_1);
    }

    @Test
    void testReadLinesEmptyFile() {
        readContent("");
        assertLinesRead();
    }

    @Test
    void testReadLinesSingleLine() {
        readContent("line1");
        assertLinesRead("line1");
    }

    @Test
    void testReadLinesSingleLineWithTrailingNewline() {
        readContent("line1\n");
        assertLinesRead("line1");
    }

    @SuppressWarnings("java:S5976")
    @Test
    void testReadLinesTwoLinesWithCR() {
        readContent("line1\nline2");
        assertLinesRead("line1", "line2");
    }

    @Test
    void testReadLinesTwoLinesWithLF() {
        readContent("line1\rline2");
        assertLinesRead("line1", "line2");
    }

    @Test
    void testReadLinesTwoLinesWithLFCR() {
        readContent("line1\r\nline2");
        assertLinesRead("line1", "line2");
    }

    @Test
    void wrapsConsumerFailureWithLineInformation() {
        final RuntimeException cause = new IllegalArgumentException("invalid line");
        doThrow(cause).when(this.consumerMock).readLine(1, "line1");

        final ImporterException exception = assertThrows(ImporterException.class, () -> readContent("line1"));

        assertAll(
                () -> assertThat(exception.getMessage(),
                        equalTo("Error processing line dummy:1 'line1': " + cause)),
                () -> assertThat(exception.getCause(), sameInstance(cause)));
    }

    @Test
    void wrapsReaderCreationFailure() throws IOException {
        final InputFile file = mock(InputFile.class);
        final IOException cause = new IOException("cannot read");
        when(file.createReader()).thenThrow(cause);
        when(file.toString()).thenReturn("unreadable.file");

        final ImporterException exception = assertThrows(ImporterException.class,
                () -> LineReader.create(file).readLines(this.consumerMock));

        assertAll(
                () -> assertThat(exception.getMessage(), equalTo("Error reading \"unreadable.file\" at line 0")),
                () -> assertThat(exception.getCause(), sameInstance(cause)));
    }

    private void readContent(final String content) {
        final InputFile file = StreamInput.forReader(DUMMY_FILE,
                new BufferedReader(new StringReader(content)));
        LineReader.create(file).readLines(this.consumerMock);
    }

    private void assertLinesRead(final String... expectedLines) {
        final InOrder inOrder = inOrder(this.consumerMock);
        int lineNumber = 1;
        for (final String line : expectedLines) {
            inOrder.verify(this.consumerMock).readLine(lineNumber, line);
            lineNumber++;
        }
        inOrder.verifyNoMoreInteractions();
    }
}
