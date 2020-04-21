package org.itsallcode.openfasttrace.mode;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.core.Oft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

@ExtendWith(TempDirectory.class)
public class ITestOftAsConverter extends AbstractOftTest
{
    private static final String SPECOBJECT_PREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<specdocument>";
    private Oft oft;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir) throws UnsupportedEncodingException
    {
        perpareOutput(tempDir);
        this.oft = Oft.create();
    }

    @Test
    void testConvertToSpecobjectFile() throws IOException
    {
        final ImportSettings settings = ImportSettings.builder().addInputs(this.docDir).build();
        final List<SpecificationItem> items = this.oft.importItems(settings);
        this.oft.exportToPath(items, this.outputFile);
        assertStandardFileExportResult();
    }

    private void assertStandardFileExportResult() throws IOException
    {
        assertOutputFileExists(true);
        assertOutputFileContentStartsWith(SPECOBJECT_PREAMBLE + "\n  <specobjects doctype=\"dsn\">");
    }
}