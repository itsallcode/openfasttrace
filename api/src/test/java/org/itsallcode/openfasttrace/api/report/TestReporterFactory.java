package org.itsallcode.openfasttrace.api.report;

/*-
 * #%L
 * OpenFastTrace API
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.api.report.ReporterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestReporterFactory
{
    private ReporterFactory reporterFactory;
    private ReporterContext context;

    @BeforeEach
    void setUp()
    {
        context = new ReporterContext(null);
        reporterFactory = new TestingReporterFactory();
    }

    @Test
    void testInitSetsContext()
    {
        reporterFactory.init(context);
        assertThat(reporterFactory.getContext(), sameInstance(context));
    }

    private static class TestingReporterFactory extends ReporterFactory
    {
        @Override
        public boolean supportsFormat(String format)
        {
            return false;
        }

        @Override
        public Reportable createImporter(Trace trace)
        {
            return null;
        }
    }
}
