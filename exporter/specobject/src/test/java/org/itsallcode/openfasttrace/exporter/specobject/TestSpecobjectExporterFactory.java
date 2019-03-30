package org.itsallcode.openfasttrace.exporter.specobject;

/*-
 * #%L
 * OpenFastTrace Specobject Exporter
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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
import static org.hamcrest.Matchers.equalTo;

import java.io.StringWriter;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.exporter.Exporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestSpecobjectExporterFactory
{
    private SpecobjectExporterFactory factory;

    @BeforeEach
    void setUp()
    {
        factory = new SpecobjectExporterFactory();
    }

    @Test
    void testCreateExporterWriterStreamOfSpecificationItemNewline()
    {
        final StringWriter writer = new StringWriter();
        final SpecificationItem item = SpecificationItem.builder().id("art", "name", 42).build();

        final Exporter exporter = factory.createExporter(writer, Stream.of(item), Newline.UNIX);
        exporter.runExport();

        assertThat(writer.getBuffer().toString(), equalTo(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><specdocument><specobjects doctype=\"art\"><specobject><id>name</id><status>approved</status><version>42</version></specobject></specobjects></specdocument>"));
    }
}
