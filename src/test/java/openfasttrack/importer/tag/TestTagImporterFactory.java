package openfasttrack.importer.tag;

import static java.util.Arrays.asList;

import java.util.List;

import openfasttrack.importer.ImporterFactoryTestBase;

/**
 * Tests for {@link TagImporterFactory}
 */
public class TestTagImporterFactory extends ImporterFactoryTestBase<TagImporterFactory>
{
    @Override
    protected TagImporterFactory createFactory()
    {
        return new TagImporterFactory();
    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("file.java", "file.JAVA", "FILE.java", "FILE.JAVA", "file.md.java");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("file.md", "file.jav", "file.ml", "file.1java", "file.java1", "file.java.md",
                "file_java", "filejava");
    }
}
