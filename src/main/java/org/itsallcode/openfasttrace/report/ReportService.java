package org.itsallcode.openfasttrace.report;

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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.report.html.HtmlReport;
import org.itsallcode.openfasttrace.report.plaintext.PlainTextReport;

public class ReportService
{
    public void reportTraceToPath(final Trace trace, final Path outputPath, final String format,
            final ReportVerbosity verbosity, final Newline newline)
    {
        try (OutputStream outputStream = Files.newOutputStream(outputPath))
        {
            reportTraceToStream(trace, format, verbosity, newline, outputStream);
        }
        catch (final IOException e)
        {
            throw new ReportException("Error generating stream to output path " + outputPath, e);
        }
    }

    public void reportTraceToStdOut(final Trace trace, final String format,
            final ReportVerbosity verbosity, final Newline newline)
    {
        reportTraceToStream(trace, format, verbosity, newline, getStdOutStream());
    }

    // Using System.out by intention
    @SuppressWarnings("squid:S106")
    private PrintStream getStdOutStream()
    {
        return System.out;
    }

    private void reportTraceToStream(final Trace trace, final String format,
            final ReportVerbosity verbosity, final Newline newline, final OutputStream outputStream)
    {
        final OutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        final Reportable report = createReport(trace, format, newline);
        report.renderToStreamWithVerbosityLevel(bufferedOutputStream, verbosity);
        try
        {
            bufferedOutputStream.flush();
        }
        catch (final IOException exception)
        {
            throw new ReportException(exception.getMessage());
        }
    }

    protected Reportable createReport(final Trace trace, final String format, final Newline newline)
    {
        Reportable report = null;
        switch (ReportFormat.parse(format))
        {
        case PLAIN_TEXT:
            report = new PlainTextReport(trace, newline);
            break;
        case HTML:
            report = new HtmlReport(trace);
            break;
        default:
            throw new IllegalArgumentException(
                    "Unable to create report with format \"" + format + "\"");
        }
        return report;
    }

}
