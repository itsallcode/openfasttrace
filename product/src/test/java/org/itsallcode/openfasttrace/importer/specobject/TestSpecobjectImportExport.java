package org.itsallcode.openfasttrace.importer.specobject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.itsallcode.openfasttrace.testutil.core.TraceAssertions.assertTraceContainsDefectIds;
import static org.itsallcode.openfasttrace.testutil.core.TraceAssertions.assertTraceSize;
import static org.itsallcode.openfasttrace.testutil.core.TraceAssertions.getItemFromTraceForId;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.matcher.auto.AutoMatcher;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.core.Linker;
import org.itsallcode.openfasttrace.core.Tracer;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;

class TestSpecobjectImportExport
{
    @Test
    void testTraceContent()
    {
        final String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<specdocument>\n" //
                + "    <specobjects doctype=\"impl\">\n" //
                + "        <specobject>\n" //
                + "            <id>exampleB-3454416016</id>\n"
                + "            <status>approved</status>\n" //
                + "            <version>0</version>\n" //
                + "            <sourcefile>source.java</sourcefile>\n" //
                + "            <sourceline>1</sourceline>\n" //
                + "            <providescoverage>\n" //
                + "                <provcov>\n" //
                + "                    <linksto>dsn:exampleB</linksto>\n"
                + "                    <dstversion>1</dstversion>\n"
                + "                </provcov>\n" //
                + "            </providescoverage>\n" //
                + "        </specobject>\n" //
                + "    </specobjects>\n" //
                + "    <specobjects doctype=\"dsn\">\n" //
                + "        <specobject>\n" //
                + "            <id>exampleB</id>\n" //
                + "            <status>approved</status>\n" //
                + "            <version>1</version>\n"
                + "            <sourcefile>spec.md</sourcefile>\n"
                + "            <sourceline>2</sourceline>\n"
                + "            <description>Example requirement</description>\n"
                + "            <needscoverage>\n" //
                + "                <needsobj>utest</needsobj>\n"
                + "                <needsobj>impl</needsobj>\n" //
                + "            </needscoverage>\n" //
                + "        </specobject>\n" //
                + "    </specobjects>\n" //
                + "</specdocument>";

        final Trace trace = trace(content);
        final SpecificationItemId dsnId = SpecificationItemId.createId("dsn", "exampleB", 1);
        final SpecificationItemId implId = SpecificationItemId.createId("impl",
                "exampleB-3454416016", 0);

        final LinkedSpecificationItem dsn = getItemFromTraceForId(trace, dsnId);
        final LinkedSpecificationItem impl = getItemFromTraceForId(trace, implId);

        final SpecificationItem expectedImpl = item().id(implId)
                .location("source.java", 1).status(ItemStatus.APPROVED)
                .addCoveredId("dsn", "exampleB", 1).build();
        final SpecificationItem expectedDsn = item().id(dsnId)
                .location("spec.md", 2).description("Example requirement")
                .addNeedsArtifactType("utest").addNeedsArtifactType("impl").build();

        assertAll(() -> assertTraceSize(trace, 2), //
                () -> assertTraceContainsDefectIds(trace, dsnId), //
                () -> assertThat(dsn.getItem(), AutoMatcher.equalTo(expectedDsn)), //
                () -> assertThat(impl.getItem(), AutoMatcher.equalTo(expectedImpl)));
    }

    private Trace trace(final String content)
    {
        return trace(parse(content));
    }

    private Trace trace(final List<SpecificationItem> items)
    {
        final List<LinkedSpecificationItem> linkedItems = new Linker(items).link();
        return new Tracer().trace(linkedItems);
    }

    private List<SpecificationItem> parse(final String content)
    {
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        final InputFile input = StreamInput.forContent(Paths.get("dummy.xml"), content);
        final Importer importer = new SpecobjectImporterFactory().createImporter(input, listener);
        importer.runImport();
        return listener.build();
    }
}
