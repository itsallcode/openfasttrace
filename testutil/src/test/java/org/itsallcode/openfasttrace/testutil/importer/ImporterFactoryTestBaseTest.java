package org.itsallcode.openfasttrace.testutil.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.junit.jupiter.api.Test;

class ImporterFactoryTestBaseTest
{
    @Test
    void testConstructor()
    {
        assertThat(new DummyImplementation(), notNullValue());
    }

    private static class DummyImplementation extends ImporterFactoryTestBase<DummyImporterFactory>
    {
        @Override
        protected DummyImporterFactory createFactory()
        {
            throw new UnsupportedOperationException("Unimplemented method 'createFactory'");
        }

        @Override
        protected List<String> getSupportedFilenames()
        {
            throw new UnsupportedOperationException("Unimplemented method 'getSupportedFilenames'");
        }

        @Override
        protected List<String> getUnsupportedFilenames()
        {
            throw new UnsupportedOperationException("Unimplemented method 'getUnsupportedFilenames'");
        }
    }

    private static class DummyImporterFactory extends ImporterFactory
    {
        @Override
        public boolean supportsFile(final InputFile file)
        {
            throw new UnsupportedOperationException("Unimplemented method 'supportsFile'");
        }

        @Override
        public Importer createImporter(final InputFile file, final ImportEventListener listener)
        {
            throw new UnsupportedOperationException("Unimplemented method 'createImporter'");
        }
    }
}
