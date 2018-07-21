package org.itsallcode.openfasttrace.report.html;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

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

import java.io.File;
import java.io.IOException;

import org.itsallcode.openfasttrace.testutil.AbstractFileBasedTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

public class TestHtmlReportCssInlining extends AbstractFileBasedTest
{
    private static final String CSS = "* {\n  font-family: helvetica, arial;\n}";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
    private File specFile;

    @Before
    public void before() throws IOException
    {
        this.specFile = this.tempFolder.newFile("spec.md");
        writeTextFile(this.specFile, "Dummy content");
        this.exit.expectSystemExitWithStatus(0);
    }

    // [itest->dsn~reporting.html.inline_css~1]
    @Test
    public void testHtmlReportContainsInlineCSS()
    {
        this.exit.checkAssertionAfterwards(() -> {
            final String stdOut = this.systemOutRule.getLog();
            assertThat(stdOut, containsString(CSS));
        });
        runWithArguments("trace", "-o", "html", this.specFile.toString());
    }
}