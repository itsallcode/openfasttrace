package openfasttrack.mode;

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

import java.nio.file.Path;
import java.util.List;

import openfasttrack.Reporter;
import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.Trace;
import openfasttrack.core.Tracer;
import openfasttrack.report.ReportService;
import openfasttrack.report.ReportVerbosity;

public class ReportMode extends AbstractMode<ReportMode> implements Reporter
{
    private final Tracer tracer = new Tracer();
    private final ReportService reportService = new ReportService();
    private ReportVerbosity verbosity = ReportVerbosity.FAILURE_DETAILS;

    @Override
    public Trace trace()
    {
        final List<LinkedSpecificationItem> linkedItems = importLinkedSpecificationItems();
        return this.tracer.trace(linkedItems);
    }

    @Override
    public void reportToFileInFormat(final Trace trace, final Path output, final String format)
    {
        this.reportService.reportTraceToPath(trace, output, this.verbosity, this.newline);
    }

    @Override
    public Reporter setReportVerbosity(final ReportVerbosity verbosity)
    {
        this.verbosity = verbosity;
        return this;
    }

    @Override
    public void reportToStdOutInFormat(final Trace trace, final String format)
    {
        this.reportService.reportTraceToStdOut(trace, this.verbosity, this.newline);
    }

    @Override
    protected ReportMode self()
    {
        return this;
    }
}
