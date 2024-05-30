package org.itsallcode.openfasttrace.importer.markdown;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestMarkdownImporter
{
    // [utest->dsn~md.specification-item-id-format~3]
    @ParameterizedTest
    @CsvSource(
    { "req~foo~1<a id=\"req~foo~1\"></a>", "a~b~0", "req~test~1",
            "req~test~999", "req~test.requirement~1", "req~test_underscore~1",
            "`req~test1~1`arbitrary text",
            // See https://github.com/itsallcode/openfasttrace/issues/366
            "req~zellzustandsänderung~1", "req~öäüßÖÄ~1"
    })
    void testIdentifyId(final String text)
    {
        MarkdownAsserts.assertMatch(MdPattern.ID, text);
    }

    // [utest->dsn~md.specification-item-id-format~3]
    @ParameterizedTest
    @CsvSource(
    { "test~1", "req-test~1", "req~4test~1", "räq~test~1" })
    void testIdentifyNonId(final String text)
    {
        MarkdownAsserts.assertMismatch(MdPattern.ID, text);
    }

    // [utest->dsn~md.specification-item-title~1]
    @ParameterizedTest
    @CsvSource(
    { "#Title", "# Title", "###### Title", "#   Title", "# Änderung" })
    void testIdentifyTitle(final String text)
    {
        MarkdownAsserts.assertMatch(MdPattern.TITLE, text);
    }

    // [utest->dsn~md.specification-item-title~1]
    @ParameterizedTest
    @CsvSource(
    { "Title", "Title #", "' # Title'" })
    void testIdentifyNonTitle(final String text)
    {
        MarkdownAsserts.assertMismatch(MdPattern.TITLE, text);
    }

    @ParameterizedTest
    @CsvSource(
    { "Needs: req, dsn", "Needs:req,dsn", "'Needs:  \treq , dsn '" })
    void testIdentifyNeeds(final String text)
    {
        MarkdownAsserts.assertMatch(MdPattern.NEEDS_INT, text);
    }

    @ParameterizedTest
    @CsvSource(
    { "Needs:", "#Needs: abc", "' Needs: abc'", "Needs: önderung" })
    void testIdentifyNonNeeds(final String text)
    {
        MarkdownAsserts.assertMismatch(MdPattern.NEEDS_INT, text);
    }

    @ParameterizedTest
    @CsvSource(
    { "Tags: req, dsn", "Tags:req,dsn", "'Tags:  \treq , dsn '" })
    void testIdentifyTags(final String text)
    {
        MarkdownAsserts.assertMatch(MdPattern.TAGS_INT, text);
    }

    @ParameterizedTest
    @CsvSource(
    { "Tags:", "#Needs: abc", "' Needs: abc'", "Needs: änderung" })
    void testIdentifyNonTags(final String text)
    {
        MarkdownAsserts.assertMismatch(MdPattern.TAGS_INT, text);
    }

    @Test
    void testRunImportHandlesIOException(@Mock final InputFile fileMock, @Mock final ImportEventListener listenerMock,
            @Mock final BufferedReader readerMock) throws IOException
    {
        when(fileMock.getPath()).thenReturn("/the/file");
        when(fileMock.createReader()).thenReturn(readerMock);
        when(readerMock.readLine()).thenThrow(new IOException("Dummy exception"));
        final MarkdownImporter importer = new MarkdownImporter(fileMock, listenerMock);
        final ImporterException exception = assertThrows(ImporterException.class, importer::runImport);
        assertThat(exception.getMessage(), equalTo("Error reading '/the/file' at line 0: Dummy exception"));
    }
}
