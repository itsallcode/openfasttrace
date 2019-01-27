package org.itsallcode.openfasttrace.importer;

import static org.hamcrest.MatcherAssert.assertThat;

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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.openfasttrace.importer.input.RealFileInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Base class for {@link ImporterFactory} tests.
 *
 * @param <T>
 *            type of the factory under test
 */
public abstract class ImporterFactoryTestBase<T extends ImporterFactory>
{
    @Mock
    protected ImporterContext contextMock;

    @BeforeEach
    public void initMocks()
    {
        MockitoAnnotations.initMocks(this);
        when(this.contextMock.getImportSettings()).thenReturn(ImportSettings.createDefault());
    }

    @Test
    void testSupportedFileNames()
    {
        assertSupported(getSupportedFilenames(), true);
    }

    @Test
    void testUnsupportedFileNames()
    {
        assertSupported(getUnsupportedFilenames(), false);
    }

    @Test
    void testCreateImporterThrowsExceptionForMissingFile()
    {
        final Path supportedPath = Paths.get("dir", getSupportedFilenames().get(0));
        assertThrows(ImporterException.class, //
                () -> createAndInitialize()
                        .createImporter(RealFileInput.forPath(supportedPath), null).runImport(), //
                "Error reading \"" + supportedPath + "\"");
    }

    private T createAndInitialize()
    {
        final T factory = createFactory();
        factory.init(this.contextMock);
        return factory;
    }

    @Test
    void testInit()
    {
        final T factory = createFactory();
        factory.init(this.contextMock);
        assertThat(factory.getContext(), sameInstance(this.contextMock));
    }

    @Test
    void testMissingContextThrowsException()
    {
        final T factory = createFactory();
        factory.init(null);
        assertThrows(NullPointerException.class, () -> factory.getContext(),
                "Context was not initialized");
    }

    private void assertSupported(final List<String> filenames, final boolean expectedResult)
    {
        final T factory = createFactory();
        for (final String filename : filenames)
        {
            final Path path = Paths.get("dir", filename);
            assertThat(path.toString(), factory.supportsFile(RealFileInput.forPath(path)),
                    equalTo(expectedResult));
        }
    }

    protected abstract T createFactory();

    protected abstract List<String> getSupportedFilenames();

    protected abstract List<String> getUnsupportedFilenames();
}
