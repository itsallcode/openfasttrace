package openfasttrack.importer;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import openfasttrack.importer.LineReader.LineConsumer;

public class TestLineReader
{
    private static final Path DUMMY_FILE = Paths.get("dummy");
    private static final String TEST_CONTENT_LINE_1 = "testContent äöüß";
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Mock
    private LineConsumer consumerMock;
    @Mock
    private LineNumberReader readerMock;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateForPathAndCharset() throws IOException
    {
        final Path tempFile = this.tempFolder.newFile().toPath();
        Files.write(tempFile, TEST_CONTENT_LINE_1.getBytes(StandardCharsets.UTF_8));

        LineReader.create(tempFile, StandardCharsets.UTF_8).readLines(this.consumerMock);

        assertLinesRead(TEST_CONTENT_LINE_1);
    }

    @Test
    public void testCreateForPathAndReaderReader() throws IOException
    {
        final Path tempFile = this.tempFolder.newFile().toPath();
        Files.write(tempFile, TEST_CONTENT_LINE_1.getBytes(StandardCharsets.UTF_8));

        LineReader.create(DUMMY_FILE, Files.newBufferedReader(tempFile))
                .readLines(this.consumerMock);

        assertLinesRead(TEST_CONTENT_LINE_1);
    }

    @Test
    public void testReadLinesEmptyFile()
    {
        readContent("");
        assertLinesRead();
    }

    @Test
    public void testReadLinesSingleLine()
    {
        readContent("line1");
        assertLinesRead("line1");
    }

    @Test
    public void testReadLinesSingleLineWithTrailingNewline()
    {
        readContent("line1\n");
        assertLinesRead("line1");
    }

    @Test
    public void testReadLinesTwoLinesWithCR()
    {
        readContent("line1\nline2");
        assertLinesRead("line1", "line2");
    }

    @Test
    public void testReadLinesTwoLinesWithLF()
    {
        readContent("line1\rline2");
        assertLinesRead("line1", "line2");
    }

    @Test
    public void testReadLinesTwoLinesWithLFCR()
    {
        readContent("line1\r\nline2");
        assertLinesRead("line1", "line2");
    }

    private void readContent(final String content)
    {
        LineReader.create(DUMMY_FILE, new StringReader(content))
                .readLines(this.consumerMock);
    }

    @Test
    public void testClose() throws IOException
    {
        when(this.readerMock.readLine()).thenReturn(null);
        try (LineReader lineReader = new LineReader(DUMMY_FILE, this.readerMock))
        {
            lineReader.readLines(this.consumerMock);
        }
        verify(this.readerMock).close();

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
