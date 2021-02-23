package org.itsallcode.openfasttrace.importer.tag;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// [utest->dsn~import.short-coverage-tag~1]
@ExtendWith(MockitoExtension.class)

class TestTagImporterWithConfig
{
    private static final String COVERED_ITEM_NAME1 = "covered_name1";
    private static final String COVERED_ITEM_NAME2 = "covered_name2";
    private static final Path FILE = Paths.get("dummy");
    private static final String COVERED_ITEM_TYPE = "covered_type";
    private static final String COVERING_ITEM_TYPE = "covering_type";
    private static final String COVERED_ITEM_NAME_PREFIX = "prefix.";
    private static final String INVALID_REVISION = "invalidRevision";

    @Mock
    private PathConfig configMock;
    @Mock
    private ImportEventListener listenerMock;
    private InOrder inOrderListener;

    @BeforeEach
    void beforeEach()
    {
        this.inOrderListener = inOrder(this.listenerMock);
    }

    @Test
    void testEmptyFile()
    {
        runImport("");
        verifyNoInteractions(this.listenerMock);
    }

    @Test
    void testFileWithoutMatchingTag()
    {
        runImport("non matching\nfile\n");
        verifyNoInteractions(this.listenerMock);
    }

    @Test
    void testFileWithNewTagFormatAlsoSupported()
    {
        final String itemName = "coveredtype~coveredname~1"; // do not inline to
                                                             // avoid error in
                                                             // self-trace
        runImport("[type->" + itemName + "]");
        verify(this.listenerMock)
                .setId(SpecificationItemId.createId("type", "coveredname" + "-3264583751", 0));
    }

    @Test
    void testFileWithLegacyTagFormat()
    {
        configureConfigMock();

        runImport("[[" + COVERED_ITEM_NAME1 + ":1]]");
        verifyTag(1, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME1, 1),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME1 + "-135790575"));
        this.inOrderListener.verifyNoMoreInteractions();
    }

    @Test
    void testFileWithLegacyTagFormatTwoTagsInTwoLines()
    {
        configureConfigMock();

        runImport("[[" + COVERED_ITEM_NAME1 + ":1]]\n" //
                + "[[" + COVERED_ITEM_NAME2 + ":2]]");
        verifyTag(1, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME1, 1),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME1 + "-135790575"));
        verifyTag(2, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME2, 2),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME2 + "-3623433492"));
        this.inOrderListener.verifyNoMoreInteractions();
    }

    @Test
    void testFileWithLegacyTagFormatTwoTagsInSameLine()
    {
        configureConfigMock();

        runImport("[[" + COVERED_ITEM_NAME1 + ":1]]" //
                + "[[" + COVERED_ITEM_NAME2 + ":2]]");
        verifyTag(1, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME1, 1),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME1 + "-135790575"));
        verifyTag(1, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME2, 2),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME2 + "-4032809256"));
        this.inOrderListener.verifyNoMoreInteractions();
    }

    @Test
    void testNonIntegerRevisionRejected()
    {
        final String expectedMessage = "Error processing line dummy:1 ([[" + COVERED_ITEM_NAME1
                + ":" + INVALID_REVISION + "]]): Error parsing revision '" + INVALID_REVISION
                + "' for item '" + COVERED_ITEM_NAME1 + "'";
        assertThrows(ImporterException.class,
                () -> runImport("[[" + COVERED_ITEM_NAME1 + ":" + INVALID_REVISION + "]]"),
                expectedMessage);
    }

    @Test
    void testFileWithLegacyTagFormatWithPrefix()
    {
        configureConfigMock();

        when(this.configMock.getCoveredItemNamePrefix()).thenReturn(COVERED_ITEM_NAME_PREFIX);
        runImport("[[" + COVERED_ITEM_NAME1 + ":1]]");
        verifyTag(1,
                SpecificationItemId.createId(COVERED_ITEM_TYPE,
                        COVERED_ITEM_NAME_PREFIX + COVERED_ITEM_NAME1, 1),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME_PREFIX + COVERED_ITEM_NAME1 + "-2831791434"));
        this.inOrderListener.verifyNoMoreInteractions();
    }

    private void configureConfigMock()
    {
        when(this.configMock.getCoveredItemArtifactType()).thenReturn(COVERED_ITEM_TYPE);
        when(this.configMock.getCoveredItemNamePrefix()).thenReturn(null);
        when(this.configMock.getTagArtifactType()).thenReturn(COVERING_ITEM_TYPE);
        when(this.configMock.getCoveredItemNamePrefix()).thenReturn(null);
    }

    private void verifyTag(final int lineNumber, final SpecificationItemId coveredId,
            final SpecificationItemId tagItemId)
    {
        this.inOrderListener.verify(this.listenerMock).beginSpecificationItem();
        this.inOrderListener.verify(this.listenerMock).setLocation(FILE.toString(), lineNumber);
        this.inOrderListener.verify(this.listenerMock).setId(tagItemId);
        this.inOrderListener.verify(this.listenerMock).addCoveredId(coveredId);
        this.inOrderListener.verify(this.listenerMock).endSpecificationItem();
    }

    private void runImport(final String content)
    {
        final InputFile file = StreamInput.forReader(FILE,
                new BufferedReader(new StringReader(content)));

        TagImporter.create(Optional.of(this.configMock), file, this.listenerMock).runImport();
    }
}
