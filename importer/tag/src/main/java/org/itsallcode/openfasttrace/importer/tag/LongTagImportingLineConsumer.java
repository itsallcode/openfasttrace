package org.itsallcode.openfasttrace.importer.tag;

import static java.util.Collections.emptyList;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

// [impl->dsn~import.full-coverage-tag~1]
// [impl->dsn~import.full-coverage-tag-with-needed-coverage~1]
class LongTagImportingLineConsumer extends RegexLineConsumer
{
    private static final Logger LOG = Logger
            .getLogger(LongTagImportingLineConsumer.class.getName());

    private static final String COVERING_ARTIFACT_TYPE_PATTERN = "\\p{Alpha}+";
    // [impl->dsn~import.full-coverage-tag-with-revision~1]
    private static final String OPTIONAL_WHITESPACE = "\\s*";
    private static final String TAG_PREFIX = "\\[";
    private static final String TAG_SUFFIX = "\\]";
    private static final String NEEDS_COVERAGE = ">>" + OPTIONAL_WHITESPACE + "(\\p{Alpha}+(?:"
            + OPTIONAL_WHITESPACE
            + "," + OPTIONAL_WHITESPACE + "\\p{Alpha}+)*)";
    private static final String TAG_REGEX = TAG_PREFIX + OPTIONAL_WHITESPACE//
            + "(" + COVERING_ARTIFACT_TYPE_PATTERN + ")"
            + "(?:" + SpecificationItemId.ARTIFACT_TYPE_SEPARATOR
            // [impl->dsn~import.full-coverage-tag-with-name-and-revision~1]
            + "(" + SpecificationItemId.ITEM_NAME_PATTERN + ")?"
            + SpecificationItemId.REVISION_SEPARATOR
            + SpecificationItemId.ITEM_REVISION_PATTERN + ")?" //
            + OPTIONAL_WHITESPACE + "->" + OPTIONAL_WHITESPACE //
            + "(" + SpecificationItemId.ID_PATTERN + ")" //
            + OPTIONAL_WHITESPACE + "(?:" + NEEDS_COVERAGE + OPTIONAL_WHITESPACE + ")?" //
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
        final SpecificationItemId coveredId = SpecificationItemId.parseId(matcher.group(5));
        final List<String> neededArtifactTypes = parseCommaSeparatedList(matcher.group(9));
        final SpecificationItemId generatedId = createItemId(matcher, lineNumber, lineMatchCount, coveredId,
                neededArtifactTypes);
        logItem(lineNumber, coveredId, neededArtifactTypes, generatedId);
        this.listener.setId(generatedId);
        this.listener.addCoveredId(coveredId);
        neededArtifactTypes.forEach(listener::addNeededArtifactType);
        this.listener.endSpecificationItem();
    }

    private List<String> parseCommaSeparatedList(final String input)
    {
        if (input == null)
        {
            return emptyList();
        }

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .toList();
    }

    private SpecificationItemId createItemId(final Matcher matcher, final int lineNumber, final int lineMatchCount,
            final SpecificationItemId coveredId, final List<String> neededArtifactTypes)
    {
        final String artifactType = matcher.group(1);
        final String customName = matcher.group(2);
        final String revision = matcher.group(4);
        final String name = customName != null ? customName
                : getItemName(lineNumber, lineMatchCount, coveredId, neededArtifactTypes);
        return SpecificationItemId.createId(artifactType, name, parseRevision(revision));
    }

    private void logItem(final int lineNumber, final SpecificationItemId coveredId,
            final List<String> neededArtifactTypes, final SpecificationItemId generatedId)
    {
        if (neededArtifactTypes.isEmpty())
        {
            LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + generatedId
                    + "' covering id '" + coveredId);
        }
        else
        {
            LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + generatedId
                    + "' covering id '" + coveredId + "', needs artifact types "
                    + neededArtifactTypes);
        }
    }

    private int parseRevision(final String revision)
    {
        return Optional.ofNullable(revision)
                .map(Integer::parseInt)
                .orElse(0);
    }

    // [impl->dsn~import.full-coverage-tag-with-needed-coverage-readable-names~1]
    private String getItemName(final int lineNumber, final int lineMatchCount, final SpecificationItemId coveredId,
            final List<String> neededArtifactTypes)
    {
        if (neededArtifactTypes.isEmpty())
        {
            return generateUniqueName(coveredId, lineNumber, lineMatchCount);
        }
        return coveredId.getName();
    }

    private String generateUniqueName(final SpecificationItemId coveredId, final int lineNumber,
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
