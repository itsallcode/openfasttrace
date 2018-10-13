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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.*;

import org.itsallcode.openfasttrace.*;
import org.itsallcode.openfasttrace.core.*;
import org.itsallcode.openfasttrace.report.ReportVerbosity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

public class ITestOftAsReporter extends AbstractOftTest
{
    private Oft oft;
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();
    private Trace trace;
    private List<LinkedSpecificationItem> linkedItems;

    @Before
    public void setUp() throws UnsupportedEncodingException
    {
        perpareOutput();
        final ImportSettings settings = ImportSettings.builder().addInputs(this.docDir).build();
        this.oft = Oft.create();
        final List<SpecificationItem> items = this.oft.importItems(settings);
        this.linkedItems = this.oft.link(items);
        this.trace = this.oft.trace(this.linkedItems);
    }

    @Test
    public void testTraceToFile() throws IOException
    {
        this.oft.reportToPath(this.trace, this.outputFile);
        assertStandardReportFileResult();
    }

    private void assertStandardReportFileResult() throws IOException
    {
        assertOutputFileExists(true);
        assertOutputFileContentStartsWith("ok - 5 total");
    }

    @Test
    public void testTraceWithReportVerbosityMinimal() throws IOException
    {
        final ReportSettings settings = ReportSettings.builder().verbosity(ReportVerbosity.MINIMAL)
                .build();
        this.oft.reportToPath(this.trace, this.outputFile, settings);
        assertOutputFileExists(true);
        assertOutputFileContentStartsWith("ok");
    }

    @Test
    public void testTraceMacNewlines() throws IOException
    {
        final ReportSettings settings = ReportSettings.builder() //
                .newline(Newline.OLDMAC) //
                .build();
        this.oft.reportToPath(this.trace, this.outputFile, settings);
        assertThat(Files.exists(this.outputFile), equalTo(true));
        assertThat("Has old Mac newlines", getOutputFileContent().contains(CARRIAGE_RETURN),
                equalTo(true));
        assertThat("Has no Unix newlines", getOutputFileContent().contains(NEWLINE),
                equalTo(false));
    }

    @Test
    public void testTraceToStdOut() throws IOException
    {
        this.oft.reportToStdOut(this.trace);
        assertStandardReportStdOutResult();
    }

    private void assertStandardReportStdOutResult() throws IOException
    {
        assertStdOutStartsWith("ok - 5 total");
    }

    // [itest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    public void testFilterAllowsAllAtrifactsButDsn()
    {
        final Set<String> artifactTypes = new HashSet<>(Arrays.asList("feat", "req"));
        assertThat("Number of items with type \"dsn\" in regular trace",
                countItemsOfArtifactTypeInTrace("dsn", this.trace), greaterThan(0L));
        final FilterSettings filterSettings = new FilterSettings.Builder()
                .artifactTypes(artifactTypes).build();
        final Trace filteredTrace = traceWithFilters(filterSettings);
        assertThat("Number of items with ignored type \"dsn\" in filtered trace",
                countItemsOfArtifactTypeInTrace("dsn", filteredTrace), equalTo(0L));
    }

    private Trace traceWithFilters(final FilterSettings filterSettings)
    {
        final ImportSettings importSettings = ImportSettings.builder().filter(filterSettings)
                .build();
        final List<SpecificationItem> filteredItems = this.oft.importItems(importSettings);
        final List<LinkedSpecificationItem> filteredSpecificationItems = this.oft
                .link(filteredItems);
        final Trace filteredTrace = this.oft.trace(filteredSpecificationItems);
        return filteredTrace;
    }

    private long countItemsOfArtifactTypeInTrace(final String artifactType, final Trace trace)
    {
        return trace.getItems().stream() //
                .filter(item -> {
                    return item.getId().getArtifactType().equals(artifactType);
                }) //
                .count();
    }
}
