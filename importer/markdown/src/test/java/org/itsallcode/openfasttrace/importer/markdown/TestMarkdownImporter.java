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
        return "# " + "Requirement Title" //
                + "\n" //
                + "`" + SpecificationItemId.parseId("type~id~1") + "` <a id=\"" + SpecificationItemId.parseId("type~id~1") + "\"></a>" //
                + "\n" //
                + "Description" + "\n" //
                + "" + "\n" //
                + "More description" + "\n" //
                + "\nRationale:\n" //
                + "Rationale" + "\n" //
                + "More rationale" + "\n" //
                + "\nCovers:\n\n" //
                + "  * " + "impl~foo1~1" + "\n" //
                + " + " + "[Link to baz2](#" + "impl~baz2~2" + ")\n" //
                + "\nDepends:\n\n" //
                + "  + " + "configuration~blubb.blah.blah~4711" + "\n" //
                + "  - " + "db~blah.blubb~42" + "\n" //
                + "\nComment:\n\n" //
                + "Comment" + "\n" //
                + "More comment" + "\n" //
                + "\nNeeds: " + "artA" //
                + " , " + "artB" + " ";
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
        inOrder.verify(this.listenerMock).setId(SpecificationItemId.parseId("type~id~1"));
        inOrder.verify(this.listenerMock).setLocation(FILENAME, 2);
        inOrder.verify(this.listenerMock).setTitle("Requirement Title");
        inOrder.verify(this.listenerMock).appendDescription("Description");
        inOrder.verify(this.listenerMock).appendDescription(System.lineSeparator());
        inOrder.verify(this.listenerMock).appendDescription("");
        inOrder.verify(this.listenerMock).appendDescription(System.lineSeparator());
        inOrder.verify(this.listenerMock).appendDescription("More description");
        inOrder.verify(this.listenerMock).appendRationale("Rationale");
        inOrder.verify(this.listenerMock).appendRationale("More rationale");
        inOrder.verify(this.listenerMock).addCoveredId(SpecificationItemId.parseId("impl~foo1~1"));
        inOrder.verify(this.listenerMock).addCoveredId(SpecificationItemId.parseId("impl~baz2~2"));
        inOrder.verify(this.listenerMock)
                .addDependsOnId(SpecificationItemId.parseId("configuration~blubb.blah.blah~4711"));
        inOrder.verify(this.listenerMock)
                .addDependsOnId(SpecificationItemId.parseId("db~blah.blubb~42"));
        inOrder.verify(this.listenerMock).appendComment("Comment");
        inOrder.verify(this.listenerMock).appendComment("More comment");
        inOrder.verify(this.listenerMock).addNeededArtifactType("artA");
        inOrder.verify(this.listenerMock).addNeededArtifactType("artB");
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
        return "# " + "Requirement Title" //
                + "\n" //
                + SpecificationItemId.parseId("type~id~1") + "\n" //
                + "\n" + MarkdownTestConstants.ID2 + "\n" //
                + "# Irrelevant Title";
    }

    private void assertImporterEventsForTwoConsecutiveItemsCalled()
    {
        final InOrder inOrder = inOrder(this.listenerMock);
        inOrder.verify(this.listenerMock).beginSpecificationItem();
        inOrder.verify(this.listenerMock).setId(SpecificationItemId.parseId("type~id~1"));
        inOrder.verify(this.listenerMock).setLocation(FILENAME, 2);
        inOrder.verify(this.listenerMock).setTitle("Requirement Title");
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
        final String singleNeedsItem = "`foo~bar~1`\n\nNeeds: " + "artA";
        runImporterOnText(singleNeedsItem);
        verify(this.listenerMock, times(1)).addNeededArtifactType("artA");
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
