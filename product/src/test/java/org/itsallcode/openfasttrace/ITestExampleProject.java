package org.itsallcode.openfasttrace;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.core.Oft;
import org.itsallcode.openfasttrace.core.OftRunner;
import org.junit.jupiter.api.Test;

class ITestExampleProject
{

    @Test
    void test()
    {
        final Oft oft = new OftRunner();
        final List<SpecificationItem> items = oft.importItems(ImportSettings.builder()
                .addInputs(Paths.get("src/test/resources/example")).build());
        final List<LinkedSpecificationItem> linkedItems = oft.link(items);
        final Trace trace = oft.trace(linkedItems);
        oft.reportToStdOut(trace);
        assertTrue(trace.hasNoDefects());
    }
}
