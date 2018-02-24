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

import org.itsallcode.openfasttrace.Converter;
import org.itsallcode.openfasttrace.exporter.ExporterConstants;
import org.junit.Before;
import org.junit.Test;

public class ITestConverter extends AbstractOftModeTest
{
    private static final String REQM2_PREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><specdocument>";

    private Converter converter;

    @Before
    public void setUp() throws UnsupportedEncodingException
    {
        perpareOutput();
        this.converter = new ConvertMode();
    }

    @Test
    public void testConvertToSpecobjectFile() throws IOException
    {
        this.converter.addInputs(this.docDir);
        this.converter.convertToFileInFormat(this.outputFile,
                ExporterConstants.DEFAULT_OUTPUT_FORMAT);
        assertStandardFileExportResult();
    }

    private void assertStandardFileExportResult() throws IOException
    {
        assertOutputFileExists(true);
        assertOutputFileContentStartsWith(REQM2_PREAMBLE + "<specobjects doctype=\"dsn\">");
    }
}
