package openfasttrack.importer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import openfasttrack.core.SpecificationItem;

/**
 * This service provides a method
 */
public class ImporterService
{
    private final ImporterFactoryLoader factoryLoader;

    public ImporterService()
    {
        this(new ImporterFactoryLoader());
    }

    ImporterService(final ImporterFactoryLoader factoryLoader)
    {
        this.factoryLoader = factoryLoader;
    }

    /**
     * Import the given file using the matching {@link Importer} using charset
     * {@link StandardCharsets#UTF_8 UTF-8} for reading.
     *
     * @param file
     *            the file to import.
     * @return a {@link List} of {@link SpecificationItem} imported from the
     *         file.
     */
    public List<SpecificationItem> importFile(final Path file)
    {
        return importFile(file, StandardCharsets.UTF_8);
    }

    /**
     * Import the given file using the matching {@link Importer}.
     *
     * @param file
     *            the file to import.
     * @param charset
     *            the {@link Charset} used for reading the file.
     * @return a {@link List} of {@link SpecificationItem} imported from the
     *         file.
     */
    public List<SpecificationItem> importFile(final Path file, final Charset charset)
    {
        final SpecificationItemListBuilder builder = new SpecificationItemListBuilder();
        final Importer importer = createImporter(file, charset, builder);
        importer.runImport();
        return builder.build();
    }

    private Importer createImporter(final Path file, final Charset charset,
            final SpecificationItemListBuilder builder)
    {
        final ImporterFactory importerFactory = this.factoryLoader.getImporterFactory(file);
        return importerFactory.createImporter(file, charset, builder);
    }
}
