package org.itsallcode.openfasttrace.importer.rif;

import static java.util.Arrays.asList;

import java.util.List;

import org.itsallcode.openfasttrace.importer.ImporterFactoryTestBase;

public class TestRifImporterFactory extends ImporterFactoryTestBase<RifImporterFactory>
{

    @Override
    protected RifImporterFactory createFactory()
    {
        return new RifImporterFactory();

    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("file.rif", "file.RIF", "FILE.rif", "FILE.RIF", "file.md.rif");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("file.md", "file.ri", "file.if", "file.1rif", "file.rif1", "file.rif.md",
                "file_rif", "filerif");
    }

}
