package org.itsallcode.openfasttrace.api.importer;

/*-
 * #%L
 * OpenFastTrace API
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * This service provides convenient methods for importing
 * {@link SpecificationItem}s that automatically use the correct
 * {@link Importer} based on the filename.
 */
public interface ImporterService
{
    /**
     * Import a file's contents
     * 
     * @param file
     *            file to be imported
     * @return list of recognized specification items
     */
    List<SpecificationItem> importFile(InputFile file);

    /**
     * Create a new {@link MultiFileImporter} using the given
     * {@link ImportEventListener}.
     * 
     * @param builder
     *            the builder used by the new importer.
     * @return a new importer.
     */
    MultiFileImporter createImporter(ImportEventListener builder);

    /**
     * Create a new {@link MultiFileImporter}.
     * 
     * @return a new importer.
     */
    MultiFileImporter createImporter();
}
