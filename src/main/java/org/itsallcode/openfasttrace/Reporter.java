
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
import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.FilterSettings;
import org.itsallcode.openfasttrace.importer.legacytag.LegacyTagImporterFactory;
import org.itsallcode.openfasttrace.importer.legacytag.config.LegacyTagImporterConfig;
import org.itsallcode.openfasttrace.report.ReportVerbosity;

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
     * Set the representation for new line
     * 
     * @param newline
     *            type of newline
     * @return a <code>Reporter</code> instance for fluent programming
     */
    public Reporter setNewline(Newline newline);

    /**
     * Select how verbose the tracing report should be
     * 
     * @param verbosity
     *            report verbosity
     * @return a <code>Reporter</code> instance for fluent programming
     */
    public Reporter setReportVerbosity(final ReportVerbosity verbosity);

    /**
     * Set the {@link LegacyTagImporterConfig} for the
     * {@link LegacyTagImporterFactory}.
     * 
     * @param config
     *            the {@link LegacyTagImporterConfig} to set.
     * @return a <code>Reporter</code> instance for fluent programming
     */
    public Reporter setLegacyTagImporterPathConfig(final LegacyTagImporterConfig config);

    /**
     * Run a trace on the input files
     */
    public Trace trace();

    /**
     * Write the tracing report to a file
     * 
     * @param trace
     *            trace result to be written
     * 
     * @param output
     *            output file or directory
     * 
     * @param format
     *            report format (this is a name defined in the respective
     *            reporter plug-in)
     */
    public void reportToFileInFormat(final Trace trace, final Path output, final String format);

    /**
     * Write the tracing report to standard out
     * 
     * @param trace
     *            trace result to be written
     * 
     * @param format
     *            report format (this is a name defined in the respective
     *            reporter plug-in)
     */
    public void reportToStdOutInFormat(final Trace trace, final String format);
}
