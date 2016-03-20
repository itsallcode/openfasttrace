package openfasttrack.exporter.specobject;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
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
import java.util.Collection;
import java.util.logging.Logger;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.hamcrest.Matchers;
import org.junit.Test;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.SpecificationMapListBuilder;
import openfasttrack.importer.specobject.SpecobjectImporterFactory;
import openfasttrack.matcher.SpecificationItemMatcher;
import openfasttrack.testutil.xml.IndentingXMLStreamWriter;

public class TestSpecobjectExporter
{
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
        assertExportContent(Paths.get("src/test/resources/specobject/single-specobject.xml"),
                new LinkedSpecificationItem(item));
    }

    private void assertExportContent(final Path expectedContentFile,
            final LinkedSpecificationItem... items)
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
            final LinkedSpecificationItem... expectedLinkedItems)
    {
        final String actualContent = export(expectedLinkedItems);
        LOG.info(() -> "Actual  : " + actualContent);
        LOG.info(() -> "Expected: " + expectedContent);
        assertEquals(expectedContent, actualContent);
        assertThat(actualContent, equalTo(expectedContent));
        final Collection<SpecificationItem> parseSpecobjects = parseSpecobjectXml(actualContent);

        if (expectedLinkedItems.length == 0)
        {
            assertThat(parseSpecobjects, emptyIterable());
            return;
        }

        if (expectedLinkedItems.length == 1)
        {
            assertThat(parseSpecobjects.iterator().next(),
                    SpecificationItemMatcher.equalTo(expectedLinkedItems[0].getItem()));
            return;
        }

        final Collection<SpecificationItem> expectedItems = Arrays.stream(expectedLinkedItems)
                .map(i -> i.getItem()).collect(toList());
        assertThat(parseSpecobjects,
                Matchers.containsInAnyOrder(SpecificationItemMatcher.equalTo(expectedItems)));
    }

    private Collection<SpecificationItem> parseSpecobjectXml(final String specobjectXml)
    {
        final SpecificationMapListBuilder builder = new SpecificationMapListBuilder();
        new SpecobjectImporterFactory().createImporter(new StringReader(specobjectXml), builder)
                .runImport();
        return builder.build().values();
    }

    private String export(final LinkedSpecificationItem... items)
    {
        try
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            final XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(stream);
            new SpecobjectExporter(asList(items),
                    new IndentingXMLStreamWriter(xmlWriter, "\t", "\n")).runExport();
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        }
        catch (XMLStreamException | FactoryConfigurationError e)
        {
            throw new AssertionError("Error exporting items", e);
        }
    }
}
