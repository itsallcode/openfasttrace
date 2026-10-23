package org.itsallcode.openfasttrace.importer.tag.common;

import static java.util.Collections.emptyList;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

// [impl->dsn~import.full-coverage-tag~1]
// [impl->dsn~import.full-coverage-tag-with-needed-coverage~1]
// [impl->dsn~import.full-coverage-tag-multiple-needed-coverage~1]
class LongTagImportingLineConsumer extends AbstractRegexLineConsumer
{
    private static final Logger LOG = Logger.getLogger(LongTagImportingLineConsumer.class.getName());
    private static final String ARTIFACT_TYPE_GROUP = "artifactType";
    private static final String CUSTOM_NAME_GROUP = "customName";
    private static final String REVISION_GROUP = "revision";
    private static final String COVERED_IDS_GROUP = "coveredIds";
    private static final String COVERING_ARTIFACT_TYPE_PATTERN = "\\p{Alpha}+";
    // [impl->dsn~import.full-coverage-tag-with-revision~2]
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
            + "(?<" + ARTIFACT_TYPE_GROUP + ">" + COVERING_ARTIFACT_TYPE_PATTERN + ")"
            + "(?:" + SpecificationItemId.ARTIFACT_TYPE_SEPARATOR
            // [impl->dsn~import.full-coverage-tag-with-name-and-revision~2]
            + "(?<" + CUSTOM_NAME_GROUP + ">" + SpecificationItemId.ITEM_NAME_PATTERN + ")?"
            + SpecificationItemId.REVISION_SEPARATOR_PATTERN
            + "(?<" + REVISION_GROUP + ">" + SpecificationItemId.ITEM_REVISION_PATTERN + "))?"
            + OPTIONAL_WHITESPACE + "->" + OPTIONAL_WHITESPACE
            + "(?<" + COVERED_IDS_GROUP + ">" + COVERED_IDS + ")"
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
        final List<LocatedSpecificationItemId> locatedCoveredIds = locateCoveredIds(lineNumber, matcher);
        final List<SpecificationItemId> coveredIds = locatedCoveredIds.stream()
                .map(LocatedSpecificationItemId::getId)
                .toList();
        final List<String> neededArtifactTypes = parseNeededArtifactTypes(matcher.group("neededArtifactTypes"));
        final List<SpecificationItemId> generatedIds = createItemIds(matcher, lineNumber, lineMatchCount, coveredIds,
                neededArtifactTypes);

        if (generatedIds.size() > 1)
        {
            assert generatedIds.size() == locatedCoveredIds.size();
            for (int i = 0; i < generatedIds.size(); i++)
            {
                addSpecificationItem(lineNumber, matcher, generatedIds.get(i), List.of(locatedCoveredIds.get(i)),
                        neededArtifactTypes);
            }
        }
        else
        {
            addSpecificationItem(lineNumber, matcher, generatedIds.get(0), locatedCoveredIds, neededArtifactTypes);
        }
    }

    private void addSpecificationItem(final int lineNumber, final Matcher matcher,
            final SpecificationItemId generatedId, final List<LocatedSpecificationItemId> coveredIds,
            final List<String> neededArtifactTypes)
    {
        final SpecificationItem.Builder item = SpecificationItem.builder()
                .id(locatedGeneratedId(lineNumber, matcher, generatedId))
                .location(this.file.getPath(), lineNumber);
        coveredIds.forEach(item::addCoveredId);
        neededArtifactTypes.forEach(item::addNeedsArtifactType);
        this.listener.addSpecificationItem(item.build());
        logItem(lineNumber, coveredIds.stream().map(LocatedSpecificationItemId::getId).toList(), neededArtifactTypes,
                generatedId);
    }

    // [impl->dsn~located-specification-item-id-tag-ranges~1]
    private static List<LocatedSpecificationItemId> locateCoveredIds(final int lineNumber, final Matcher matcher)
    {
        final String coveredIdsText = matcher.group(COVERED_IDS_GROUP);
        final int coveredIdsStart = matcher.start(COVERED_IDS_GROUP);
        final Matcher idMatcher = SpecificationItemId.ID_PATTERN.matcher(coveredIdsText);
        final List<LocatedSpecificationItemId> located = new ArrayList<>();
        while (idMatcher.find())
        {
            located.add(locatedId(lineNumber, coveredIdsStart, idMatcher,
                    SpecificationItemId.fromIdPatternMatcher(idMatcher)));
        }
        return located;
    }

    private static LocatedSpecificationItemId locatedGeneratedId(final int lineNumber, final Matcher matcher,
            final SpecificationItemId id)
    {
        if (matcher.group(CUSTOM_NAME_GROUP) == null)
        {
            return LocatedSpecificationItemId.builder().id(id).build();
        }
        final int start = matcher.start(ARTIFACT_TYPE_GROUP);
        final int end = matcher.end(REVISION_GROUP);
        return LocatedSpecificationItemId.builder().id(id).range(sourceRange(lineNumber, start, end))
                .artifactTypeRange(sourceRange(lineNumber, start, matcher.end(ARTIFACT_TYPE_GROUP)))
                .nameRange(sourceRange(lineNumber, matcher.start(CUSTOM_NAME_GROUP), matcher.end(CUSTOM_NAME_GROUP)))
                .revisionRange(sourceRange(lineNumber, matcher.start(REVISION_GROUP), matcher.end(REVISION_GROUP)))
                .build();
    }

    private static LocatedSpecificationItemId locatedId(final int lineNumber, final int coveredIdsStart,
            final Matcher matcher, final SpecificationItemId id)
    {
        return LocatedSpecificationItemId.builder().id(id)
                .range(sourceRange(lineNumber, coveredIdsStart + matcher.start(), coveredIdsStart + matcher.end()))
                .artifactTypeRange(sourceRange(lineNumber, coveredIdsStart + matcher.start(1),
                        coveredIdsStart + matcher.end(1)))
                .nameRange(sourceRange(lineNumber, coveredIdsStart + matcher.start(2),
                        coveredIdsStart + matcher.end(2)))
                .revisionRange(sourceRange(lineNumber, coveredIdsStart + matcher.start(3),
                        coveredIdsStart + matcher.end(3))).build();
    }

    private static SourceRange sourceRange(final int lineNumber, final int start, final int end)
    {
        return new SourceRange(new SourcePosition(lineNumber - 1, start), new SourcePosition(lineNumber - 1, end));
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
        final String artifactType = matcher.group(ARTIFACT_TYPE_GROUP);
        final String customName = matcher.group(CUSTOM_NAME_GROUP);
        final String revision = matcher.group(REVISION_GROUP);
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
