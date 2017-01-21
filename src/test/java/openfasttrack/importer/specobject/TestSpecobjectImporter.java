package openfasttrack.importer.specobject;

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

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.ImporterException;
import openfasttrack.importer.ImporterService;

/**
 * Test for {@link SpecobjectImporter}
 */
public class TestSpecobjectImporter
{
    private static final String TEST_FILE_PREFIX = "src/test/resources/specobject/";
    private SpecificationItem specItem1;
    private SpecificationItem specItem2;

    @Before
    public void setup()
    {
        this.specItem1 = new SpecificationItem.Builder() //
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
                        .build()) //
                .build();
        this.specItem2 = new SpecificationItem.Builder() //
                .id(new SpecificationItemId.Builder() //
                        .artifactType("doctype") //
                        .name("id2") //
                        .revision(42 + 1) //
                        .build()) //
                .comment("Comment2").rationale("Rationale2") //
                .description("Description2") //
                .build();
    }

    @Test
    public void testSingleSpecObject() throws FileNotFoundException
    {
        final List<SpecificationItem> result = runImporter("single-specobject.xml");
        assertThat(result, hasSize(1));
        assertThat(result, contains(this.specItem1));
    }

    @Test
    public void testTwoSpecObjects() throws FileNotFoundException
    {
        final List<SpecificationItem> result = runImporter("two-specobjects.xml");
        assertThat(result, hasSize(2));
        assertThat(result, contains(this.specItem1, this.specItem2));
    }

    @Test
    public void testEmptySpecObjects() throws FileNotFoundException
    {
        final List<SpecificationItem> result = runImporter("no-specobject.xml");
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

    private List<SpecificationItem> runImporter(final String fileName) throws FileNotFoundException
    {
        final Path file = Paths.get(TEST_FILE_PREFIX, fileName).toAbsolutePath();
        return new ImporterService().importFile(file);
    }
}
