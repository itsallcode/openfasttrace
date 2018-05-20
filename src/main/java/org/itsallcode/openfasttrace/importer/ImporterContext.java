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
import java.util.Objects;

import org.itsallcode.openfasttrace.importer.legacytag.config.LegacyTagImporterConfig;

public class ImporterContext
{
    private final LegacyTagImporterConfig tagImporterConfig;
    private ImporterService importerService;

    public ImporterContext(final LegacyTagImporterConfig tagImporterConfig)
    {
        this.tagImporterConfig = tagImporterConfig;
    }

    public void setImporterService(final ImporterService importerService)
    {
        this.importerService = importerService;
    }

    public ImporterService getImporterService()
    {
        return Objects.requireNonNull(this.importerService, "Importer service was not initialized");
    }

    public LegacyTagImporterConfig getTagImporterConfig()
    {
        return this.tagImporterConfig;
    }
}
