package openfasttrack.importer.markdown;

import static java.util.Arrays.asList;

import java.util.List;

import openfasttrack.importer.ImporterFactoryTestBase;

/**
 * Tests for {@link MarkdownImporterFactory}
 */
public class TestMarkdownImporterFactory extends ImporterFactoryTestBase<MarkdownImporterFactory>
{
    @Override
    protected MarkdownImporterFactory createFactory()
    {
        return new MarkdownImporterFactory();
    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("file.md", "file.MD", "FILE.md", "FILE.MD");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("file.m", "file.d", "file.txt", "file.1md", "file.md1");
    }
}
