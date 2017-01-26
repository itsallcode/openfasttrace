package openfasttrack.importer.tag;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;

/**
 * {@link Importer} for tags in source code files.
 */
class TagImporter implements Importer
{
    private static final Logger LOG = Logger.getLogger(TagImporter.class.getName());
    private static final String COVERED_ID_PATTERN = "\\p{Alpha}+~\\p{Alpha}\\w*(?:\\.\\p{Alpha}\\w*)*~\\d+";
    private static final String COVERING_ARTIFACT_TYPE_PATTERN = "\\p{Alpha}+";
    private static final String TAG_PREFIX_PATTERN = "\\[";
    private static final String TAG_SUFFIX_PATTERN = "\\]";

    private final BufferedReader reader;
    private final String fileName;
    private final ImportEventListener listener;
    private final Pattern tagPattern;

    public TagImporter(final String fileName, final Reader reader,
            final ImportEventListener listener)
    {
        this.reader = new BufferedReader(reader);
        this.fileName = fileName;
        this.listener = listener;
        this.tagPattern = Pattern.compile(TAG_PREFIX_PATTERN //
                + "(" + COVERING_ARTIFACT_TYPE_PATTERN + ")" //
                + "->" //
                + "(" + COVERED_ID_PATTERN + ")" //
                + TAG_SUFFIX_PATTERN);
    }

    @Override
    public void runImport()
    {
        String line;
        int lineNumber = 0;
        try
        {
            while ((line = this.reader.readLine()) != null)
            {
                ++lineNumber;
                processLine(lineNumber, line);
            }
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error reading file " + this.fileName + ":" + lineNumber,
                    e);
        }
    }

    private void processLine(final int lineNumber, final String line)
    {
        final Matcher matcher = this.tagPattern.matcher(line);
        int counter = 0;
        while (matcher.find())
        {
            this.listener.beginSpecificationItem();
            final String generatedName = this.fileName + ":" + lineNumber + "-" + counter;
            final SpecificationItemId generatedId = SpecificationItemId.createId(matcher.group(1),
                    generatedName, 0);
            final SpecificationItemId coveredId = SpecificationItemId.parseId(matcher.group(2));

            LOG.finest(() -> "File " + this.fileName + ":" + lineNumber + ": found '" + generatedId
                    + "' covering id '" + coveredId + "'");
            this.listener.setId(generatedId);
            this.listener.addCoveredId(coveredId);
            this.listener.endSpecificationItem();
            counter++;
        }
    }
}
