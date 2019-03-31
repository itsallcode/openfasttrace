package org.itsallcode.openfasttrace.report;

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

import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.core.serviceloader.Initializable;

/**
 * Super class for factories producing {@link Reportable}s.
 */
public abstract class ReporterFactory implements Initializable<ReporterContext>
{
    private ReporterContext context;

    /**
     * Check if this {@link ReporterFactory} supports creating
     * {@link Reportable}s for the given format.
     * 
     * @param format
     *            the format to check.
     * @return <code>true</code> if this {@link ReporterFactory} supports the
     *         given format.
     */
    public abstract boolean supportsFormat(final String format);

    /**
     * Create a new {@link Reportable}.
     *
     * @param trace
     *            the trace that will be reported.
     * @return the new {@link Reportable}.
     */
    public abstract Reportable createImporter(final Trace trace);

    @Override
    public void init(final ReporterContext context)
    {
        this.context = context;
    }

    /**
     * Get the {@link ReporterContext} set by the {@link #init(ReporterContext)}
     * method.
     * 
     * @return the {@link ReporterContext}.
     */
    public ReporterContext getContext()
    {
        return Objects.requireNonNull(this.context, "Context was not initialized");
    }
}
