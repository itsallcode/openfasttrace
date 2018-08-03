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

import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.ChecksumCalculator;
import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.ImporterException;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.tag.config.PathConfig;

// [impl->dsn~import.short-coverage-tag~1]
class ShortTagImportingLineConsumer extends RegexLineConsumer
{
    private static final Logger LOG = Logger
            .getLogger(ShortTagImportingLineConsumer.class.getName());

    private static final String TAG_PREFIX = "\\[\\[";
    private static final String TAG_SUFFIX = "\\]\\]";
    private static final String TAG_REGEX = TAG_PREFIX //
            + SpecificationItemId.ITEM_NAME_PATTERN //
            + ":" //
            + "(\\w+)" //
            + TAG_SUFFIX;

    private final PathConfig pathConfig;
    private final ImportEventListener listener;
    private final InputFile file;

    ShortTagImportingLineConsumer(final PathConfig pathConfig, final InputFile file,
            final ImportEventListener listener)
    {
        super(TAG_REGEX);
        this.pathConfig = pathConfig;
        this.file = file;
        this.listener = listener;
    }

    @Override
    void processMatch(final Matcher matcher, final int lineNumber, final int lineMatchCount)
    {
        final String coveredItemName = matcher.group(1);
        final String coveredItemRevision = matcher.group(2);
        final SpecificationItemId coveredId = createCoveredItem(coveredItemName,
                coveredItemRevision);

        final String generatedName = generateName(coveredId, lineNumber, lineMatchCount);
        final SpecificationItemId tagItemId = SpecificationItemId
                .createId(this.pathConfig.getTagArtifactType(), generatedName);

        LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + tagItemId
                + "' covering id '" + coveredId + "'");

        addItem(lineNumber, coveredId, tagItemId);
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
