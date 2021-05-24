package org.itsallcode.openfasttrace.report.aspec;

/*-
 * #%L
 * OpenFastTrace HTML Reporter
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterFactory;

/**
 * A {@link ReporterFactory} for SpecObject reports. This supports the
 * <code>specobject</code> format.
 */
public class ASpecReporterFactory extends ReporterFactory
{
    private static final String ASPEC_REPORT_FORMAT = "aspec";

    @Override
    public boolean supportsFormat(String format)
    {
        return ASPEC_REPORT_FORMAT.equalsIgnoreCase(format);
    }

    @Override
    public Reportable createImporter(Trace trace)
    {
        return new ASpecReport(trace, getContext());
    }

}
