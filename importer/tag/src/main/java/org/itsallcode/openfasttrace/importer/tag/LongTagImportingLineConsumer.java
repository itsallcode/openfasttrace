package org.itsallcode.openfasttrace.importer.tag;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

// [impl->dsn~import.full-coverage-tag~1]
// [impl->dsn~import.full-coverage-tag-with-needed-coverage~1]
class LongTagImportingLineConsumer extends AbstractRegexLineConsumer
{
    private static final Logger LOG = Logger
            .getLogger(LongTagImportingLineConsumer.class.getName());

    private static final String COVERING_ARTIFACT_TYPE_PATTERN = "\\p{Alpha}+";
    // [impl->dsn~import.full-coverage-tag-with-revision~1]
    private static final String OPTIONAL_WHITESPACE = "\\s*";
    private static final String TAG_PREFIX = "\\[";
    private static final String TAG_SUFFIX = "\\]";
    private static final String COVERED_IDS = SpecificationItemId.ID_PATTERN + "(?:"
            + OPTIONAL_WHITESPACE + "," + OPTIONAL_WHITESPACE + SpecificationItemId.ID_PATTERN
            + ")*";
    private static final String NEEDS_COVERAGE = ">>" + OPTIONAL_WHITESPACE
            + "(?<neededArtifactTypes>\\p{Alpha}+(?:" + OPTIONAL_WHITESPACE + ","
            + OPTIONAL_WHITESPACE + "\\p{Alpha}+)*)";
    private static final String TAG_REGEX = TAG_PREFIX + OPTIONAL_WHITESPACE//
            + "(?<artifactType>" + COVERING_ARTIFACT_TYPE_PATTERN + ")"
            + "(?:" + SpecificationItemId.ARTIFACT_TYPE_SEPARATOR
            // [impl->dsn~import.full-coverage-tag-with-name-and-revision~1]
            + "(?<customName>" + SpecificationItemId.ITEM_NAME_PATTERN + ")?"
            + SpecificationItemId.REVISION_SEPARATOR
            + "(?<revision>" + SpecificationItemId.ITEM_REVISION_PATTERN + "))?" //
            + OPTIONAL_WHITESPACE + "->" + OPTIONAL_WHITESPACE //
            + "(?<coveredIds>" + COVERED_IDS + ")" //
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
        final List<SpecificationItemId> coveredIds = parseCoveredIds(matcher.group("coveredIds"));
        final List<String> neededArtifactTypes = parseNeededArtifactTypes(matcher.group("neededArtifactTypes"));
        final SpecificationItemId generatedId = createItemId(matcher, lineNumber, lineMatchCount,
                coveredIds, neededArtifactTypes);
        logItem(lineNumber, coveredIds, neededArtifactTypes, generatedId);
        this.listener.setId(generatedId);
        coveredIds.forEach(this.listener::addCoveredId);
        neededArtifactTypes.forEach(this.listener::addNeededArtifactType);
        this.listener.endSpecificationItem();
    }

    private static List<SpecificationItemId> parseCoveredIds(final String input)
    {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .map(SpecificationItemId::parseId)
                .toList();
    }

    private static List<String> parseNeededArtifactTypes(final String input)
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
            final List<SpecificationItemId> coveredIds, final List<String> neededArtifactTypes)
    {
        final String artifactType = matcher.group("artifactType");
        final String customName = matcher.group("customName");
        final String revision = matcher.group("revision");
        final String name = customName != null ? customName
                : getItemName(lineNumber, lineMatchCount, coveredIds, neededArtifactTypes);
        return SpecificationItemId.createId(artifactType, name, parseRevision(revision));
    }

    private void logItem(final int lineNumber, final List<SpecificationItemId> coveredIds,
            final List<String> neededArtifactTypes, final SpecificationItemId generatedId)
    {
        if (neededArtifactTypes.isEmpty())
        {
            LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + generatedId
                    + "' covering ids " + coveredIds);
        }
        else
        {
            LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + generatedId
                    + "' covering ids " + coveredIds + ", needs artifact types "
                    + neededArtifactTypes);
        }
    }

    private static int parseRevision(final String revision)
    {
        return Optional.ofNullable(revision)
                .map(Integer::parseInt)
                .orElse(0);
    }

    // [impl->dsn~import.full-coverage-tag-with-needed-coverage-readable-names~1]
    private String getItemName(final int lineNumber, final int lineMatchCount,
            final List<SpecificationItemId> coveredIds, final List<String> neededArtifactTypes)
    {
        if (neededArtifactTypes.isEmpty())
        {
            return generateUniqueName(coveredIds, lineNumber, lineMatchCount);
        }
        return joinCoveredIdNamesWithHyphen(coveredIds);
    }

    private String generateUniqueName(final List<SpecificationItemId> coveredIds, final int lineNumber,
            final int counter)
    {
        final String CoveredIdStringsJoinedWithHyphen = coveredIds.stream()
                .map(SpecificationItemId::toString)
                .collect(java.util.stream.Collectors.joining("-"));
        final String uniqueName = this.file.getPath() + lineNumber + counter
                + CoveredIdStringsJoinedWithHyphen;
        final String checksum = Long.toString(ChecksumCalculator.calculateCrc32(uniqueName));
        return joinCoveredIdNamesWithHyphen(coveredIds) + "-" + checksum;
    }

    private static String joinCoveredIdNamesWithHyphen(final List<SpecificationItemId> coveredIds)
    {
        return coveredIds.stream()
                .map(SpecificationItemId::getName)
                .collect(java.util.stream.Collectors.joining("-"));
    }
}
