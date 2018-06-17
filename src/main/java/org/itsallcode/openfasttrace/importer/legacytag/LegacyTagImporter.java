package org.itsallcode.openfasttrace.importer.legacytag;

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

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.*;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.legacytag.config.PathConfig;

// [impl->dsn~import.short-coverage-tag~1]
class LegacyTagImporter implements Importer
{
    private static final Logger LOG = Logger.getLogger(LegacyTagImporter.class.getName());

    private static final String TAG_PREFIX = "\\[\\[";
    private static final String TAG_SUFFIX = "\\]\\]";
    private final PathConfig pathConfig;
    private final ImportEventListener listener;
    private final InputFile file;
    private final Pattern tagPattern;

    LegacyTagImporter(final PathConfig pathConfig, final InputFile file,
            final ImportEventListener listener)
    {
        this.pathConfig = pathConfig;
        this.file = file;
        this.listener = listener;
        this.tagPattern = Pattern.compile(TAG_PREFIX //
                + SpecificationItemId.ITEM_NAME_PATTERN + ":"
                + SpecificationItemId.ITEM_REVISION_PATTERN + TAG_SUFFIX);
    }

    @Override
    public void runImport()
    {
        final LineReader reader = LineReader.create(this.file);
        reader.readLines(this::processLine);
    }

    private void processLine(final int lineNumber, final String line)
    {
        final Matcher matcher = this.tagPattern.matcher(line);
        int counter = 0;
        while (matcher.find())
        {
            final String coveredItemName = matcher.group(1);
            final String coveredItemRevision = matcher.group(2);
            final SpecificationItemId coveredId = createCoveredItem(coveredItemName,
                    coveredItemRevision);

            final String generatedName = generateName(coveredId, lineNumber, counter);
            final SpecificationItemId tagItemId = SpecificationItemId
                    .createId(this.pathConfig.getTagArtifactType(), generatedName);

            LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + tagItemId
                    + "' covering id '" + coveredId + "'");

            addItem(lineNumber, coveredId, tagItemId);
            counter++;
        }
    }

    private void addItem(final int lineNumber, final SpecificationItemId coveredId,
            final SpecificationItemId tagItemId)
    {
        this.listener.beginSpecificationItem();
        this.listener.setLocation(this.file.toString(), lineNumber);
        this.listener.setId(tagItemId);
        this.listener.addCoveredId(coveredId);
        this.listener.endSpecificationItem();
    }

    private SpecificationItemId createCoveredItem(final String name, final String revision)
    {
        final int parsedRevision = parseRevision(name, revision);
        final String nameWithPrefix = getCoveredItemNamePrefix() + name;
        return SpecificationItemId.createId(this.pathConfig.getCoveredItemArtifactType(),
                nameWithPrefix, parsedRevision);
    }

    private int parseRevision(final String name, final String revision)
    {
        try
        {
            return Integer.parseInt(revision);
        }
        catch (final NumberFormatException e)
        {
            throw new ImporterException(
                    "Error parsing revision '" + revision + "' for item '" + name + "'.", e);
        }
    }

    private String getCoveredItemNamePrefix()
    {
        return this.pathConfig.getCoveredItemNamePrefix() != null
                ? this.pathConfig.getCoveredItemNamePrefix()
                : "";
    }

    private String generateName(final SpecificationItemId coveredId, final int lineNumber,
            final int counter)
    {
        final String uniqueName = this.file.toString() + lineNumber + counter
                + coveredId.toString();
        final String checksum = Long.toString(ChecksumCalculator.calculateCrc32(uniqueName));
        return coveredId.getName() + "-" + checksum;
    }
}
