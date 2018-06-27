package org.itsallcode.openfasttrace.exporter.specobject;

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

import static java.util.Arrays.asList;
import static org.itsallcode.openfasttrace.matcher.MultilineTextMatcher.matchesAllLines;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.*;

import org.itsallcode.openfasttrace.core.*;
import org.itsallcode.openfasttrace.testutil.xml.IndentingXMLStreamWriter;
import org.junit.Test;

/**
 * Unit tests for {@link SpecobjectExporter}
 */
public class TestSpecobjectExporter
{
    // [itest->dsn~conversion.reqm2-export~1]
    @Test
    public void testExportSimpleSpecObjectWithMandatoryElements()
            throws IOException, XMLStreamException
    {
        final SpecificationItem item = new SpecificationItem.Builder() //
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
    public void testExportSpecObjectWithOptionalElements() throws IOException, XMLStreamException
    {
        final SpecificationItem item = new SpecificationItem.Builder() //
                .id(SpecificationItemId.createId("req", "me", 2)) //
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
                + "     <linksto>covered</linksto>\n" //
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
    public void testExportTwoSpecObjects() throws IOException, XMLStreamException
    {
        final SpecificationItem itemA = new SpecificationItem.Builder() //
                .id(SpecificationItemId.createId("foo", "bar", 1)) //
                .status(ItemStatus.PROPOSED) //
                .description("the description") //
                .rationale("the rationale") //
                .comment("the comment") //
                .build();
        final SpecificationItem itemB = new SpecificationItem.Builder() //
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

    private String exportToString(final SpecificationItem... items)
    {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream())
        {
            final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            final XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(stream,
                    StandardCharsets.UTF_8.name());
            new SpecobjectExporter(asList(items).stream(),
                    new IndentingXMLStreamWriter(xmlWriter, " ", Newline.UNIX.toString()),
                    Newline.UNIX).runExport();
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        }
        catch (IOException | XMLStreamException | FactoryConfigurationError e)
        {
            throw new AssertionError("Error exporting items", e);
        }
    }
}
