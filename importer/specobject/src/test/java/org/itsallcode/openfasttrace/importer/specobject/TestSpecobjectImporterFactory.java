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

import static java.util.Arrays.asList;

import java.util.List;

import org.itsallcode.openfasttrace.testutil.importer.ImporterFactoryTestBase;

/**
 * Tests for {@link SpecobjectImporterFactory}
 */
class TestSpecobjectImporterFactory
        extends ImporterFactoryTestBase<SpecobjectImporterFactory>
{

    @Override
    protected SpecobjectImporterFactory createFactory()
    {
        return new SpecobjectImporterFactory();
    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("file.xml", "file.XML", "FILE.xml", "FILE.XML", "file.md.xml");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("file.md", "file.xm", "file.ml", "file.1xml", "file.xml1", "file.xml.md",
                "file_xml", "filexml");
    }
}
