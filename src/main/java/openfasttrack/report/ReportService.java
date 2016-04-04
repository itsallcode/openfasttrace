package openfasttrack.report;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import openfasttrack.core.Trace;

public class ReportService
{
    public void generateReport(final Trace trace, final Path outputFile,
            final ReportVerbosity verbosity)
    {
        try (OutputStream outputStream = new BufferedOutputStream(createOutputStream(outputFile)))
        {
            new PlainTextReport(trace).renderToStreamWithVerbosityLevel(outputStream, verbosity);
        }
        catch (final IOException e)
        {
            throw new ReportException("Error generating report to output file " + outputFile, e);
        }
    }

    private OutputStream createOutputStream(final Path outputFile) throws IOException
    {
        return outputFile == null ? System.out : Files.newOutputStream(outputFile);
    }
}
