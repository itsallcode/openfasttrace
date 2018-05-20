package org.itsallcode.openfasttrace.importer.markdown;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import static org.itsallcode.openfasttrace.importer.markdown.MarkdownAsserts.assertMatch;
import static org.itsallcode.openfasttrace.importer.markdown.MarkdownAsserts.assertMismatch;
import static org.itsallcode.openfasttrace.importer.markdown.MarkdownTestConstants.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;

import org.itsallcode.openfasttrace.core.ItemStatus;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.Importer;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestImportMarkdown
{
    private static final String TAG2 = "Tag2";
    private static final String TAG1 = "Tag1";
    private static final String FILENAME = "file name";

    @Mock
    ImportEventListener listenerMock;

    @Mock
    Reader readerMock;

    // [utest~md.specification_item_id_format~1]
    @Test
    public void testIdentifyId()
    {
        assertMatch(MdPattern.ID, "req~foo~1<a id=\"req~foo~1\"></a>", "a~b~0", "req~test~1",
                "req~test~999", "req~test.requirement~1", "req~test_underscore~1",
                "`req~test1~1`arbitrary text");
        assertMismatch(MdPattern.ID, "test~1", "req-test~1", "req~4test~1");
    }

    // [utest~md.specification_item_title~1]
    @Test
    public void testIdentifyTitle()
    {
        assertMatch(MdPattern.TITLE, "#Title", "# Title", "###### Title", "#   Title");
        assertMismatch(MdPattern.TITLE, "Title", "Title #", " # Title");
    }

    @Test
    public void testFindRequirement()
    {
        final String completeItem = createCompleteSpecificationItemInMarkdownFormat();
        runImporterOnText(completeItem);
        assertAllImporterEventsCalled();
    }

    // [utest->dsn~md.needs-coverage-list-compact~1]
    private String createCompleteSpecificationItemInMarkdownFormat()
    {
        return "# " + TITLE //
                + "\n" //
                + "`" + ID1 + "` <a id=\"" + ID1 + "\"></a>" //
                + "\n" //
                + DESCRIPTION_LINE1 + "\n" //
                + DESCRIPTION_LINE2 + "\n" //
                + DESCRIPTION_LINE3 + "\n" //
                + "\nRationale:\n" //
                + RATIONALE_LINE1 + "\n" //
                + RATIONALE_LINE2 + "\n" //
                + "\nCovers:\n\n" //
                + "  * " + COVERED_ID1 + "\n" //
                + " + " + "[Link to baz2](#" + COVERED_ID2 + ")\n" //
                + "\nDepends:\n\n" //
                + "  + " + DEPENDS_ON_ID1 + "\n" //
                + "  - " + DEPENDS_ON_ID2 + "\n" //
                + "\nComment:\n\n" //
                + COMMENT_LINE1 + "\n" //
                + COMMENT_LINE2 + "\n" //
                + "\nNeeds: " + NEEDS_ARTIFACT_TYPE1 //
                + ", " + NEEDS_ARTIFACT_TYPE2;
    }

    private void runImporterOnText(final String text)
    {
        final BufferedReader reader = new BufferedReader(new StringReader(text));
        final InputFile file = InputFile.forReader(Paths.get(FILENAME), reader);
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
        inOrder.verify(this.listenerMock).setId(ID1);
        inOrder.verify(this.listenerMock).setLocation(FILENAME, 2);
        inOrder.verify(this.listenerMock).setTitle(TITLE);
        inOrder.verify(this.listenerMock)
                .appendDescription(DESCRIPTION_LINE1 + System.lineSeparator() + DESCRIPTION_LINE2
                        + System.lineSeparator() + DESCRIPTION_LINE3);
        inOrder.verify(this.listenerMock)
                .appendRationale(RATIONALE_LINE1 + System.lineSeparator() + RATIONALE_LINE2);
        inOrder.verify(this.listenerMock).addCoveredId(SpecificationItemId.parseId(COVERED_ID1));
        inOrder.verify(this.listenerMock).addCoveredId(SpecificationItemId.parseId(COVERED_ID2));
        inOrder.verify(this.listenerMock)
                .addDependsOnId(SpecificationItemId.parseId(MarkdownTestConstants.DEPENDS_ON_ID1));
        inOrder.verify(this.listenerMock)
                .addDependsOnId(SpecificationItemId.parseId(DEPENDS_ON_ID2));
        inOrder.verify(this.listenerMock)
                .appendComment(COMMENT_LINE1 + System.lineSeparator() + COMMENT_LINE2);
        inOrder.verify(this.listenerMock).addNeededArtifactType(NEEDS_ARTIFACT_TYPE1);
        inOrder.verify(this.listenerMock).addNeededArtifactType(NEEDS_ARTIFACT_TYPE2);
        inOrder.verify(this.listenerMock).endSpecificationItem();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testTwoConsecutiveSpecificationItems()
    {
        runImporterOnText(createTwoConsecutiveItemsInMarkdownFormat());
        assertImporterEventsForTwoConsecutiveItemsCalled();
    }

    private String createTwoConsecutiveItemsInMarkdownFormat()
    {
        return "# " + TITLE //
                + "\n" //
                + ID1 + "\n" //
                + "\n" + ID2 + "\n" //
                + "# Irrelevant Title";
    }

    private void assertImporterEventsForTwoConsecutiveItemsCalled()
    {
        final InOrder inOrder = inOrder(this.listenerMock);
        inOrder.verify(this.listenerMock).beginSpecificationItem();
        inOrder.verify(this.listenerMock).setId(ID1);
        inOrder.verify(this.listenerMock).setLocation(FILENAME, 2);
        inOrder.verify(this.listenerMock).setTitle(TITLE);
        inOrder.verify(this.listenerMock).endSpecificationItem();
        inOrder.verify(this.listenerMock).beginSpecificationItem();
        inOrder.verify(this.listenerMock).setId(ID2);
        inOrder.verify(this.listenerMock).setLocation(FILENAME, 4);
        inOrder.verify(this.listenerMock).endSpecificationItem();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSingleNeeds()
    {
        final String singleNeedsItem = "`foo~bar~1`\n\nNeeds: " + NEEDS_ARTIFACT_TYPE1;
        runImporterOnText(singleNeedsItem);
        verify(this.listenerMock, times(1)).addNeededArtifactType(NEEDS_ARTIFACT_TYPE1);
    }

    // [utest->dsn~md.eb-markdown-id~1]
    @Test
    public void testIdentifyLegacyId()
    {
        assertMatch(MdPattern.ID, "a:b, v0", "req:test, v1", "req:test,v1", "req:test, v999",
                "req:test.requirement, v1", "req:test_underscore, v1",
                "`req:test1, v1`arbitrary text");
        assertMismatch(MdPattern.ID, "test, v1", "req-test, v1", "req.4test, v1");
    }

    @Test
    public void testFindLegacyRequirement()
    {
        final String completeItem = createCompleteSpecificationItemInLegacyMarkdownFormat();
        runImporterOnText(completeItem);
        assertAllImporterEventsForLegacyItemCalled();
    }

    // [utest->dsn~md.needs-coverage-list~2]
    private String createCompleteSpecificationItemInLegacyMarkdownFormat()
    {
        return "# " + TITLE //
                + "\n" //
                + "`" + LEGACY_ID + "`" //
                + "\n" //
                + "\nStatus: proposed\n" //
                + "\nDescription:\n" + DESCRIPTION_LINE1 + "\n" //
                + DESCRIPTION_LINE2 + "\n" //
                + DESCRIPTION_LINE3 + "\n" //
                + "\nRationale:\n" //
                + RATIONALE_LINE1 + "\n" //
                + RATIONALE_LINE2 + "\n" //
                + "\nDepends:\n\n" //
                + "  + `" + LEGACY_DEPENDS_ON_ID1 + "`\n" //
                + "  - `" + LEGACY_DEPENDS_ON_ID2 + "`\n" //
                + "\nCovers:\n\n" //
                + "  * `" + LEGACY_COVERED_ID1 + "`\n" //
                + " + `" + LEGACY_COVERED_ID2 + "`\n" //
                + "\nComment:\n\n" //
                + COMMENT_LINE1 + "\n" //
                + COMMENT_LINE2 + "\n" //
                + "\nNeeds:\n" //
                + "   * " + NEEDS_ARTIFACT_TYPE1 + "\n"//
                + "+ " + NEEDS_ARTIFACT_TYPE2 + "\n" //
                + "\nTags:\n" //
                + " * " + TAG1 + "\n" //
                + "   + " + TAG2 + "";
    }

    private void assertAllImporterEventsForLegacyItemCalled()
    {
        final InOrder inOrder = inOrder(this.listenerMock);
        inOrder.verify(this.listenerMock).beginSpecificationItem();
        inOrder.verify(this.listenerMock).setId(PARSED_LEGACY_ID);
        inOrder.verify(this.listenerMock).setLocation(FILENAME, 2);
        inOrder.verify(this.listenerMock).setTitle(TITLE);
        inOrder.verify(this.listenerMock).setStatus(ItemStatus.PROPOSED);
        inOrder.verify(this.listenerMock)
                .appendDescription(DESCRIPTION_LINE1 + System.lineSeparator() + DESCRIPTION_LINE2
                        + System.lineSeparator() + DESCRIPTION_LINE3);
        inOrder.verify(this.listenerMock)
                .appendRationale(RATIONALE_LINE1 + System.lineSeparator() + RATIONALE_LINE2);
        inOrder.verify(this.listenerMock).addDependsOnId(PARSED_LEGACY_DEPENDS_ON_ID1);
        inOrder.verify(this.listenerMock).addDependsOnId(PARSED_LEGACY_DEPENDS_ON_ID2);
        inOrder.verify(this.listenerMock).addCoveredId(PARSED_LEGACY_COVERED_ID1);
        inOrder.verify(this.listenerMock).addCoveredId(PARSED_LEGACY_COVERED_ID2);
        inOrder.verify(this.listenerMock)
                .appendComment(COMMENT_LINE1 + System.lineSeparator() + COMMENT_LINE2);
        inOrder.verify(this.listenerMock).addNeededArtifactType(NEEDS_ARTIFACT_TYPE1);
        inOrder.verify(this.listenerMock).addNeededArtifactType(NEEDS_ARTIFACT_TYPE2);
        inOrder.verify(this.listenerMock).addTag(TAG1);
        inOrder.verify(this.listenerMock).addTag(TAG2);
        inOrder.verify(this.listenerMock).endSpecificationItem();
        inOrder.verifyNoMoreInteractions();
    }
}