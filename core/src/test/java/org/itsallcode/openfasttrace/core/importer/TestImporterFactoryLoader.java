package org.itsallcode.openfasttrace.core.importer;

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

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;

import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.api.importer.ImporterFactory;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.core.serviceloader.InitializingServiceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Test for {@link ImporterFactoryLoader}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TestImporterFactoryLoader
{
    @Mock
    private InitializingServiceLoader<ImporterFactory, ImporterContext> serviceLoaderMock;
    @Mock
    private ImporterFactory supportedFactory1;
    @Mock
    private ImporterFactory supportedFactory2;
    @Mock
    private ImporterFactory unsupportedFactory;
    @Mock
    private ImporterContext contextMock;

    private ImporterFactoryLoader loader;
    private InputFile file;

    @BeforeEach
    void beforeEach()
    {
        this.loader = new ImporterFactoryLoader(this.serviceLoaderMock);
        this.file = RealFileInput.forPath(Paths.get("dir", "name"));

        when(this.supportedFactory1.supportsFile(same(this.file))).thenReturn(true);
        when(this.supportedFactory2.supportsFile(same(this.file))).thenReturn(true);
        when(this.unsupportedFactory.supportsFile(same(this.file))).thenReturn(false);
    }

    @Test
    void testNoFactoryRegisteredReturnsNoImporter()
    {
        simulateFactories();
        assertTrue(this.loader.getImporterFactory(this.file).isEmpty());
    }

    @Test
    void testMatchingFactoryFoundOnlyOneAvailable()
    {
        simulateFactories(this.supportedFactory1);
        assertFactoryFound(this.supportedFactory1);
    }

    @Test
    void testMatchingFactoryFoundTwoAvailable()
    {
        simulateFactories(this.supportedFactory1, this.unsupportedFactory);
        assertFactoryFound(this.supportedFactory1);
    }

    @Test
    void testMultipleMatchingFactoriesFoundReturnNoImporter()
    {
        simulateFactories(this.supportedFactory1, this.supportedFactory1);
        assertTrue(this.loader.getImporterFactory(this.file).isEmpty());
    }

    private void assertFactoryFound(final ImporterFactory expectedFactory)
    {
        assertThat(this.loader.getImporterFactory(this.file).get(), sameInstance(expectedFactory));
    }

    private void simulateFactories(final ImporterFactory... factories)
    {
        when(this.serviceLoaderMock.spliterator()).thenReturn(asList(factories).spliterator());
    }
}
