package org.itsallcode.openfasttrace.importer.tag;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.*;
import org.itsallcode.openfasttrace.importer.input.InputFile;

/**
 * {@link Importer} for tags in source code files.
 */
// [impl->dsn~import.full-coverage-tag~1]
class TagImporter implements Importer
{
    private static final Logger LOG = Logger.getLogger(TagImporter.class.getName());
    private static final Pattern COVERED_ID_PATTERN = SpecificationItemId.ID_PATTERN;
    private static final String COVERING_ARTIFACT_TYPE_PATTERN = "\\p{Alpha}+";
    private static final String TAG_PREFIX_PATTERN = "\\[";
    private static final String TAG_SUFFIX_PATTERN = "\\]";

    private final InputFile file;
    private final ImportEventListener listener;
    private final Pattern tagPattern;

    public TagImporter(final InputFile file, final ImportEventListener listener)
    {
        this.file = file;
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
        try (BufferedReader reader = this.file.createReader())
        {
            while ((line = reader.readLine()) != null)
            {
                ++lineNumber;
                processLine(lineNumber, line);
            }
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error reading file " + this.file + ":" + lineNumber, e);
        }
    }

    private void processLine(final int lineNumber, final String line)
    {
        final Matcher matcher = this.tagPattern.matcher(line);
        int counter = 0;
        while (matcher.find())
        {
            this.listener.beginSpecificationItem();
            this.listener.setLocation(this.file.getPath().toString(), lineNumber);
            final SpecificationItemId coveredId = SpecificationItemId.parseId(matcher.group(2));
            final String generatedName = generateName(coveredId, lineNumber, counter);
            final SpecificationItemId generatedId = SpecificationItemId.createId(matcher.group(1),
                    generatedName, 0);

            LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + generatedId
                    + "' covering id '" + coveredId + "'");
            this.listener.setId(generatedId);
            this.listener.addCoveredId(coveredId);
            this.listener.endSpecificationItem();
            counter++;
        }
    }

    private String generateName(final SpecificationItemId coveredId, final int lineNumber,
            final int counter)
    {
        final String uniqueName = this.file.getPath() + lineNumber + counter + coveredId.toString();
        final String checksum = Long.toString(ChecksumCalculator.calculateCrc32(uniqueName));
        return coveredId.getName() + "-" + checksum;
    }
}
