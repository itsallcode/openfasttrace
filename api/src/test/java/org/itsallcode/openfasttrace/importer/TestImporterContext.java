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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TestImporterContext
{
    @Mock
    private ImportSettings settingsMock;
    @Mock
    private ImporterService importerServiceMock;

    private ImporterContext context;

    @BeforeEach
    void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        this.context = new ImporterContext(this.settingsMock);
    }

    @Test
    void testGetImporterConfig()
    {
        assertThat(this.context.getImportSettings(), sameInstance(this.settingsMock));
    }

    @Test
    void testGetImporterServiceNullByDefault()
    {
        assertThrows(NullPointerException.class, () -> this.context.getImporterService());
    }

    @Test
    void testGetImporterService()
    {
        this.context.setImporterService(this.importerServiceMock);
        assertThat(this.context.getImporterService(), sameInstance(this.importerServiceMock));
    }
}