package org.itsallcode.openfasttrace.importer.pdf;

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