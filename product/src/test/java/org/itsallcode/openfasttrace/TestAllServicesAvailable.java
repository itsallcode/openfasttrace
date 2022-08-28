package org.itsallcode.openfasttrace;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.exporter.Exporter;
import org.itsallcode.openfasttrace.api.exporter.ExporterContext;
import org.itsallcode.openfasttrace.api.exporter.ExporterFactory;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.api.importer.ImporterFactory;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.api.report.ReporterFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Answers;

class TestAllServicesAvailable
{
    private static List<ImporterFactory> importerFactories;
    private static List<ExporterFactory> exporterFactories;
    private static List<ReporterFactory> reporterFactories;

    @TempDir
    static Path tempDir;

    @BeforeAll
    static void loadFactories()
    {
        importerFactories = loadImporterFactories();
        exporterFactories = loadExporterFactories();
        reporterFactories = loadReporterFactories();
    }

    private static List<ImporterFactory> loadImporterFactories()
    {
        final ServiceLoader<ImporterFactory> loader = ServiceLoader.load(ImporterFactory.class);
        final ImporterContext contextMock = mock(ImporterContext.class,
                withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        final List<ImporterFactory> importerFactories = loader.stream() //
                .map(ServiceLoader.Provider::get).collect(toList());
        importerFactories.forEach(factory -> factory.init(contextMock));
        return importerFactories;
    }

    private static List<ExporterFactory> loadExporterFactories()
    {
        final ServiceLoader<ExporterFactory> loader = ServiceLoader.load(ExporterFactory.class);
        final ExporterContext contextMock = mock(ExporterContext.class,
                withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        final List<ExporterFactory> exporterFactories = loader.stream() //
                .map(ServiceLoader.Provider::get).collect(toList());
        exporterFactories.forEach(factory -> factory.init(contextMock));
        return exporterFactories;
    }

    private static List<ReporterFactory> loadReporterFactories()
    {
        final ServiceLoader<ReporterFactory> loader = ServiceLoader.load(ReporterFactory.class);
        final ReporterContext contextMock = mock(ReporterContext.class,
                withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        final List<ReporterFactory> reporterFactories = loader.stream() //
                .map(ServiceLoader.Provider::get).collect(toList());
        reporterFactories.forEach(factory -> factory.init(contextMock));
        return reporterFactories;
    }

    @ParameterizedTest
    @CsvSource(
    { "md", "oreqm", "java", "zip" })
    void importerAvailable(final String suffix)
    {
        final InputFile file = RealFileInput.forPath(Paths.get("file." + suffix));
        final Optional<ImporterFactory> factory = importerFactories.stream() //
                .filter(f -> f.supportsFile(file)) //
                .findAny();
        if (factory.isEmpty())
        {
            fail("No importer found for file name suffix '" + suffix + "'");
        }
        final Importer importer = factory.get().createImporter(file, mock(ImportEventListener.class));
        assertNotNull(importer);
    }

    @ParameterizedTest
    @CsvSource(
    { "specobject" })
    void exporterAvailable(final String format)
    {
        final Optional<ExporterFactory> factory = exporterFactories.stream() //
                .filter(f -> f.supportsFormat(format)) //
                .findAny();
        if (factory.isEmpty())
        {
            fail("No exporter found for format '" + format + "'");
        }
        final Exporter exporter = factory.get().createExporter(tempDir.resolve("file." + format), format,
                StandardCharsets.UTF_8,
                Newline.UNIX, Stream.empty());
        assertNotNull(exporter);
    }

    @ParameterizedTest
    @CsvSource(
    { "aspec", "html", "plain" })
    void reporterAvailable(final String format)
    {
        final Optional<ReporterFactory> factory = reporterFactories.stream() //
                .filter(f -> f.supportsFormat(format)) //
                .findAny();
        if (factory.isEmpty())
        {
            fail("No reporter found for format '" + format + "'");
        }
        final Reportable reportable = factory.get().createImporter(Trace.builder().build());
        assertNotNull(reportable);
    }
}
