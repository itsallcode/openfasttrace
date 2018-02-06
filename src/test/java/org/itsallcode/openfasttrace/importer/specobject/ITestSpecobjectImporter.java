package org.itsallcode.openfasttrace.importer.specobject;

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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.core.ItemStatus;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItem.Builder;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.core.xml.SaxParserConfigurator;
import org.itsallcode.openfasttrace.importer.ImporterException;
import org.itsallcode.openfasttrace.importer.ImporterService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xml.sax.SAXException;

import com.github.hamstercommunity.matcher.auto.AutoMatcher;

/**
 * Test for {@link SpecobjectImporter}
 */
public class ITestSpecobjectImporter
{
    private static final Path TEST_FILE_PREFIX = Paths.get("src/test/resources/specobject/")
            .toAbsolutePath();
    private static final Path SINGLE_SPECOBJECT_FILE = TEST_FILE_PREFIX
            .resolve("single-specobject.xml");
    private static final Path TWO_SPECOBJECT_FILE = TEST_FILE_PREFIX.resolve("two-specobjects.xml");
    private static final Path NO_SPECOBJECT_FILE = TEST_FILE_PREFIX.resolve("no-specobject.xml");

    @Mock
    private SAXParserFactory parserFactoryMock;

    private Builder specItem1Builder;
    private Builder specItem2Builder;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        this.specItem1Builder = new SpecificationItem.Builder() //
                .id(new SpecificationItemId.Builder() //
                        .artifactType("doctype") //
                        .name("id") //
                        .revision(42) //
                        .build()) //
                .title("") //
                .comment("Comment").rationale("Rationale") //
                .description("Description") //
                .addNeedsArtifactType("code").addNeedsArtifactType("test") //
                .addCoveredId(new SpecificationItemId.Builder() //
                        .artifactType(null) //
                        .name("provid") //
                        .revision(43) //
                        .build()) //
                .addDependOnId(new SpecificationItemId.Builder() //
                        .artifactType("dependsOnDocType") //
                        .name("dependsOnName") //
                        .revision(44) //
                        .build()); //
        this.specItem2Builder = new SpecificationItem.Builder() //
                .id(new SpecificationItemId.Builder() //
                        .artifactType("doctype") //
                        .name("id2") //
                        .revision(42 + 1) //
                        .build()) //
                .title("ShortDesc2") //
                .status(ItemStatus.DRAFT) //
                .comment("Comment2") //
                .rationale("Rationale2") //
                .description("Description2") //
                .addTag("first tag") //
                .addTag("second tag");
    }

    @Test
    public void testSingleSpecObject() throws FileNotFoundException
    {
        final List<SpecificationItem> result = runImporter(SINGLE_SPECOBJECT_FILE);
        assertThat(result, hasSize(1));
        final SpecificationItem item1 = this.specItem1Builder
                .location(SINGLE_SPECOBJECT_FILE.toString(), 4) //
                .build();
        assertThat(result, AutoMatcher.contains(item1));
        assertThat(result, contains(item1));
    }

    @Test
    public void testTwoSpecObjects() throws FileNotFoundException
    {
        final List<SpecificationItem> result = runImporter(TWO_SPECOBJECT_FILE);
        assertThat(result, hasSize(2));

        final SpecificationItem item1 = this.specItem1Builder
                .location(TWO_SPECOBJECT_FILE.toString(), 4) //
                .build();
        final SpecificationItem item2 = this.specItem2Builder
                .location(TWO_SPECOBJECT_FILE.toString(), 24) //
                .build();
        assertThat(result, AutoMatcher.contains(item1, item2));
        assertThat(result, contains(item1, item2));
    }

    @Test
    public void testEmptySpecObjects() throws FileNotFoundException
    {
        final List<SpecificationItem> result = runImporter(NO_SPECOBJECT_FILE);
        assertThat(result, hasSize(0));
    }

    @Test(expected = ImporterException.class)
    public void testSpecObjectsWithoutDoctype() throws FileNotFoundException
    {
        final String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
                "<specdocument>\n" + //
                "    <specobjects>\n" + //
                "    </specobjects>\n" + //
                "</specdocument>";
        final SAXParserFactory parserFactory = SaxParserConfigurator.createSaxParserFactory();
        new SpecobjectImporter("testfilename", new StringReader(content), parserFactory, null)
                .runImport();
    }

    @Test(expected = ImporterException.class)
    public void testSaxExceptionWhenCreatingReaderThrowsImporterException()
            throws FileNotFoundException, ParserConfigurationException, SAXException
    {
        when(this.parserFactoryMock.newSAXParser()).thenThrow(new SAXException("expected"));
        new SpecobjectImporter("testfilename", new StringReader(""), this.parserFactoryMock, null)
                .runImport();
    }

    private List<SpecificationItem> runImporter(final Path path) throws FileNotFoundException
    {
        return new ImporterService().importFile(path);
    }
}
