package org.itsallcode.openfasttrace.core.importer;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * An implementation of the {@link ImporterService} interface. This service
 * provides convenient methods for importing {@link SpecificationItem}s that
 * automatically use the correct {@link Importer} based on the filename.
 */
public class ImporterServiceImpl implements ImporterService
{
    private final ImporterFactoryLoader factoryLoader;
    private final ImportSettings settings;

    /**
     * Create a new instance of an {@link ImporterServiceImpl}
     * 
     * @param factoryLoader
     *            loader for importer factories depending on the source
     * @param settings
     *            import settings (e.g. filters)
     */
    public ImporterServiceImpl(final ImporterFactoryLoader factoryLoader,
            final ImportSettings settings)
    {
        this.factoryLoader = factoryLoader;
        this.settings = settings;
    }

    @Override
    public List<SpecificationItem> importFile(final InputFile file)
    {
        return createImporter() //
                .importFile(file) //
                .getImportedItems();
    }

    @Override
    public MultiFileImporterImpl createImporter(final ImportEventListener builder)
    {
        return new MultiFileImporterImpl((SpecificationListBuilder) builder, this.factoryLoader);
    }

    @Override
    public MultiFileImporter createImporter()
    {
        return createImporter(
                SpecificationListBuilder.createWithFilter(this.settings.getFilters()));
    }
}