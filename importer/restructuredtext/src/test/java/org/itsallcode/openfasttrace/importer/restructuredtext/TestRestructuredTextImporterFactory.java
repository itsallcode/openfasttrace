package org.itsallcode.openfasttrace.importer.restructuredtext;

import org.itsallcode.openfasttrace.testutil.importer.ImporterFactoryTestBase;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Tests for {@link RestructuredTextImporterFactory}
 */
class TestRestructuredTextImporterFactory extends ImporterFactoryTestBase<RestructuredTextImporterFactory>
{
    @Override
    protected RestructuredTextImporterFactory createFactory()
    {
        return new RestructuredTextImporterFactory();
    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("file.rst", "file.RST", "FILE.rst", "FILE.RST");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("file.rs", "file.rest", "filerst", "file.rst.");
    }
}
