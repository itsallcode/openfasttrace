package org.itsallcode.openfasttrace.importer.tag.common;

import static java.util.Collections.emptyList;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

// [impl->dsn~import.full-coverage-tag~1]
// [impl->dsn~import.full-coverage-tag-with-needed-coverage~1]
// [impl->dsn~import.full-coverage-tag-multiple-needed-coverage~1]
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
    private static final String TAG_REGEX = TAG_PREFIX + OPTIONAL_WHITESPACE
            + "(?<artifactType>" + COVERING_ARTIFACT_TYPE_PATTERN + ")"
            + "(?:" + SpecificationItemId.ARTIFACT_TYPE_SEPARATOR
            // [impl->dsn~import.full-coverage-tag-with-name-and-revision~1]
            + "(?<customName>" + SpecificationItemId.ITEM_NAME_PATTERN + ")?"
            + SpecificationItemId.REVISION_SEPARATOR
            + "(?<revision>" + SpecificationItemId.ITEM_REVISION_PATTERN + "))?"
            + OPTIONAL_WHITESPACE + "->" + OPTIONAL_WHITESPACE
            + "(?<coveredIds>" + COVERED_IDS + ")"
            + OPTIONAL_WHITESPACE + "(?:" + NEEDS_COVERAGE + OPTIONAL_WHITESPACE + ")?"
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
        final List<SpecificationItemId> coveredIds = parseCoveredIds(matcher.group("coveredIds"));
        final List<String> neededArtifactTypes = parseNeededArtifactTypes(matcher.group("neededArtifactTypes"));
        final List<SpecificationItemId> generatedIds = createItemIds(matcher, lineNumber, lineMatchCount, coveredIds,
                neededArtifactTypes);

        if (generatedIds.size() > 1)
        {
            assert generatedIds.size() == coveredIds.size();
            for (int i = 0; i < generatedIds.size(); i++)
            {
                addSpecificationItem(lineNumber, generatedIds.get(i), List.of(coveredIds.get(i)), neededArtifactTypes);
            }
        }
        else
        {
            addSpecificationItem(lineNumber, generatedIds.get(0), coveredIds, neededArtifactTypes);
        }
    }

    private void addSpecificationItem(final int lineNumber, final SpecificationItemId generatedId,
            final List<SpecificationItemId> coveredIds, final List<String> neededArtifactTypes)
    {
        final SpecificationItem.Builder item = SpecificationItem.builder()
                .id(generatedId)
                .location(this.file.getPath(), lineNumber);
        coveredIds.forEach(item::addCoveredId);
        neededArtifactTypes.forEach(item::addNeedsArtifactType);
        this.listener.addSpecificationItem(item.build());
        logItem(lineNumber, coveredIds, neededArtifactTypes, generatedId);
    }

    private static List<SpecificationItemId> parseCoveredIds(final String input)
    {
        if (input == null)
        {
            return emptyList();
        }
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

    private List<SpecificationItemId> createItemIds(final Matcher matcher, final int lineNumber,
            final int lineMatchCount, final List<SpecificationItemId> coveredIds,
            final List<String> neededArtifactTypes)
    {
        final String artifactType = matcher.group("artifactType");
        final String customName = matcher.group("customName");
        final String revision = matcher.group("revision");
        if (customName != null)
        {
            return List.of(SpecificationItemId.createId(artifactType, customName, parseRevision(revision)));
        }

        final List<SpecificationItemId> result = new java.util.ArrayList<>(coveredIds.size());
        for (final SpecificationItemId coveredId : coveredIds)
        {
            final String name = getItemName(lineNumber, lineMatchCount, coveredId, neededArtifactTypes);
            result.add(SpecificationItemId.createId(artifactType, name, parseRevision(revision)));
        }
        return result;
    }

    private void logItem(final int lineNumber, final List<SpecificationItemId> coveredIds,
            final List<String> neededArtifactTypes, final SpecificationItemId generatedId)
    {
        if (neededArtifactTypes.isEmpty())
        {
            LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + generatedId + "' covering IDs "
                    + coveredIds);
        }
        else
        {
            LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + generatedId + "' covering IDs "
                    + coveredIds + ", needs artifact types " + neededArtifactTypes);
        }
    }

    private static int parseRevision(final String revision)
    {
        return Optional.ofNullable(revision).map(Integer::parseInt).orElse(0);
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

    private String generateUniqueName(final SpecificationItemId coveredId, final int lineNumber, final int counter)
    {
        final String uniqueName = this.file.getPath() + lineNumber + counter + coveredId;
        final String checksum = Long.toString(ChecksumCalculator.calculateCrc32(uniqueName));
        return coveredId.getName() + "-" + checksum;
    }
}
