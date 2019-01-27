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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.openfasttrace.cli.StandardDirectoryService;
import org.itsallcode.openfasttrace.core.*;
import org.itsallcode.openfasttrace.importer.ImportSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ITestSelfTrace
{
    private Oft oft;

    @BeforeEach
    void beforeEach()
    {
        this.oft = new OftRunner();
    }

    @Test
    void testSelfTrace()
    {
        final ImportSettings importSettings = buildOftSettings();
        final Trace trace = trace(importSettings);
        this.oft.reportToStdOut(trace);
        assertSelfTraceClean(trace);
    }

    private void assertSelfTraceClean(final Trace trace)
    {
        if (!trace.hasNoDefects())
        {
            final String message = createSelfTraceReport(trace);
            throw new AssertionError(message);
        }
    }

    protected String createSelfTraceReport(final Trace trace)
    {
        String message = "Self trace has " + trace.countDefects() + " / " + trace.count()
                + " defect items.\n\n";
        for (final LinkedSpecificationItem item : trace.getDefectItems())
        {
            final Location location = item.getLocation();
            message += "at " + item.getId() + " (" + location.getPath() + ":" + location.getLine()
                    + ")\n";
        }
        return message;
    }

    private ImportSettings buildOftSettings()
    {
        final Path baseDir = Paths.get(new StandardDirectoryService().getCurrent());
        return ImportSettings.builder() //
                .addInputs(baseDir.resolve("src/main")) //
                .addInputs(baseDir.resolve("src/test/java")) //
                .addInputs(baseDir.resolve("doc/design.md")) //
                .addInputs(baseDir.resolve("doc/system_requirements.md")) //
                .build();
    }

    private Trace trace(final ImportSettings importSettings)
    {
        final List<SpecificationItem> items = this.oft.importItems(importSettings);
        final List<LinkedSpecificationItem> linkedItems = this.oft.link(items);
        return this.oft.trace(linkedItems);
    }
}
