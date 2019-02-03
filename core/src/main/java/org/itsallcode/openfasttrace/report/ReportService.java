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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.itsallcode.openfasttrace.ReportSettings;
import org.itsallcode.openfasttrace.core.Trace;

public class ReportService
{
    private final ReporterFactoryLoader reporterFactoryLoader;

    public ReportService(ReportSettings settings)
    {
        this(new ReporterFactoryLoader(new ReporterContext(settings)));
    }

    public ReportService(ReporterFactoryLoader reporterFactoryLoader)
    {
        this.reporterFactoryLoader = reporterFactoryLoader;
    }

    public void reportTraceToPath(final Trace trace, final Path outputPath,
            final String outputFormat)
    {
        try (OutputStream outputStream = Files.newOutputStream(outputPath))
        {
            reportTraceToStream(trace, outputStream, outputFormat);
        }
        catch (final IOException e)
        {
            throw new ReportException("Error generating stream to output path " + outputPath, e);
        }
    }

    public void reportTraceToStdOut(final Trace trace, final String outputFormat)
    {
        reportTraceToStream(trace, getStdOutStream(), outputFormat);
    }

    // Using System.out by intention
    @SuppressWarnings("squid:S106")
    private PrintStream getStdOutStream()
    {
        return System.out;
    }

    private void reportTraceToStream(final Trace trace, final OutputStream outputStream,
            final String outputFormat)
    {
        final Reportable report = createReport(trace, outputFormat);
        report.renderToStream(outputStream);
        try
        {
            outputStream.flush();
        }
        catch (final IOException exception)
        {
            throw new ReportException(exception.getMessage());
        }
    }

    protected Reportable createReport(final Trace trace, final String outputFormat)
    {
        final ReporterFactory reporterFactory = reporterFactoryLoader
                .getReporterFactory(outputFormat);
        return reporterFactory.createImporter(trace);
    }
}
