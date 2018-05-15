package org.itsallcode.openfasttrace.importer.rif;

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
