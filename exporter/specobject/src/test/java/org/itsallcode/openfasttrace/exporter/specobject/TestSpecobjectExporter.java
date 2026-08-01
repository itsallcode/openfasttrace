package org.itsallcode.openfasttrace.exporter.specobject;

import static java.util.Arrays.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.itsallcode.openfasttrace.testutil.matcher.MultilineTextMatcher.matchesAllLines;
import static org.itsallcode.openfasttrace.testutil.core.ItemBuilderFactory.item;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import javax.xml.stream.*;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.testutil.xml.IndentingXMLStreamWriter;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class TestSpecobjectExporter
{
    // [itest->dsn~conversion.reqm2-export~1]
    @Test
    void testExportSimpleSpecObjectWithMandatoryElements()
    {
        final SpecificationItem item = item() //
                .id(SpecificationItemId.createId("foo", "bar", 1)) //
                .description("the description") //
                .build();
        final String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <specdocument>
                 <specobjects doctype="foo">
                  <specobject>
                   <id>bar</id>
                   <status>approved</status>
                   <version>1</version>
                   <description>the description</description>
                  </specobject>
                 </specobjects>
                </specdocument>
                """;
        final String actual = exportToString(item);
        assertThat(actual, matchesAllLines(expected));
    }

    @Test
    void testExportSpecObjectWithOptionalElements()
    {
        final SpecificationItem item = item() //
                .id(SpecificationItemId.createId("req", "me", 2)) //
                .title("My item title") //
                .status(ItemStatus.DRAFT) //
                .description("the description") //
                .rationale("the rationale") //
                .comment("the comment") //
                .addCoveredId("feat", "covered", 1) //
                .addDependOnId("req", "depend-on", 1) //
                .addNeedsArtifactType("impl") //
                .addTag("the tag") //
                .location("/the/file", 1024) //
                .build();
        final String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <specdocument>
                 <specobjects doctype="req">
                  <specobject>
                   <id>me</id>
                   <shortdesc>My item title</shortdesc>
                   <status>draft</status>
                   <version>2</version>
                   <sourcefile>/the/file</sourcefile>
                   <sourceline>1024</sourceline>
                   <description>the description</description>
                   <rationale>the rationale</rationale>
                   <comment>the comment</comment>
                   <tags>
                    <tag>the tag</tag>
                   </tags>
                   <needscoverage>
                    <needsobj>impl</needsobj>
                   </needscoverage>
                   <providescoverage>
                    <provcov>
                     <linksto>feat:covered</linksto>
                     <dstversion>1</dstversion>
                    </provcov>
                   </providescoverage>
                   <dependencies>
                    <dependson>req~depend-on~1</dependson>
                   </dependencies>
                  </specobject>
                 </specobjects>
                </specdocument>
                """;
        final String actual = exportToString(item);
        assertThat(actual, matchesAllLines(expected));
    }

    @Test
    void testExportTwoSpecObjects()
    {
        final SpecificationItem itemA = item() //
                .id(SpecificationItemId.createId("foo", "bar", 1)) //
                .status(ItemStatus.PROPOSED) //
                .description("the description") //
                .rationale("the rationale") //
                .comment("the comment") //
                .build();
        final SpecificationItem itemB = item() //
                .id(SpecificationItemId.createId("baz", "zoo", 2)) //
                .status(ItemStatus.REJECTED) //
                .description("another\ndescription") //
                .rationale("another\nrationale") //
                .comment("another\ncomment") //
                .build();
        final String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <specdocument>
                 <specobjects doctype="foo">
                  <specobject>
                   <id>bar</id>
                   <status>proposed</status>
                   <version>1</version>
                   <description>the description</description>
                   <rationale>the rationale</rationale>
                   <comment>the comment</comment>
                  </specobject>
                 </specobjects>
                 <specobjects doctype="baz">
                  <specobject>
                   <id>zoo</id>
                   <status>rejected</status>
                   <version>2</version>
                   <description>another
                description</description>
                   <rationale>another
                rationale</rationale>
                   <comment>another
                comment</comment>
                  </specobject>
                 </specobjects>
                </specdocument>
                """;
        final String actual = exportToString(itemA, itemB);
        assertThat(actual, matchesAllLines(expected));
    }

    @Test
    void testExportClosesWriters() throws XMLStreamException, IOException
    {
        final XMLStreamWriter xmlWriterMock = mock(XMLStreamWriter.class);
        final Writer writerMock = mock(Writer.class);
        new SpecobjectExporter(Stream.empty(), xmlWriterMock, writerMock, Newline.UNIX).runExport();

        final InOrder inOrder = inOrder(xmlWriterMock, writerMock);
        inOrder.verify(xmlWriterMock).close();
        inOrder.verify(writerMock).close();
        inOrder.verifyNoMoreInteractions();
    }

    private String exportToString(final SpecificationItem... items)
    {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream())
        {
            final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            final XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(stream,
                    StandardCharsets.UTF_8.name());
            new SpecobjectExporter(stream(items),
                    new IndentingXMLStreamWriter(xmlWriter, " ", Newline.UNIX.toString()),
                    new OutputStreamWriter(stream), Newline.UNIX).runExport();
            return stream.toString(StandardCharsets.UTF_8);
        }
        catch (IOException | XMLStreamException | FactoryConfigurationError e)
        {
            throw new AssertionError("Error exporting items", e);
        }
    }
}
