package org.itsallcode.openfasttrace.importer.lightweightmarkup.linereader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.*;

import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineReaderTest
{
    private static final String FILE_PATH = "file/path";

    @Mock
    LineReaderCallback callbackMock;
    InOrder inOrder;

    @BeforeEach
    void setUp()
    {
        inOrder = inOrder(callbackMock);
    }

    @AfterEach
    void verifyNoMoreInteractions()
    {
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "", "\n", "\r\n", "\n\r" })
    void testReadEmptyFile(final String content) throws IOException
    {
        parse(content);
        inOrder.verify(callbackMock).finishReading();
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "", "\n", "\r\n", })
    void testReadSingleLine(final String lineEnding) throws IOException
    {
        parse("line1" + lineEnding);
        inOrder.verify(callbackMock).nextLine(new LineContext(1, null, "line1", null));
        inOrder.verify(callbackMock).finishReading();
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "\n", "\r\n" })
    void testReadTwoLines(final String lineEnding) throws IOException
    {
        parse("line1" + lineEnding + "line2" + lineEnding);
        inOrder.verify(callbackMock).nextLine(new LineContext(1, null, "line1", "line2"));
        inOrder.verify(callbackMock).nextLine(new LineContext(2, "line1", "line2", null));
        inOrder.verify(callbackMock).finishReading();
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "\n", "\r\n" })
    void testReadThreeLines(final String lineEnding) throws IOException
    {
        parse("line1" + lineEnding + "line2" + lineEnding + "line3" + lineEnding);
        inOrder.verify(callbackMock).nextLine(new LineContext(1, null, "line1", "line2"));
        inOrder.verify(callbackMock).nextLine(new LineContext(2, "line1", "line2", "line3"));
        inOrder.verify(callbackMock).nextLine(new LineContext(3, "line2", "line3", null));
        inOrder.verify(callbackMock).finishReading();
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "\n", "\r\n" })
    void testReadFourLines(final String lineEnding) throws IOException
    {
        parse("line1" + lineEnding + "line2" + lineEnding + "line3" + lineEnding + "line4");
        inOrder.verify(callbackMock).nextLine(new LineContext(1, null, "line1", "line2"));
        inOrder.verify(callbackMock).nextLine(new LineContext(2, "line1", "line2", "line3"));
        inOrder.verify(callbackMock).nextLine(new LineContext(3, "line2", "line3", "line4"));
        inOrder.verify(callbackMock).nextLine(new LineContext(4, "line3", "line4", null));
        inOrder.verify(callbackMock).finishReading();
    }

    @Test
    void testReadFails(@Mock final BufferedReader readerMock) throws IOException
    {
        when(readerMock.readLine()).thenThrow(new IOException("mock"));
        final ImporterException exception = assertThrows(ImporterException.class, () -> parse(readerMock));
        assertThat(exception.getMessage(), equalTo("Error reading '" + FILE_PATH + "' at line 0: mock"));
    }

    private void parse(final String content) throws IOException
    {
        final BufferedReader reader = new BufferedReader(new StringReader(content));
        parse(reader);
    }

    private void parse(final BufferedReader reader) throws IOException
    {
        final InputFile inputFileMock = mock(InputFile.class);
        lenient().when(inputFileMock.getPath()).thenReturn(FILE_PATH);
        when(inputFileMock.createReader()).thenReturn(reader);
        new LineReader(inputFileMock, callbackMock).readFile();
    }
}
