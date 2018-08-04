package org.itsallcode.openfasttrace.importer.specobject;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.openfasttrace.core.*;
import org.itsallcode.openfasttrace.importer.Importer;
import org.itsallcode.openfasttrace.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.input.StreamInput;
import org.junit.Test;

import com.github.hamstercommunity.matcher.auto.AutoMatcher;

public class TestSpecobjectImportExport
{
    @Test
    public void testTraceContent()
    {
        final String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<specdocument>\n"
                + "    <specobjects doctype=\"impl\">\n" + "        <specobject>\n"
                + "            <id>exampleB-3454416016</id>\n"
                + "            <status>approved</status>\n" + "            <version>0</version>\n"
                + "            <sourcefile>source.java</sourcefile>\n"
                + "            <sourceline>1</sourceline>\n" + "            <providescoverage>\n"
                + "                <provcov>\n"
                + "                    <linksto>dsn:exampleB</linksto>\n"
                + "                    <dstversion>1</dstversion>\n"
                + "                </provcov>\n" + "            </providescoverage>\n"
                + "        </specobject>\n" + "    </specobjects>\n" + "\n"
                + "    <specobjects doctype=\"dsn\">\n" + "        <specobject>\n"
                + "            <id>exampleB</id>\n" + "            <status>approved</status>\n"
                + "            <version>1</version>\n"
                + "            <sourcefile>spec.md</sourcefile>\n"
                + "            <sourceline>2</sourceline>\n"
                + "            <description>Example requirement</description>\n"
                + "            <needscoverage>\n" + "                <needsobj>utest</needsobj>\n"
                + "                <needsobj>impl</needsobj>\n" + "            </needscoverage>\n"
                + "        </specobject>\n" + "    </specobjects>\n" + "</specdocument>";

        final Trace trace = trace(content);
        assertThat(trace.getItems(), hasSize(2));
        assertThat(trace.getDefectItems(), hasSize(2));

        final LinkedSpecificationItem tag = trace.getItems().get(0);
        final LinkedSpecificationItem req = trace.getItems().get(1);

        final SpecificationItem expectedTag = new SpecificationItem.Builder()
                .id("impl", "exampleB-3454416016", 0).location("source.java", 1)
                .status(ItemStatus.APPROVED).addCoveredId("dsn", "exampleB", 1).build();
        final SpecificationItem expectedReq = new SpecificationItem.Builder()
                .id("dsn", "exampleB", 1).location("spec.md", 2).description("Example requirement")
                .addNeedsArtifactType("utest").addNeedsArtifactType("impl").build();

        assertThat(tag.getItem(), AutoMatcher.equalTo(expectedTag));
        assertThat(req.getItem(), AutoMatcher.equalTo(expectedReq));
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
