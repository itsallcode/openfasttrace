package org.itsallcode.openfasttrace.api.importer;

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

/**
 * Common context shared by all {@link ImporterFactory}s. This allows importers
 * to access common infrastructure, e.g. the {@link ImporterService}.
 */
public class ImporterContext
{
    private final ImportSettings settings;
    private ImporterService importerService;

    /**
     * Creates a new {@link ImporterContext}.
     * 
     * @param settings
     *            importer specific configuration.
     */
    public ImporterContext(final ImportSettings settings)
    {
        this.settings = settings;
    }

    /**
     * Internal API used by the {@link ImporterService}.
     * 
     * @param importerService
     *            the common instance of {@link ImporterService}.
     */
    public void setImporterService(final ImporterService importerService)
    {
        this.importerService = importerService;
    }

    /**
     * Get the common instance of {@link ImporterService}. Can be used e.g. for
     * delegating to other importers.
     * 
     * @return the common instance of {@link ImporterService}.
     */
    public ImporterService getImporterService()
    {
        return Objects.requireNonNull(this.importerService, "Importer service was not initialized");
    }

    /**
     * Get importer specific configuration.
     * 
     * @return importer specific configuration.
     */
    public ImportSettings getImportSettings()
    {
        return this.settings;
    }
}