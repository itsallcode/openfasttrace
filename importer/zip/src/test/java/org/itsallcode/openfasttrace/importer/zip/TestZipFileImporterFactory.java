package org.itsallcode.openfasttrace.importer.zip;

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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.itsallcode.openfasttrace.api.importer.ImporterService;
import org.itsallcode.openfasttrace.testutil.importer.ImporterFactoryTestBase;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class TestZipFileImporterFactory extends ImporterFactoryTestBase<ZipFileImporterFactory>
{
    @Mock
    private ImporterService importerServiceMock;

    @Test
    void testConstructor()
    {
        assertThat(createFactory(), notNullValue());
    }

    @Override
    protected ZipFileImporterFactory createFactory()
    {
        return new ZipFileImporterFactory();
    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("blah.zip", "a.ZIP");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("x.java", "y.text", "z.gz", "a.zp");
    }

    @Test
    void testFactoryThrowsExceptionWhenContextMissing()
    {
        assertThrows(NullPointerException.class,
                () -> new ZipFileImporterFactory().createImporter(null, null),
                "Context was not initialized");
    }
}