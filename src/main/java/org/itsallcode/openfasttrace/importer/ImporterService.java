package org.itsallcode.openfasttrace.importer;

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

import org.itsallcode.openfasttrace.FilterSettings;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.importer.input.InputFile;

/**
 * This service provides convenient methods for importing
 * {@link SpecificationItem}s that automatically use the correct
 * {@link Importer} based on the filename.
 */
public class ImporterService
{
    private final ImporterFactoryLoader factoryLoader;
    private FilterSettings filterSettings;
    private final ImporterContext context;

    public ImporterService(final ImporterContext context)
    {
        this(new ImporterFactoryLoader(context), context);
    }

    ImporterService(final ImporterFactoryLoader factoryLoader, final ImporterContext context)
    {
        this.factoryLoader = factoryLoader;
        this.context = context;
        this.context.setImporterService(this);
    }

    /**
     * Set the filters to be applied
     * 
     * @param filterSettings
     *            filter settings
     * @return <code>this</code> for fluent programming style
     */
    public ImporterService setFilters(final FilterSettings filterSettings)
    {
        this.filterSettings = filterSettings;
        return this;
    }

    public List<SpecificationItem> importFile(final InputFile file)
    {
        return createImporter() //
                .importFile(file) //
                .getImportedItems();
    }

    public MultiFileImporter createImporter(final ImportEventListener builder)
    {
        return new MultiFileImporter((SpecificationListBuilder) builder, this.factoryLoader);
    }

    public MultiFileImporter createImporter()
    {
        return createImporter(SpecificationListBuilder.createWithFilter(this.filterSettings));
    }
}
