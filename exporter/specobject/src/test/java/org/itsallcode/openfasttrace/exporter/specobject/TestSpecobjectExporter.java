package org.itsallcode.openfasttrace.exporter.specobject;

import static java.util.Arrays.asList;
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
    void testExportSimpleSpecObjectWithMandatoryElements() throws IOException, XMLStreamException
    {
        final SpecificationItem item = item() //
                .id(SpecificationItemId.createId("foo", "bar", 1)) //
                .description("the description") //
                .build();
        final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<specdocument>\n" //
                + " <specobjects doctype=\"foo\">\n" //
                + "  <specobject>\n" //
                + "   <id>bar</id>\n" //
                + "   <status>approved</status>\n" //
                + "   <version>1</version>\n" //
                + "   <description>the description</description>\n" //
                + "  </specobject>\n" //
                + " </specobjects>\n" //
                + "</specdocument>\n";
        final String actual = exportToString(item);
        assertThat(actual, matchesAllLines(expected));
    }

    @Test
    void testExportSpecObjectWithOptionalElements() throws IOException, XMLStreamException
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
        final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<specdocument>\n" //
                + " <specobjects doctype=\"req\">\n" //
                + "  <specobject>\n" //
                + "   <id>me</id>\n" //
                + "   <shortdesc>My item title</shortdesc>\n" //
                + "   <status>draft</status>\n" //
                + "   <version>2</version>\n" //
                + "   <sourcefile>/the/file</sourcefile>\n" //
                + "   <sourceline>1024</sourceline>\n" // "
                + "   <description>the description</description>\n" //
                + "   <rationale>the rationale</rationale>\n" //
                + "   <comment>the comment</comment>\n" //
                + "   <tags>\n" //
                + "    <tag>the tag</tag>\n" //
                + "   </tags>\n" //
                + "   <needscoverage>\n" //
                + "    <needsobj>impl</needsobj>\n" //
                + "   </needscoverage>\n" //
                + "   <providescoverage>\n" //
                + "    <provcov>\n" //
                + "     <linksto>feat:covered</linksto>\n" //
                + "     <dstversion>1</dstversion>\n" //
                + "    </provcov>\n" //
                + "   </providescoverage>\n" //
                + "   <dependencies>\n" //
                + "    <dependson>req~depend-on~1</dependson>\n" //
                + "   </dependencies>\n" //
                + "  </specobject>\n" //
                + " </specobjects>\n" //
                + "</specdocument>\n";
        final String actual = exportToString(item);
        assertThat(actual, matchesAllLines(expected));
    }

    @Test
    void testExportTwoSpecObjects() throws IOException, XMLStreamException
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
        final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<specdocument>\n" //
                + " <specobjects doctype=\"foo\">\n" //
                + "  <specobject>\n" //
                + "   <id>bar</id>\n" //
                + "   <status>proposed</status>\n" //
                + "   <version>1</version>\n" //
                + "   <description>the description</description>\n" //
                + "   <rationale>the rationale</rationale>\n" //
                + "   <comment>the comment</comment>\n" //
                + "  </specobject>\n" //
                + " </specobjects>\n" //
                + " <specobjects doctype=\"baz\">\n" //
                + "  <specobject>\n" //
                + "   <id>zoo</id>\n" //
                + "   <status>rejected</status>\n" //
                + "   <version>2</version>\n" //
                + "   <description>another\ndescription</description>\n" //
                + "   <rationale>another\nrationale</rationale>\n" //
                + "   <comment>another\ncomment</comment>\n" //
                + "  </specobject>\n" //
                + " </specobjects>\n" //
                + "</specdocument>\n";
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
            new SpecobjectExporter(asList(items).stream(),
                    new IndentingXMLStreamWriter(xmlWriter, " ", Newline.UNIX.toString()),
                    new OutputStreamWriter(stream), Newline.UNIX).runExport();
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        }
        catch (IOException | XMLStreamException | FactoryConfigurationError e)
        {
            throw new AssertionError("Error exporting items", e);
        }
    }
}
