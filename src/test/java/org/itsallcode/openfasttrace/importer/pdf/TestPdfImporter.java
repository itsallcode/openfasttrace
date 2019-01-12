package org.itsallcode.openfasttrace.importer.pdf;

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

import static org.mockito.Mockito.verify;

import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestPdfImporter
{
    @Mock
    ImportEventListener listenerMock;

    @Test
    void testImportLineWithSpecObject()
    {
        final PdfFileImporter importer = new PdfFileImporter(null, null, this.listenerMock);

        importer.createSpecificationItemFromLine("Test 1234 [ABCD-134] This is a test title ");

        verify(this.listenerMock).beginSpecificationItem();

        final SpecificationItemId specItemId = SpecificationItemId.createId(
                PdfFileImporter.DEFAULT_ARTIFACT_TYPE, "ABCD-134",
                PdfFileImporter.DEFAULT_SPECITEM_REVISION);
        verify(this.listenerMock).setId(specItemId);

        verify(this.listenerMock).setTitle("This is a test title");
        verify(this.listenerMock).appendDescription("This is a test title");

        verify(this.listenerMock).endSpecificationItem();
    }

    @Test
    void testImportLineWithoutSpecObject()
    {
        final PdfFileImporter importer = new PdfFileImporter(null, null, this.listenerMock);

        importer.createSpecificationItemFromLine("Test 1234 ABCD-134 This is a test title ");

        verify(this.listenerMock, Mockito.never()).beginSpecificationItem();
        verify(this.listenerMock, Mockito.never()).endSpecificationItem();
    }

}