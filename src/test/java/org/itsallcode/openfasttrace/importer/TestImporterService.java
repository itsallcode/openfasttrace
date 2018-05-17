package org.itsallcode.openfasttrace.importer;

import static org.hamcrest.CoreMatchers.equalTo;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

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

    @Captor
    private ArgumentCaptor<SpecificationListBuilder> builderArg;

    @Captor
    private ArgumentCaptor<InputFile> fileArg;

    private Path file;
    private ImporterService importerService;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        this.importerService = new ImporterService(this.factoryLoaderMock);
        this.file = Paths.get("dir", "file");

        when(this.factoryLoaderMock.getImporterFactory(any(InputFile.class)))
                .thenReturn(this.importerFactoryMock);
        when(this.importerFactoryMock.createImporter(any(), any())).thenReturn(this.importerMock);
    }

    @Test
    public void testImport()
    {
        final List<SpecificationItem> result = this.importerService.importFile(this.file);

        verify(this.importerMock).runImport();
        verify(this.importerFactoryMock).createImporter(this.fileArg.capture(),
                this.builderArg.capture());

        final SpecificationListBuilder builder = this.builderArg.getValue();
        assertThat(result, sameInstance(builder.build()));

        assertThat(this.fileArg.getValue().getPath(), equalTo("dir/file"));
    }
}
