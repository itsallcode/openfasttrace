package openfasttrack.importer.specobject;

import static java.util.Arrays.asList;

import java.util.List;

import openfasttrack.importer.ImporterFactoryTestBase;

/**
 * Tests for {@link SpecobjectImporterFactory}
 */
public class TestSpecobjectImporterFactory
        extends ImporterFactoryTestBase<SpecobjectImporterFactory>
{

    @Override
    protected SpecobjectImporterFactory createFactory()
    {
        return new SpecobjectImporterFactory();
    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("file.xml", "file.XML", "FILE.xml", "FILE.XML", "file.md.xml");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("file.md", "file.xm", "file.ml", "file.1xml", "file.xml1", "file.xml.md");
    }
}
