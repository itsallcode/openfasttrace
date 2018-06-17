package org.itsallcode.openfasttrace.importer;

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

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import org.itsallcode.openfasttrace.importer.legacytag.config.LegacyTagImporterConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestImporterContext
{
    @Mock
    private LegacyTagImporterConfig configMock;
    @Mock
    private ImporterService importerServiceMock;

    private ImporterContext context;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        this.context = new ImporterContext(this.configMock);
    }

    @Test
    public void testGetImporterConfig()
    {
        assertThat(this.context.getTagImporterConfig(), sameInstance(this.configMock));
    }

    @Test(expected = NullPointerException.class)
    public void testGetImporterServiceNullByDefault()
    {
        this.context.getImporterService();
    }

    @Test
    public void testGetImporterService()
    {
        this.context.setImporterService(this.importerServiceMock);
        assertThat(this.context.getImporterService(), sameInstance(this.importerServiceMock));
    }
}
