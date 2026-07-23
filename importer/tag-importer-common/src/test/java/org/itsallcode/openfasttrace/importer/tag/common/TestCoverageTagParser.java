package org.itsallcode.openfasttrace.importer.tag.common;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class TestCoverageTagParser
{
    private static final String FILE = "source.file";

    @Test
    void testImportsFullTag()
    {
        final ImportEventListener listener = mock(ImportEventListener.class);
        final LineConsumer parser = CoverageTagParser.create(null, inputFile(), listener);

        final String coverageTag = "[" + "impl~name~1->dsn~covered~2>>utest]";
        parser.readLine(3, coverageTag);

        final InOrder inOrder = inOrder(listener);
        inOrder.verify(listener).beginSpecificationItem();
        inOrder.verify(listener).setLocation(FILE, 3);
        inOrder.verify(listener).setId(SpecificationItemId.parseId("impl~name~1"));
        inOrder.verify(listener).addCoveredId(SpecificationItemId.parseId("dsn~covered~2"));
        inOrder.verify(listener).addNeededArtifactType("utest");
        inOrder.verify(listener).endSpecificationItem();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testImportsConfiguredShortTag()
    {
        final PathConfig config = mock(PathConfig.class);
        when(config.getCoveredItemArtifactType()).thenReturn("req");
        when(config.getCoveredItemNamePrefix()).thenReturn("prefix.");
        when(config.getTagArtifactType()).thenReturn("utest");
        final ImportEventListener listener = mock(ImportEventListener.class);
        final LineConsumer parser = CoverageTagParser.create(config, inputFile(), listener);

        parser.readLine(2, "[[covered:3]]");

        final InOrder inOrder = inOrder(listener);
        inOrder.verify(listener).beginSpecificationItem();
        inOrder.verify(listener).setLocation(FILE, 2);
        inOrder.verify(listener).setId(SpecificationItemId.createId("utest", "prefix.covered-1743877134"));
        inOrder.verify(listener).addCoveredId(SpecificationItemId.createId("req", "prefix.covered", 3));
        inOrder.verify(listener).endSpecificationItem();
        inOrder.verifyNoMoreInteractions();
    }

    private static InputFile inputFile()
    {
        return StreamInput.forReader(Paths.get(FILE), new BufferedReader(new StringReader("")));
    }
}
