package org.itsallcode.openfasttrace;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.exporter.Exporter;
import org.itsallcode.openfasttrace.api.exporter.ExporterContext;
import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.core.exporter.ExporterFactoryLoader;
import org.itsallcode.openfasttrace.core.importer.ImporterFactoryLoader;
import org.itsallcode.openfasttrace.core.report.ReporterFactoryLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Answers;

class TestAllServicesAvailable
{
    private static ImporterFactoryLoader importerLoader;
    private static ExporterFactoryLoader exporterLoader;
    private static ReporterFactoryLoader reporterLoader;

    @TempDir
    static Path tempDir;

    @BeforeAll
    static void createFactoryLoaders()
    {
        importerLoader = createImporterLoader();
        exporterLoader = createExporterLoader();
        reporterLoader = createReporterLoader();
    }

    private static ImporterFactoryLoader createImporterLoader()
    {
        final ImporterContext contextMock = mock(ImporterContext.class,
                withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));

        return new ImporterFactoryLoader(contextMock);
    }

    private static ExporterFactoryLoader createExporterLoader()
    {
        final ExporterContext contextMock = mock(ExporterContext.class,
                withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        return new ExporterFactoryLoader(contextMock);
    }

    private static ReporterFactoryLoader createReporterLoader()
    {
        final ReporterContext contextMock = mock(ReporterContext.class,
                withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        return new ReporterFactoryLoader(contextMock);
    }

    @ParameterizedTest
    @CsvSource(
    { "md", "oreqm", "java", "zip" })
    void importerAvailable(final String suffix)
    {
        final InputFile file = RealFileInput.forPath(Paths.get("file." + suffix));
        if (!importerLoader.supportsFile(file))
        {
            fail("No importer found for file name suffix '" + suffix + "'");
        }
        final Importer importer = importerLoader.getImporterFactory(file).get().createImporter(file,
                mock(ImportEventListener.class));
        assertNotNull(importer);
    }

    @ParameterizedTest
    @CsvSource(
    { "specobject" })
    void exporterAvailable(final String format)
    {
        if (!exporterLoader.isFormatSupported(format))
        {
            fail("No exporter found for format '" + format + "'");
        }
        final Exporter exporter = exporterLoader.getExporterFactory(format).createExporter(
                tempDir.resolve("file." + format), format,
                StandardCharsets.UTF_8,
                Newline.UNIX, Stream.empty());
        assertNotNull(exporter);
    }

    @ParameterizedTest
    @CsvSource(
    { "aspec", "html", "plain", "ux" })
    void reporterAvailable(final String format)
    {
        if (!reporterLoader.isFormatSupported(format))
        {
            fail("No reporter found for format '" + format + "'");
        }
        final Reportable reportable = reporterLoader.getReporterFactory(format).createImporter(Trace.builder().build());
        assertNotNull(reportable);
    }
}
