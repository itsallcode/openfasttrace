package openfasttrack.exporter.specobject;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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
import static openfasttrack.matcher.MultilineTextMatcher.matchesAllLines;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.*;

import org.junit.Test;

import openfasttrack.core.Newline;
import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.SpecificationListBuilder;
import openfasttrack.importer.specobject.SpecobjectImporterFactory;
import openfasttrack.matcher.SpecificationItemMatcher;
import openfasttrack.testutil.xml.IndentingXMLStreamWriter;

/**
 * Unit tests for {@link SpecobjectExporter}
 */
public class TestSpecobjectExporter
{
    private static final String SPECOBJECT_RESOURCES = "src/test/resources/specobject/";
    private final static Logger LOG = Logger.getLogger(TestSpecobjectExporter.class.getName());

    @Test
    public void testEmptyItemList()
    {
        assertExportContent(Paths.get("src/test/resources/specobject/no-specobject.xml"));
    }

    @Test
    public void testSingleItem()
    {
        final SpecificationItem item = new SpecificationItem.Builder()
                .id(SpecificationItemId.createId("doctype", "id", 42)) //
                .description("Description") //
                .rationale("Rationale") //
                .comment("Comment") //
                .addNeedsArtifactType("code").addNeedsArtifactType("test") //
                .addCoveredId(SpecificationItemId.createId(null, "provid", 43)) //
                .addDependOnId(SpecificationItemId.parseId("dependsOnDocType~dependsOnName~44"))
                .build();
        assertExportContent(Paths.get("src/test/resources/specobject/single-specobject.xml"), item);
    }

    @Test
    public void testTwoItems()
    {
        final SpecificationItem item1 = new SpecificationItem.Builder()
                .id(SpecificationItemId.createId("doctype", "id", 42)) //
                .description("Description") //
                .rationale("Rationale") //
                .comment("Comment") //
                .addNeedsArtifactType("code").addNeedsArtifactType("test") //
                .addCoveredId(SpecificationItemId.createId(null, "provid", 43)) //
                .addDependOnId(SpecificationItemId.parseId("dependsOnDocType~dependsOnName~44"))
                .build();
        final SpecificationItem item2 = new SpecificationItem.Builder()
                .id(SpecificationItemId.createId("doctype", "id2", 43)) //
                .description("Description2") //
                .rationale("Rationale2") //
                .comment("Comment2") //
                .build();
        assertExportContent(getSpecobjecResourcePath("two-specobjects.xml"), item1, item2);
    }

    private Path getSpecobjecResourcePath(final String resource)
    {
        return Paths.get(SPECOBJECT_RESOURCES + resource);
    }

    @Test
    public void testTwoItemsDifferentDoctype()
    {
        final SpecificationItem item1 = new SpecificationItem.Builder()
                .id(SpecificationItemId.createId("doctype1", "id", 42)) //
                .description("Description") //
                .rationale("Rationale") //
                .comment("Comment") //
                .addNeedsArtifactType("code").addNeedsArtifactType("test") //
                .addCoveredId(SpecificationItemId.createId(null, "provid", 43)) //
                .addDependOnId(SpecificationItemId.parseId("dependsOnDocType~dependsOnName~44"))
                .build();
        final SpecificationItem item2 = new SpecificationItem.Builder()
                .id(SpecificationItemId.createId("doctype2", "id2", 43)) //
                .description("Description2") //
                .rationale("Rationale2") //
                .comment("Comment2") //
                .build();
        assertExportContent(getSpecobjecResourcePath("two-specobjects-different-doctype.xml"),
                item1, item2);
    }

    @Test
    public void testNewlineUnification()
    {
        final SpecificationItem item1 = new SpecificationItem.Builder()
                .id(SpecificationItemId.createId("foo", "single-newlines", 1)) //
                .description("This contains\na unix-style newline.") //
                .rationale("This contains\ra MacOS 9 newline.") //
                .comment("This contains\r\na DOS/Windows newline.") //
                .build();
        final SpecificationItem item2 = new SpecificationItem.Builder()
                .id(SpecificationItemId.createId("bar", "mixed-newlines", 2)) //
                .description("This contains\na unix-style and\ran old-mac-style newline.") //
                .rationale("This contains\na unix-style and\r\na DOS/Windows newline.") //
                .comment("This\r\ncontains\ra mixture\nof all three.") //
                .build();
        assertThat(export(item1, item2), matchesAllLines(
                readFile(getSpecobjecResourcePath("two-specobjects-unified-newlines.xml"))));
    }

    private void assertExportContent(final Path expectedContentFile,
            final SpecificationItem... items)
    {
        final String expectedContent = readFile(expectedContentFile);
        assertExportContent(expectedContent, items);
    }

    private String readFile(final Path path)
    {
        try
        {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        }
        catch (final IOException e)
        {
            throw new AssertionError("Error reading file " + path, e);
        }
    }

    private void assertExportContent(final String expectedContent,
            final SpecificationItem... expectedItems)
    {
        final String actualContent = export(expectedItems);
        LOG.finest(() -> "Actual  : " + actualContent);
        LOG.finest(() -> "Expected: " + expectedContent);
        assertEquals(expectedContent, actualContent);
        assertThat(actualContent, equalTo(expectedContent));
        final List<SpecificationItem> actualParsedSpecobjects = parseSpecobjectXml(actualContent);

        assertThat(actualParsedSpecobjects, hasSize(expectedItems.length));

        assertThat(actualParsedSpecobjects,
                SpecificationItemMatcher.equalToAnyOrder(Arrays.asList(expectedItems)));
    }

    private List<SpecificationItem> parseSpecobjectXml(final String specobjectXml)
    {
        final SpecificationListBuilder builder = new SpecificationListBuilder();
        new SpecobjectImporterFactory()
                .createImporter("testfilename", new StringReader(specobjectXml), builder)
                .runImport();
        return builder.build();
    }

    private String export(final SpecificationItem... items)
    {
        try
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            final XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(stream);
            new SpecobjectExporter(asList(items).stream(),
                    new IndentingXMLStreamWriter(xmlWriter, "\t", Newline.UNIX.toString()),
                    Newline.UNIX).runExport();
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        }
        catch (XMLStreamException | FactoryConfigurationError e)
        {
            throw new AssertionError("Error exporting items", e);
        }
    }
}
