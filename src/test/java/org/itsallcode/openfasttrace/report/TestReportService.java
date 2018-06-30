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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.core.Trace;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestReportService
{
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    Trace traceMock;

    private ReportService service;

    @Before
    public void prepareTest()
    {
        MockitoAnnotations.initMocks(this);
        this.service = new ReportService();
    }

    @Test
    public void testReportPlainText()
    {
        this.service.reportTraceToStdOut(this.traceMock, "plain", ReportVerbosity.MINIMAL,
                Newline.UNIX);
        assertThat(this.systemOutRule.getLog(), equalTo("not ok\n"));
    }

    @Test
    public void testReportHtml()
    {
        this.service.reportTraceToStdOut(this.traceMock, "html", ReportVerbosity.MINIMAL,
                Newline.UNIX);
        assertThat(this.systemOutRule.getLog(), startsWith("<!DOCTYPE html>"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidReportFormatThrowsIllegalArgumentException()
    {
        this.service.reportTraceToStdOut(this.traceMock, "invalid", ReportVerbosity.QUIET,
                Newline.UNIX);
    }

    @Test(expected = ReportException.class)
    public void testReportToIllegalPathThrowsReportExpection() throws IOException
    {
        final File readOnlyFile = this.temporaryFolder.newFile();
        readOnlyFile.setReadOnly();
        this.service.reportTraceToPath(this.traceMock, readOnlyFile.toPath(), "plain",
                ReportVerbosity.QUIET, Newline.UNIX);
    }
}
