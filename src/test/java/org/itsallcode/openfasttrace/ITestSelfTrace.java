package org.itsallcode.openfasttrace;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.openfasttrace.core.*;
import org.junit.Before;
import org.junit.Test;

public class ITestSelfTrace
{
    private Oft oft;

    @Before
    public void setUp()
    {
        this.oft = new OftRunner();
    }

    @Test
    public void testSelfTrace()
    {
        final ImportSettings importSettings = buildOftSettings();
        final Trace trace = trace(importSettings);
        this.oft.reportToStdOut(trace);
        assertThat("defect count", trace.countDefects(), equalTo(0));
    }

    private ImportSettings buildOftSettings()
    {
        final Path baseDir = Paths.get(".").toAbsolutePath();
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
