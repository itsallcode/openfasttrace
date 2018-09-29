package org.itsallcode.openfasttrace;

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
import static org.junit.Assert.assertThat;

import org.itsallcode.openfasttrace.ReportSettings.Builder;
import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.report.ReportVerbosity;
import org.junit.Before;
import org.junit.Test;

public class TestReportSettings
{
    private Builder builder;

    @Before
    public void before()
    {
        this.builder = new ReportSettings.Builder();
    }

    @Test
    public void testDefaultVerbosity()
    {
        assertThat(this.builder.build().getReportVerbosity(),
                equalTo(ReportVerbosity.FAILURE_DETAILS));
    }

    @Test
    public void testOriginNotShownByDefault()
    {
        assertThat(this.builder.build().showOrigin(), equalTo(false));
    }

    @Test
    public void testDefaultOutputFormat()
    {
        assertThat(this.builder.build().getOutputFormat(), equalTo("plain"));
    }

    @Test
    public void testDefaultNewlineFormat()
    {
        assertThat(this.builder.build().getNewlineFormat(), equalTo(Newline.UNIX));
    }

    @Test
    public void testBuildWithVerbosity()
    {
        assertThat(this.builder.verbosity(ReportVerbosity.ALL).build().getReportVerbosity(),
                equalTo(ReportVerbosity.ALL));
    }

    @Test
    public void testBuildWithOriginShown()
    {
        assertThat(this.builder.showOrigin(true).build().showOrigin(), equalTo(true));
    }

    @Test
    public void testBuildWithOutputFormat()
    {
        assertThat(this.builder.outputFormat("html").build().getOutputFormat(), equalTo("html"));
    }

    @Test
    public void testBuildWithNewlineFormat()
    {
        assertThat(this.builder.newlineFormat(Newline.OLDMAC).build().getNewlineFormat(),
                equalTo(Newline.OLDMAC));
    }
}
