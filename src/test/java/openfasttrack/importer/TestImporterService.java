package openfasttrack.importer;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;

/**
 * Test for {@link ImporterService}
 */
public class TestImporterService
{
    @Mock
    private ImporterFactoryLoader factoryLoaderMock;
    @Mock
    private ImporterFactory importerFactoryMock;
    @Mock
    private Importer importerMock;

    private Path file;
    private ImporterService importerService;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        this.importerService = new ImporterService(this.factoryLoaderMock);
        this.file = Paths.get("dir", "file");

        when(this.factoryLoaderMock.getImporterFactory(same(this.file)))
                .thenReturn(this.importerFactoryMock);
        when(this.importerFactoryMock.createImporter(same(this.file), same(StandardCharsets.UTF_8),
                any())).thenReturn(this.importerMock);
    }

    @Test
    public void testImport()
    {
        final Map<SpecificationItemId, SpecificationItem> result = this.importerService
                .importFile(this.file);

        verify(this.importerMock).runImport();

        final SpecificationMapListBuilder builder = getBuilder();
        assertThat(result, sameInstance(builder.build()));
    }

    private SpecificationMapListBuilder getBuilder()
    {
        final ArgumentCaptor<SpecificationMapListBuilder> arg = ArgumentCaptor
                .forClass(SpecificationMapListBuilder.class);
        verify(this.importerFactoryMock).createImporter(same(this.file),
                same(StandardCharsets.UTF_8), arg.capture());
        return arg.getValue();
    }
}
