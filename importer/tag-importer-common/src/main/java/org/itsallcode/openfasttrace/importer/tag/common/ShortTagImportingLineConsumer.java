package org.itsallcode.openfasttrace.importer.tag.common;

import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;

// [impl->dsn~import.short-coverage-tag~1]
class ShortTagImportingLineConsumer extends AbstractRegexLineConsumer {
    private static final Logger LOG = Logger.getLogger(ShortTagImportingLineConsumer.class.getName());

    private static final String TAG_PREFIX = "\\[\\[";
    private static final String TAG_SUFFIX = "\\]\\]";
    private static final String TAG_REGEX = TAG_PREFIX
            + SpecificationItemId.ITEM_NAME_PATTERN
            + ":" //
            + "(\\w+)" //
            + TAG_SUFFIX;

    private final PathConfig pathConfig;
    private final ImportEventListener listener;
    private final InputFile file;

    ShortTagImportingLineConsumer(final PathConfig pathConfig, final InputFile file,
            final ImportEventListener listener) {
        super(TAG_REGEX);
        this.pathConfig = pathConfig;
        this.file = file;
        this.listener = listener;
    }

    @Override
    void processMatch(final Matcher matcher, final int lineNumber, final int lineMatchCount) {
        final String coveredItemName = matcher.group(1);
        final String coveredItemRevision = matcher.group(2);
        final SpecificationItemId coveredId = createCoveredItem(coveredItemName, coveredItemRevision);
        final String generatedName = generateName(coveredId, lineNumber, lineMatchCount);
        final SpecificationItemId tagItemId = SpecificationItemId.createId(this.pathConfig.getTagArtifactType(),
                generatedName);

        LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + tagItemId + "' covering id '"
                + coveredId + "'");
        this.listener.addSpecificationItem(SpecificationItem.builder()
                .id(LocatedSpecificationItemId.builder().id(tagItemId).build())
                .location(this.file.toString(), lineNumber)
                .addCoveredId(locatedCoveredId(matcher, lineNumber, coveredId))
                .build());
    }

    private static LocatedSpecificationItemId locatedCoveredId(final Matcher matcher, final int lineNumber,
            final SpecificationItemId id)
    {
        // [impl->dsn~located-specification-item-id-tag-ranges~1]
        final int start = matcher.start(1);
        final int end = matcher.end(2);
        final SourceRange range = new SourceRange(new SourcePosition(lineNumber - 1, start),
                new SourcePosition(lineNumber - 1, end));
        return LocatedSpecificationItemId.builder().id(id).range(range)
                .nameRange(new SourceRange(new SourcePosition(lineNumber - 1, start),
                        new SourcePosition(lineNumber - 1, matcher.end(1))))
                .revisionRange(new SourceRange(new SourcePosition(lineNumber - 1, matcher.start(2)),
                        new SourcePosition(lineNumber - 1, end))).build();
    }

    private SpecificationItemId createCoveredItem(final String name, final String revision) {
        final int parsedRevision = parseRevision(name, revision);
        final String nameWithPrefix = getCoveredItemNamePrefix() + name;
        return SpecificationItemId.createId(this.pathConfig.getCoveredItemArtifactType(), nameWithPrefix,
                parsedRevision);
    }

    private static int parseRevision(final String name, final String revision) {
        try {
            return Integer.parseInt(revision);
        } catch (final NumberFormatException exception) {
            throw new ImporterException("Error parsing revision '" + revision + "' for item '" + name + "'.",
                    exception);
        }
    }

    private String getCoveredItemNamePrefix() {
        return this.pathConfig.getCoveredItemNamePrefix() != null ? this.pathConfig.getCoveredItemNamePrefix() : "";
    }

    private String generateName(final SpecificationItemId coveredId, final int lineNumber, final int counter) {
        final String uniqueName = this.file.toString() + lineNumber + counter + coveredId;
        final String checksum = Long.toString(ChecksumCalculator.calculateCrc32(uniqueName));
        return coveredId.getName() + "-" + checksum;
    }
}
