package openfasttrack.importer;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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


import java.nio.file.Path;
import java.util.List;

import openfasttrack.core.SpecificationItem;

/**
 * This service provides convenient methods for importing
 * {@link SpecificationItem}s that automatically use the correct
 * {@link Importer} based on the filename.
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

    public List<SpecificationItem> importFile(final Path file)
    {
        return createImporter() //
                .importFile(file) //
                .getImportedItems();
    }

    public MultiFileImporter createImporter()
    {
        return new MultiFileImporter(new SpecificationListBuilder(), this.factoryLoader);
    }
}
