
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

import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.importer.tag.config.TagImporterConfig;

/**
 * OFT requirements tracer
 */
public interface Reporter
{
    /**
     * Select one or more input files
     * 
     * @param inputs
     *            input files
     * @return a <code>Reporter</code> instance for fluent programming
     */
    public Reporter addInputs(final Path... inputs);

    /**
     * Select one or more input files
     * 
     * @param inputs
     *            input files
     * @return a <code>Reporter</code> instance for fluent programming
     */
    public Reporter addInputs(final List<Path> inputs);

    /**
     * Set the filters to be applied during conversion
     * 
     * @param filterSettings
     *            the filter settings
     */
    public Reporter setFilters(FilterSettings filterSettings);

    /**
     * Set the {@link TagImporterConfig} for the
     * {@link LegacyTagImporterFactory}.
     * 
     * @param config
     *            the {@link TagImporterConfig} to set.
     * @return a <code>Reporter</code> instance for fluent programming
     */
    public Reporter setLegacyTagImporterPathConfig(final TagImporterConfig config);

    /**
     * Configure the report settings
     * 
     * @param settings
     *            report settings
     * @return a <code>Reporter</code> instance for fluent programming
     * 
     * @see {@link ReportSettings.Builder} for a list of available configuration
     *      options
     */
    public Reporter configureReport(ReportSettings settings);

    /**
     * Run a trace on the input files
     */
    public Trace trace();

    /**
     * Write the tracing stream to a file
     * 
     * @param trace
     *            trace result to be written
     * 
     * @param output
     *            output file or directory
     */
    public void reportToFile(final Trace trace, final Path output);

    /**
     * Write the tracing stream to standard out
     * 
     * @param trace
     *            trace result to be written
     */
    public void reportToStdOut(final Trace trace);
}