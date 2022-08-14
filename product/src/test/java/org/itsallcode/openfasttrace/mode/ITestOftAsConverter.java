package org.itsallcode.openfasttrace.mode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.core.Oft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ITestOftAsConverter extends AbstractOftTest
{
    private static final String SPECOBJECT_PREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<specdocument>";
    private Oft oft;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir) throws UnsupportedEncodingException
    {
        perpareOutput(tempDir);
        this.oft = Oft.create();
    }

    @Test
    void testConvertToSpecobjectFile() throws IOException
    {
        final ImportSettings settings = ImportSettings.builder().addInputs(this.docDir).build();
        final List<SpecificationItem> items = this.oft.importItems(settings);
        this.oft.exportToPath(items, this.outputFile);
        assertStandardFileExportResult();
    }

    private void assertStandardFileExportResult() throws IOException
    {
        assertOutputFileExists(true);
        assertOutputFileContentStartsWith(SPECOBJECT_PREAMBLE + "\n  <specobjects doctype=\"dsn\">");
    }
}