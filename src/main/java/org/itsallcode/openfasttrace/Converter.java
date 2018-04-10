
package org.itsallcode.openfasttrace;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.FilterSettings;
import org.itsallcode.openfasttrace.importer.legacytag.LegacyTagImporterFactory;
import org.itsallcode.openfasttrace.importer.legacytag.config.LegacyTagImporterConfig;

/**
 * Convert between different requirements formats (e.g. from ReqM2 to Markdown)
 */
public interface Converter
{
    /**
     * Select one or more input files
     * 
     * @param inputs
     *            input files
     * @return a <code>Converter</code> instance for fluent programming
     */
    public Converter addInputs(final Path... inputs);

    /**
     * Select one or more input files
     * 
     * @param inputs
     *            input files
     * @return a <code>Converter</code> instance for fluent programming
     */
    public Converter addInputs(final List<Path> inputs);

    /**
     * Set the filters to be applied during conversion
     * 
     * @param filterSettings
     *            the filter settings
     */
    public Converter setFilters(FilterSettings filterSettings);

    /**
     * Set the representation for new line
     * 
     * @param newline
     *            type of newline
     * @return a <code>Converter</code> instance for fluent programming
     */
    public Converter setNewline(Newline newline);

    /**
     * Set the {@link LegacyTagImporterConfig} for the
     * {@link LegacyTagImporterFactory}.
     * 
     * @param config
     *            the {@link LegacyTagImporterConfig} to set.
     * @return a <code>Converter</code> instance for fluent programming
     */
    public Converter setLegacyTagImporterPathConfig(final LegacyTagImporterConfig config);

    /**
     * Convert the collected requirements into target requirement format
     * 
     * @param output
     *            output file
     * 
     * @param format
     *            target format (this is a name defined in the respective
     *            exporter plug-in)
     */
    public void convertToFileInFormat(final Path output, final String format);
}
