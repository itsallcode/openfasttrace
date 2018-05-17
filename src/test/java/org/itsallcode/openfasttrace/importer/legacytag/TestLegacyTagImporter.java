package org.itsallcode.openfasttrace.importer.legacytag;

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
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.legacytag.config.PathConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// [utest->dsn~import.short-coverage-tag~1]
public class TestLegacyTagImporter
{
    private static final String COVERED_ITEM_NAME1 = "covered_name1";
    private static final String COVERED_ITEM_NAME2 = "covered_name2";
    private static final Path FILE = Paths.get("dummy");
    private static final String COVERED_ITEM_TYPE = "covered_type";
    private static final String COVERING_ITEM_TYPE = "covering_type";
    private static final String COVERED_ITEM_NAME_PREFIX = "prefix.";

    @Mock
    private PathConfig configMock;
    @Mock
    private ImportEventListener listenerMock;

    private InOrder inOrderListener;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        this.inOrderListener = inOrder(this.listenerMock);
        when(this.configMock.getCoveredItemArtifactType()).thenReturn(COVERED_ITEM_TYPE);
        when(this.configMock.getCoveredItemNamePrefix()).thenReturn(null);
        when(this.configMock.getTagArtifactType()).thenReturn(COVERING_ITEM_TYPE);
        when(this.configMock.getCoveredItemNamePrefix()).thenReturn(null);
    }

    @Test
    public void testEmptyFile()
    {
        runImport("");
        verifyZeroInteractions(this.listenerMock);
    }

    @Test
    public void testFileWithoutMatchingTag()
    {
        runImport("non matching\nfile\n");
        verifyZeroInteractions(this.listenerMock);
    }

    @Test
    public void testFileWithNewTagFormat()
    {
        final String itemName = "coveredtype~coveredname~1"; // do not inline to
                                                             // avoid error in
                                                             // self-trace
        runImport("[type->" + itemName + "]");
        verifyZeroInteractions(this.listenerMock);
    }

    @Test
    public void testFileWithLegacyTagFormat()
    {
        runImport("[[" + COVERED_ITEM_NAME1 + ":1]]");
        verifyTag(1, SpecificationItemId.createId(COVERED_ITEM_TYPE, COVERED_ITEM_NAME1, 1),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME1 + "-135790575"));
        this.inOrderListener.verifyNoMoreInteractions();
    }

    @Test
    public void testFileWithLegacyTagFormatTwoTagsInTwoLines()
    {
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
    public void testFileWithLegacyTagFormatTwoTagsInSameLine()
    {
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
    public void testFileWithLegacyTagFormatWithPrefix()
    {
        when(this.configMock.getCoveredItemNamePrefix()).thenReturn(COVERED_ITEM_NAME_PREFIX);
        runImport("[[" + COVERED_ITEM_NAME1 + ":1]]");
        verifyTag(1,
                SpecificationItemId.createId(COVERED_ITEM_TYPE,
                        COVERED_ITEM_NAME_PREFIX + COVERED_ITEM_NAME1, 1),
                SpecificationItemId.createId(COVERING_ITEM_TYPE,
                        COVERED_ITEM_NAME_PREFIX + COVERED_ITEM_NAME1 + "-2831791434"));
        this.inOrderListener.verifyNoMoreInteractions();
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
        final InputFile file = InputFile.createForReader(FILE,
                new BufferedReader(new StringReader(content)));

        new LegacyTagImporter(this.configMock, file, this.listenerMock).runImport();
    }
}
