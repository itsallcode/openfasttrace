package org.itsallcode.openfasttrace.mode;

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

import org.itsallcode.openfasttrace.ReportSettings;
import org.itsallcode.openfasttrace.Reporter;
import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.core.Tracer;
import org.itsallcode.openfasttrace.report.ReportService;

public class ReportMode extends AbstractMode<ReportMode> implements Reporter
{
    private final Tracer tracer = new Tracer();
    private final ReportService reportService = new ReportService();
    private ReportSettings settings;

    /**
     * Create a new instance of a {@link ReportMode} using the default report
     * settings.
     */
    public ReportMode()
    {
        this(new ReportSettings.Builder().build());
    }

    /**
     * Create a new instance of a {@link ReportMode} using the custom report
     * settings.
     */
    public ReportMode(final ReportSettings settings)
    {
        this.settings = settings;
    }

    @Override
    public Reporter configureReport(final ReportSettings settings)
    {
        this.settings = settings;
        return this;
    }

    @Override
    public Trace trace()
    {
        final List<LinkedSpecificationItem> linkedItems = importLinkedSpecificationItems();
        return this.tracer.trace(linkedItems);
    }

    @Override
    public void reportToFile(final Trace trace, final Path output)
    {
        this.reportService.reportTraceToPath(trace, output, this.settings);
    }

    @Override
    public void reportToStdOut(final Trace trace)
    {
        this.reportService.reportTraceToStdOut(trace, this.settings);
    }

    @Override
    protected ReportMode self()
    {
        return this;
    }
}