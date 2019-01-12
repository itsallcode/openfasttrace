package org.itsallcode.openfasttrace.importer.pdf;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
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
import org.itsallcode.openfasttrace.importer.*;
import org.itsallcode.openfasttrace.importer.input.InputFile;

/**
 * This {@link Importer} supports reading PDF files
 */
public class PdfFileImporter implements Importer
{
    final static String DEFAULT_SPECITEM_PATTERN = "\\[(\\w+-\\d+)\\](.*)";
    final static String DEFAULT_ARTIFACT_TYPE = "req";
    final static int DEFAULT_SPECITEM_REVISION = 1;

    private final InputFile file;
    private final ImportEventListener listener;
    protected final Pattern specItemPattern;

    PdfFileImporter(final ImporterService importerService, final InputFile file,
            final ImportEventListener listener)
    {
        this.file = file;
        this.listener = listener;
        this.specItemPattern = Pattern.compile(DEFAULT_SPECITEM_PATTERN);
    }

    @Override
    public void runImport()
    {
        if (!this.file.isRealFile())
        {
            throw new UnsupportedOperationException(
                    "Importing a PDF file from a stream is not supported");
        }
        try
        {
            final PDFTextStripper textStripper = new PDFTextStripper();
            final PDDocument pdDoc = PDDocument.load(new File(this.file.getPath()));

            final String pdfAsText = textStripper.getText(pdDoc);
            final BufferedReader reader = new BufferedReader(new StringReader(pdfAsText));

            String line = reader.readLine();
            while (line != null)
            {
                createSpecificationItemFromLine(line);
                line = reader.readLine();
            }

        }
        catch (final IOException e)
        {
            throw new ImporterException("Error reading \"" + this.file + "\"", e);
        }
    }

    protected void createSpecificationItemFromLine(final String line)
    {
        final Matcher matcher = this.specItemPattern.matcher(line);
        if (matcher.find())
        {
            final String specItemName = matcher.group(1);
            final String specItemTitle = matcher.group(2);

            this.listener.beginSpecificationItem();

            final SpecificationItemId specItemId = SpecificationItemId
                    .createId(DEFAULT_ARTIFACT_TYPE, specItemName, DEFAULT_SPECITEM_REVISION);

            this.listener.setId(specItemId);

            this.listener.setTitle(specItemTitle.trim());
            this.listener.appendDescription(specItemTitle.trim());

            this.listener.endSpecificationItem();
        }
    }

}
