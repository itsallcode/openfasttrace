package org.itsallcode.openfasttrace.api;

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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.ReportSettings.Builder;
import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.report.ReportVerbosity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestReportSettings
{
    private Builder builder;

    @BeforeEach
    void beforeEach()
    {
        this.builder = ReportSettings.builder();
    }

    @Test
    void testDefaultVerbosity()
    {
        assertThat(this.builder.build().getReportVerbosity(),
                equalTo(ReportVerbosity.FAILURE_DETAILS));
    }

    @Test
    void testOriginNotShownByDefault()
    {
        assertThat(this.builder.build().showOrigin(), equalTo(false));
    }

    @Test
    void testDefaultOutputFormat()
    {
        assertThat(this.builder.build().getOutputFormat(), equalTo("plain"));
    }

    @Test
    void testDefaultNewline()
    {
        assertThat(this.builder.build().getNewline(), equalTo(Newline.UNIX));
    }

    @Test
    void testBuildWithVerbosity()
    {
        assertThat(this.builder.verbosity(ReportVerbosity.ALL).build().getReportVerbosity(),
                equalTo(ReportVerbosity.ALL));
    }

    @Test
    void testBuildWithOriginShown()
    {
        assertThat(this.builder.showOrigin(true).build().showOrigin(), equalTo(true));
    }

    @Test
    void testBuildWithOutputFormat()
    {
        assertThat(this.builder.outputFormat("html").build().getOutputFormat(), equalTo("html"));
    }

    @Test
    void testBuildWithNewline()
    {
        assertThat(this.builder.newline(Newline.OLDMAC).build().getNewline(),
                equalTo(Newline.OLDMAC));
    }
}
