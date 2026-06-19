package org.itsallcode.openfasttrace.api.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link AbstractImporterFactory}.
 */
class TestAbstractImporterFactory
{
    private ImporterFactory importerFactory;
    private ImporterContext context;

    @BeforeEach
    void setUp()
    {
        context = Mockito.mock(ImporterContext.class);
        importerFactory = new TestingImporterFactory();
    }

    @Test
    void testInitSetsContext()
    {
        importerFactory.init(context);
        assertThat(importerFactory.getContext(), sameInstance(context));
    }

    @Test
    void testGetContextThrowsExceptionWhenNotInitialized()
    {
        assertThrows(NullPointerException.class, () -> importerFactory.getContext());
    }

    private static class TestingImporterFactory extends AbstractImporterFactory
    {
        @Override
        public boolean supportsFile(final InputFile file)
        {
            return false;
        }

        @Override
        public Importer createImporter(final InputFile file, final ImportEventListener listener)
        {
            return null;
        }

        @Override
        public int getPriority()
        {
            return 0;
        }
    }
}
