package org.itsallcode.openfasttrace.importer.markdown;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.*;
import java.nio.file.Paths;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestMarkdownImporter
{
    private static final String FILENAME = "file name";

    @Mock
    ImportEventListener listenerMock;

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
    void testFindRequirement()
    {
        final String completeItem = createCompleteSpecificationItemInMarkdownFormat();
        runImporterOnText(completeItem);
        assertAllImporterEventsCalled();
    }

    // [utest->dsn~md.needs-coverage-list-compact~1]
    private String createCompleteSpecificationItemInMarkdownFormat()
    {
        return "# " + MarkdownTestConstants.TITLE //
                + "\n" //
                + "`" + MarkdownTestConstants.ID1 + "` <a id=\"" + MarkdownTestConstants.ID1 + "\"></a>" //
                + "\n" //
                + MarkdownTestConstants.DESCRIPTION_LINE1 + "\n" //
                + MarkdownTestConstants.DESCRIPTION_LINE2 + "\n" //
                + MarkdownTestConstants.DESCRIPTION_LINE3 + "\n" //
                + "\nRationale:\n" //
                + MarkdownTestConstants.RATIONALE_LINE1 + "\n" //
                + MarkdownTestConstants.RATIONALE_LINE2 + "\n" //
                + "\nCovers:\n\n" //
                + "  * " + MarkdownTestConstants.COVERED_ID1 + "\n" //
                + " + " + "[Link to baz2](#" + MarkdownTestConstants.COVERED_ID2 + ")\n" //
                + "\nDepends:\n\n" //
                + "  + " + MarkdownTestConstants.DEPENDS_ON_ID1 + "\n" //
                + "  - " + MarkdownTestConstants.DEPENDS_ON_ID2 + "\n" //
                + "\nComment:\n\n" //
                + MarkdownTestConstants.COMMENT_LINE1 + "\n" //
                + MarkdownTestConstants.COMMENT_LINE2 + "\n" //
                + "\nNeeds: " + MarkdownTestConstants.NEEDS_ARTIFACT_TYPE1 //
                + " , " + MarkdownTestConstants.NEEDS_ARTIFACT_TYPE2 + " ";
    }

    private void runImporterOnText(final String text)
    {
        final BufferedReader reader = new BufferedReader(new StringReader(text));
        final InputFile file = StreamInput.forReader(Paths.get(FILENAME), reader);
        final Importer importer = new MarkdownImporterFactory().createImporter(file,
                this.listenerMock);
        importer.runImport();
    }

    // [utest->dsn~md.covers-list~1]
    // [utest->dsn~md.depends-list~1]
    // [utest->dsn~md.requirement-references~1]
    private void assertAllImporterEventsCalled()
    {
        final InOrder inOrder = inOrder(this.listenerMock);
        inOrder.verify(this.listenerMock).beginSpecificationItem();
        inOrder.verify(this.listenerMock).setId(MarkdownTestConstants.ID1);
        inOrder.verify(this.listenerMock).setLocation(FILENAME, 2);
        inOrder.verify(this.listenerMock).setTitle(MarkdownTestConstants.TITLE);
        inOrder.verify(this.listenerMock).appendDescription(MarkdownTestConstants.DESCRIPTION_LINE1);
        inOrder.verify(this.listenerMock).appendDescription(System.lineSeparator());
        inOrder.verify(this.listenerMock).appendDescription(MarkdownTestConstants.DESCRIPTION_LINE2);
        inOrder.verify(this.listenerMock).appendDescription(System.lineSeparator());
        inOrder.verify(this.listenerMock).appendDescription(MarkdownTestConstants.DESCRIPTION_LINE3);
        inOrder.verify(this.listenerMock).appendRationale(MarkdownTestConstants.RATIONALE_LINE1);
        inOrder.verify(this.listenerMock).appendRationale(MarkdownTestConstants.RATIONALE_LINE2);
        inOrder.verify(this.listenerMock).addCoveredId(SpecificationItemId.parseId(MarkdownTestConstants.COVERED_ID1));
        inOrder.verify(this.listenerMock).addCoveredId(SpecificationItemId.parseId(MarkdownTestConstants.COVERED_ID2));
        inOrder.verify(this.listenerMock)
                .addDependsOnId(SpecificationItemId.parseId(MarkdownTestConstants.DEPENDS_ON_ID1));
        inOrder.verify(this.listenerMock)
                .addDependsOnId(SpecificationItemId.parseId(MarkdownTestConstants.DEPENDS_ON_ID2));
        inOrder.verify(this.listenerMock).appendComment(MarkdownTestConstants.COMMENT_LINE1);
        inOrder.verify(this.listenerMock).appendComment(MarkdownTestConstants.COMMENT_LINE2);
        inOrder.verify(this.listenerMock).addNeededArtifactType(MarkdownTestConstants.NEEDS_ARTIFACT_TYPE1);
        inOrder.verify(this.listenerMock).addNeededArtifactType(MarkdownTestConstants.NEEDS_ARTIFACT_TYPE2);
        inOrder.verify(this.listenerMock).endSpecificationItem();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testTwoConsecutiveSpecificationItems()
    {
        runImporterOnText(createTwoConsecutiveItemsInMarkdownFormat());
        assertImporterEventsForTwoConsecutiveItemsCalled();
    }

    private String createTwoConsecutiveItemsInMarkdownFormat()
    {
        return "# " + MarkdownTestConstants.TITLE //
                + "\n" //
                + MarkdownTestConstants.ID1 + "\n" //
                + "\n" + MarkdownTestConstants.ID2 + "\n" //
                + "# Irrelevant Title";
    }

    private void assertImporterEventsForTwoConsecutiveItemsCalled()
    {
        final InOrder inOrder = inOrder(this.listenerMock);
        inOrder.verify(this.listenerMock).beginSpecificationItem();
        inOrder.verify(this.listenerMock).setId(MarkdownTestConstants.ID1);
        inOrder.verify(this.listenerMock).setLocation(FILENAME, 2);
        inOrder.verify(this.listenerMock).setTitle(MarkdownTestConstants.TITLE);
        inOrder.verify(this.listenerMock).endSpecificationItem();
        inOrder.verify(this.listenerMock).beginSpecificationItem();
        inOrder.verify(this.listenerMock).setId(MarkdownTestConstants.ID2);
        inOrder.verify(this.listenerMock).setLocation(FILENAME, 4);
        inOrder.verify(this.listenerMock).endSpecificationItem();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testSingleNeeds()
    {
        final String singleNeedsItem = "`foo~bar~1`\n\nNeeds: " + MarkdownTestConstants.NEEDS_ARTIFACT_TYPE1;
        runImporterOnText(singleNeedsItem);
        verify(this.listenerMock, times(1)).addNeededArtifactType(MarkdownTestConstants.NEEDS_ARTIFACT_TYPE1);
    }

    // [utest->dsn~md.eb-markdown-id~1]
    @Test
    void testIdentifyLegacyId()
    {
        MarkdownAsserts.assertMatch(MdPattern.ID, "a:b, v0", "req:test, v1", "req:test,v1", "req:test, v999",
                "req:test.requirement, v1", "req:test_underscore, v1",
                "`req:test1, v1`arbitrary text");
        MarkdownAsserts.assertMismatch(MdPattern.ID, "test, v1", "req-test, v1", "req.4test, v1");
    }

    @Test
    void testRunImportHandlesIOException(@Mock final InputFile fileMock, @Mock final ImportEventListener listenerMock,
                                        @Mock final BufferedReader readerMock) throws IOException {
        when(fileMock.getPath()).thenReturn("/the/file");
        when(fileMock.createReader()).thenReturn(readerMock);
        when(readerMock.readLine()).thenThrow(new IOException("Dummy exception"));
        final MarkdownImporter importer = new MarkdownImporter(fileMock, listenerMock);
        final ImporterException exception = assertThrows(ImporterException.class, importer::runImport);
        assertThat(exception.getMessage(), equalTo("Error reading \"/the/file\" at line 0"));
    }
}
