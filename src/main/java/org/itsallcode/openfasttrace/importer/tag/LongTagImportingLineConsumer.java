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
import org.itsallcode.openfasttrace.importer.input.InputFile;

// [impl->dsn~import.full-coverage-tag~1]
class LongTagImportingLineConsumer extends RegexLineConsumer
{
    private static final Logger LOG = Logger
            .getLogger(LongTagImportingLineConsumer.class.getName());

    private static final String COVERING_ARTIFACT_TYPE_PATTERN = "\\p{Alpha}+";
    private static final String TAG_PREFIX = "\\[";
    private static final String TAG_SUFFIX = "\\]";
    private static final String TAG_REGEX = TAG_PREFIX //
            + "(" + COVERING_ARTIFACT_TYPE_PATTERN + ")" //
            + "->" //
            + "(" + SpecificationItemId.ID_PATTERN + ")" //
            + TAG_SUFFIX;

    private final InputFile file;
    private final ImportEventListener listener;

    LongTagImportingLineConsumer(final InputFile file, final ImportEventListener listener)
    {
        super(TAG_REGEX);
        this.file = file;
        this.listener = listener;
    }

    @Override
    public void processMatch(final Matcher matcher, final int lineNumber, final int lineMatchCount)
    {
        this.listener.beginSpecificationItem();
        this.listener.setLocation(this.file.getPath(), lineNumber);
        final SpecificationItemId coveredId = SpecificationItemId.parseId(matcher.group(2));
        final String generatedName = generateName(coveredId, lineNumber, lineMatchCount);
        final SpecificationItemId generatedId = SpecificationItemId.createId(matcher.group(1),
                generatedName, 0);

        LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + generatedId
                + "' covering id '" + coveredId + "'");
        this.listener.setId(generatedId);
        this.listener.addCoveredId(coveredId);
        this.listener.endSpecificationItem();
    }

    private String generateName(final SpecificationItemId coveredId, final int lineNumber,
            final int counter)
    {
        final String uniqueName = new StringBuilder() //
                .append(this.file.getPath()) //
                .append(lineNumber) //
                .append(counter) //
                .append(coveredId) //
                .toString();
        final String checksum = Long.toString(ChecksumCalculator.calculateCrc32(uniqueName));
        return coveredId.getName() + "-" + checksum;
    }
}
