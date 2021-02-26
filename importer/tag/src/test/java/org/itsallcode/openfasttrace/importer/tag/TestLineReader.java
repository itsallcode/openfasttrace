package org.itsallcode.openfasttrace.importer.tag;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 hamstercommunity
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import static org.mockito.Mockito.inOrder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.importer.tag.LineReader.LineConsumer;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestLineReader
{
    private static final Path DUMMY_FILE = Paths.get("dummy");
    private static final String TEST_CONTENT_LINE_1 = "testContent äöüß";

    @Mock
    private LineConsumer consumerMock;
    @Mock
    private BufferedReader readerMock;
    private Path tempDir;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir)
    {
        this.tempDir = tempDir;
    }

    @Test
    void testCreateForPathAndCharset() throws IOException
    {
        final Path tempFile = this.tempDir.resolve("test");
        Files.write(tempFile, TEST_CONTENT_LINE_1.getBytes(StandardCharsets.UTF_8));
        LineReader.create(RealFileInput.forPath(tempFile)).readLines(this.consumerMock);
        assertLinesRead(TEST_CONTENT_LINE_1);
    }

    @Test
    void testCreateForPathAndReaderReader() throws IOException
    {
        final Path tempFile = this.tempDir.resolve("test");
        Files.write(tempFile, TEST_CONTENT_LINE_1.getBytes(StandardCharsets.UTF_8));
        LineReader.create(StreamInput.forReader(DUMMY_FILE, Files.newBufferedReader(tempFile)))
                .readLines(this.consumerMock);
        assertLinesRead(TEST_CONTENT_LINE_1);
    }

    @Test
    void testReadLinesEmptyFile()
    {
        readContent("");
        assertLinesRead();
    }

    @Test
    void testReadLinesSingleLine()
    {
        readContent("line1");
        assertLinesRead("line1");
    }

    @Test
    void testReadLinesSingleLineWithTrailingNewline()
    {
        readContent("line1\n");
        assertLinesRead("line1");
    }

    @Test
    void testReadLinesTwoLinesWithCR()
    {
        readContent("line1\nline2");
        assertLinesRead("line1", "line2");
    }

    @Test
    void testReadLinesTwoLinesWithLF()
    {
        readContent("line1\rline2");
        assertLinesRead("line1", "line2");
    }

    @Test
    void testReadLinesTwoLinesWithLFCR()
    {
        readContent("line1\r\nline2");
        assertLinesRead("line1", "line2");
    }

    private void readContent(final String content)
    {
        final InputFile file = StreamInput.forReader(DUMMY_FILE,
                new BufferedReader(new StringReader(content)));
        LineReader.create(file).readLines(this.consumerMock);
    }

    private void assertLinesRead(final String... expectedLines)
    {
        final InOrder inOrder = inOrder(this.consumerMock);
        int lineNumber = 1;
        for (final String line : expectedLines)
        {
            inOrder.verify(this.consumerMock).readLine(lineNumber, line);
            lineNumber++;
        }
        inOrder.verifyNoMoreInteractions();
    }
}