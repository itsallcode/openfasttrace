package org.itsallcode.openfasttrace.importer.rif;

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
import static java.util.Arrays.asList;

import java.util.List;

import org.itsallcode.openfasttrace.importer.ImporterFactoryTestBase;

public class TestRifImporterFactory extends ImporterFactoryTestBase<RifImporterFactory>
{

    @Override
    protected RifImporterFactory createFactory()
    {
        return new RifImporterFactory();

    }

    @Override
    protected List<String> getSupportedFilenames()
    {
        return asList("file.rif", "file.RIF", "FILE.rif", "FILE.RIF", "file.md.rif");
    }

    @Override
    protected List<String> getUnsupportedFilenames()
    {
        return asList("file.md", "file.ri", "file.if", "file.1rif", "file.rif1", "file.rif.md",
                "file_rif", "filerif");
    }

}
