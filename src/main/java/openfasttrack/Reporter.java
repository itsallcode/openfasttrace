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
package openfasttrack;

import java.nio.file.Path;
import java.util.List;

import openfasttrack.core.Newline;
import openfasttrack.core.Trace;
import openfasttrack.report.ReportVerbosity;

/**
 * OFT requirements tracer
 */
public interface Reporter
{
    String DEFAULT_REPORT_FORMAT = "plain";
    ReportVerbosity DEFAULT_VERBOSITY = ReportVerbosity.FAILURE_DETAILS;

    /**
     * Select one or more input files
     * 
     * @param inputs
     *            input files
     * @return a <code>Reporter</code> instance for fluent programming
     */
    Reporter addInputs(final Path... inputs);

    /**
     * Select one or more input files
     * 
     * @param inputs
     *            input files
     * @return a <code>Reporter</code> instance for fluent programming
     */
    Reporter addInputs(final List<Path> inputs);

    /**
     * Set the representation for new line
     * 
     * @param newline
     *            type of newline
     * @return a <code>Reporter</code> instance for fluent programming
     */
    Reporter setNewline(Newline newline);

    /**
     * Select how verbose the tracing report should be
     * 
     * @param verbosity
     *            report verbosity
     * @return a <code>Reporter</code> instance for fluent programming
     */
    Reporter setReportVerbosity(final ReportVerbosity verbosity);

    /**
     * Run a trace on the input files
     */
    Trace trace();

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
     *            report format (this is a name defined in the respective reporter
     *            plug-in)
     */
    void reportToFileInFormat(final Trace trace, final Path output, final String format);

    /**
     * Write the tracing report to standard out
     * 
     * @param trace
     *            trace result to be written
     * 
     * @param format
     *            report format (this is a name defined in the respective reporter
     *            plug-in)
     */
    void reportToStdOutInFormat(final Trace trace, final String format);
}
