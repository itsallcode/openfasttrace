package openfasttrack.importer.specobject;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.stream.XMLInputFactory;

import org.junit.Before;
import org.junit.Test;

import com.github.hamstercommunity.matcher.auto.AutoMatcher;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItem.Builder;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.ImporterException;
import openfasttrack.importer.ImporterService;

/**
 * Test for {@link SpecobjectImporter}
 */
public class TestSpecobjectImporter
{
    private static final Path TEST_FILE_PREFIX = Paths.get("src/test/resources/specobject/")
            .toAbsolutePath();
    private static final Path SINGLE_SPECOBJECT_FILE = TEST_FILE_PREFIX
            .resolve("single-specobject.xml");
    private static final Path TWO_SPECOBJECT_FILE = TEST_FILE_PREFIX.resolve("two-specobjects.xml");
    private static final Path NO_SPECOBJECT_FILE = TEST_FILE_PREFIX.resolve("no-specobject.xml");

    private Builder specItem1Builder;
    private Builder specItem2Builder;

    @Before
    public void setup()
    {
        this.specItem1Builder = new SpecificationItem.Builder() //
                .id(new SpecificationItemId.Builder() //
                        .artifactType("doctype") //
                        .name("id") //
                        .revision(42) //
                        .build()) //
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
                .comment("Comment2").rationale("Rationale2") //
                .description("Description2");
    }

    @Test
    public void testSingleSpecObject() throws FileNotFoundException
    {
        final List<SpecificationItem> result = runImporter(SINGLE_SPECOBJECT_FILE);
        assertThat(result, hasSize(1));
        final SpecificationItem item1 = this.specItem1Builder
                .location(SINGLE_SPECOBJECT_FILE.toString(), 5) //
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
                .location(TWO_SPECOBJECT_FILE.toString(), 5) //
                .build();
        final SpecificationItem item2 = this.specItem2Builder
                .location(TWO_SPECOBJECT_FILE.toString(), 25) //
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
        new SpecobjectImporter("testfilename", new StringReader(content),
                XMLInputFactory.newFactory(), null).runImport();
    }

    private List<SpecificationItem> runImporter(final Path path) throws FileNotFoundException
    {
        return new ImporterService().importFile(path);
    }
}
