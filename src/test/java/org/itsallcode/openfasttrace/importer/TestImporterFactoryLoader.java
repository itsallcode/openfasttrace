package org.itsallcode.openfasttrace.importer;

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
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.itsallcode.openfasttrace.core.ServiceLoaderWrapper;
import org.itsallcode.openfasttrace.importer.ImporterException;
import org.itsallcode.openfasttrace.importer.ImporterFactory;
import org.itsallcode.openfasttrace.importer.ImporterFactoryLoader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test for {@link ImporterFactoryLoader}
 */
public class TestImporterFactoryLoader
{
    @Mock
    private ServiceLoaderWrapper<ImporterFactory> serviceLoaderMock;
    @Mock
    private ImporterFactory supportedFactory1;
    @Mock
    private ImporterFactory supportedFactory2;
    @Mock
    private ImporterFactory unsupportedFactory;

    private ImporterFactoryLoader loader;
    private Path file;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

        this.loader = new ImporterFactoryLoader(this.serviceLoaderMock);
        this.file = Paths.get("dir", "name");

        when(this.supportedFactory1.supportsFile(same(this.file))).thenReturn(true);
        when(this.supportedFactory2.supportsFile(same(this.file))).thenReturn(true);
        when(this.unsupportedFactory.supportsFile(same(this.file))).thenReturn(false);
    }

    @Test(expected = ImporterException.class)
    public void testNoFactoryRegistered()
    {
        simulateFactories();
        this.loader.getImporterFactory(this.file);
    }

    @Test
    public void testMatchingFactoryFoundOnlyOneAvailable()
    {
        simulateFactories(this.supportedFactory1);
        assertFactoryFound(this.supportedFactory1);
    }

    @Test
    public void testMatchingFactoryFoundTwoAvailable()
    {
        simulateFactories(this.supportedFactory1, this.unsupportedFactory);
        assertFactoryFound(this.supportedFactory1);
    }

    @Test(expected = ImporterException.class)
    public void testMultipleMatchingFactoriesFound()
    {
        simulateFactories(this.supportedFactory1, this.supportedFactory1);
        this.loader.getImporterFactory(this.file);
    }

    private void assertFactoryFound(final ImporterFactory expectedFactory)
    {
        assertThat(this.loader.getImporterFactory(this.file), sameInstance(expectedFactory));
    }

    private void simulateFactories(final ImporterFactory... factories)
    {
        when(this.serviceLoaderMock.spliterator()).thenReturn(asList(factories).spliterator());
    }
}
