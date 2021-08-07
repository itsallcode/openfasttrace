package org.itsallcode.openfasttrace.core.importer;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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