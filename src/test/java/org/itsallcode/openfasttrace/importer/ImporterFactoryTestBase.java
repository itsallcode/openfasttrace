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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    protected ImporterContext contextMock;

    @Before
    public void initMocks()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSupportedFileNames()
    {
        assertSupported(getSupportedFilenames(), true);
    }

    @Test
    public void testUnsupportedFileNames()
    {
        assertSupported(getUnsupportedFilenames(), false);
    }

    @Test
    public void testCreateImporterThrowsExceptionForMissingFile()
    {
        final Path supportedPath = Paths.get("dir", getSupportedFilenames().get(0));
        this.thrown.expect(ImporterException.class);
        this.thrown.expectMessage("Error reading file " + supportedPath);
        createAndInitialize().createImporter(InputFile.forPath(supportedPath), null).runImport();
    }

    private T createAndInitialize()
    {
        final T factory = createFactory();
        factory.init(this.contextMock);
        return factory;
    }

    @Test
    public void testInit()
    {
        final T factory = createFactory();
        factory.init(this.contextMock);
        assertThat(factory.getContext(), sameInstance(this.contextMock));
    }

    @Test
    public void testMissingContextThrowsException()
    {
        final T factory = createFactory();
        this.thrown.expect(NullPointerException.class);
        this.thrown.expectMessage(equalTo("Context was not initialized"));
        factory.getContext();
    }

    private void assertSupported(final List<String> filenames, final boolean expectedResult)
    {
        final T factory = createFactory();
        for (final String filename : filenames)
        {
            final Path path = Paths.get("dir", filename);
            assertThat(path.toString(), factory.supportsFile(InputFile.forPath(path)),
                    equalTo(expectedResult));
        }
    }

    protected abstract T createFactory();

    protected abstract List<String> getSupportedFilenames();

    protected abstract List<String> getUnsupportedFilenames();
}
