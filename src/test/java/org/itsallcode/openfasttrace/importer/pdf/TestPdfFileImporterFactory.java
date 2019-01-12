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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.itsallcode.openfasttrace.importer.ImporterFactoryTestBase;
import org.itsallcode.openfasttrace.importer.ImporterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class TestPdfFileImporterFactory extends ImporterFactoryTestBase<PdfFileImporterFactory>
{
    @Mock
    private ImporterService importerServiceMock;

    @BeforeEach
    public void configureMock()
    {
        when(this.contextMock.getImporterService()).thenReturn(this.importerServiceMock);
    }

    @Test
    void test()
    {
        new PdfFileImporterFactory();
    }

    @Override
    protected PdfFileImporterFactory createFactory()
    {
        return new PdfFileImporterFactory();
    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("blah.pdf", "a.PDF");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("x.java", "y.text", "z.gz", "a.zp", "y.zip");
    }

    @Test
    void testFactoryThrowsExceptionWhenContextMissing()
    {
        assertThrows(NullPointerException.class,
                () -> new PdfFileImporterFactory().createImporter(null, null),
                "Context was not initialized");
    }
}