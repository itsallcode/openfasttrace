package org.itsallcode.openfasttrace.core.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.Arrays;

import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.api.importer.ImporterFactory;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.core.serviceloader.Loader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test for {@link ImporterFactoryLoader}
 */
@ExtendWith(MockitoExtension.class)
class TestImporterFactoryLoader
{
    @Mock
    private Loader<ImporterFactory> serviceLoaderMock;
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

        lenient().when(this.supportedFactory1.supportsFile(same(this.file))).thenReturn(true);
        lenient().when(this.supportedFactory2.supportsFile(same(this.file))).thenReturn(true);
        lenient().when(this.unsupportedFactory.supportsFile(same(this.file))).thenReturn(false);
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
        when(this.serviceLoaderMock.load()).thenReturn(Arrays.stream(factories));
    }
}
