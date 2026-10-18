package org.itsallcode.openfasttrace.importer.tag.common;

import static java.util.Collections.emptyList;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
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
    private static final String CONTENT_GROUP = "content";
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
    private static final String TAG_HEAD = TAG_PREFIX + OPTIONAL_WHITESPACE
            + "(?<" + ARTIFACT_TYPE_GROUP + ">" + COVERING_ARTIFACT_TYPE_PATTERN + ")"
            + "(?:" + SpecificationItemId.ARTIFACT_TYPE_SEPARATOR
            // [impl->dsn~import.full-coverage-tag-with-name-and-revision~1]
            + "(?<" + CUSTOM_NAME_GROUP + ">" + SpecificationItemId.ITEM_NAME_PATTERN + ")?"
            + SpecificationItemId.REVISION_SEPARATOR
            + "(?<" + REVISION_GROUP + ">" + SpecificationItemId.ITEM_REVISION_PATTERN + "))?"
            + OPTIONAL_WHITESPACE + "->" + OPTIONAL_WHITESPACE;
    private static final String TAG_TAIL = OPTIONAL_WHITESPACE + "(?:" + NEEDS_COVERAGE + OPTIONAL_WHITESPACE + ")?"
            + TAG_SUFFIX;
    private static final String TAG_REGEX = TAG_HEAD
            + "(?<" + COVERED_IDS_GROUP + ">" + COVERED_IDS + ")"
            + TAG_TAIL;
    private static final Pattern TAG_PATTERN = Pattern.compile(TAG_REGEX);
    private static final String LINE_CONTINUATION = OPTIONAL_WHITESPACE + "," + OPTIONAL_WHITESPACE + "$";
    private static final String COMMENT_MARKER = "^" + OPTIONAL_WHITESPACE + "(?:[^\\p{Alnum}\\s\\[\\]]+"
            + OPTIONAL_WHITESPACE + ")?";
    private static final Pattern OPENING_LINE_PATTERN = Pattern.compile(TAG_HEAD + COVERED_IDS + LINE_CONTINUATION);
    private static final Pattern CONTINUATION_LINE_PATTERN = Pattern
            .compile(COMMENT_MARKER + "(?<" + CONTENT_GROUP + ">" + COVERED_IDS + LINE_CONTINUATION + ")");
    private static final Pattern CLOSING_LINE_PATTERN = Pattern
            .compile(COMMENT_MARKER + "(?<" + CONTENT_GROUP + ">" + COVERED_IDS + TAG_TAIL + ")");

    private final InputFile file;
    private final ImportEventListener listener;
    private final boolean multiLineTags;
    private MultiLineTag openTag;

    LongTagImportingLineConsumer(final InputFile file, final ImportEventListener listener)
    {
        this(file, listener, false);
    }

    LongTagImportingLineConsumer(final InputFile file, final ImportEventListener listener,
            final boolean multiLineTags)
    {
        super(TAG_REGEX);
        this.file = file;
        this.listener = listener;
        this.multiLineTags = multiLineTags;
    }

    @Override
    public void readLine(final int lineNumber, final String line)
    {
        if (!this.multiLineTags)
        {
            super.readLine(lineNumber, line);
        }
        else if (this.openTag == null)
        {
            readLineOutsideOfTag(lineNumber, line, 0);
        }
        else
        {
            continueMultiLineTag(lineNumber, line);
        }
    }

    // [impl->dsn~import.full-coverage-tag-multi-line~1]
    private void readLineOutsideOfTag(final int lineNumber, final String line, final int startColumn)
    {
        final int lineMatchCount = processMatches(lineNumber, line, startColumn);
        final Matcher matcher = OPENING_LINE_PATTERN.matcher(line);
        matcher.region(startColumn, line.length());
        if (matcher.find())
        {
            this.openTag = new MultiLineTag(lineNumber, lineMatchCount);
            this.openTag.append(lineNumber, matcher.start(), matcher.group());
        }
    }

    // [impl->dsn~import.full-coverage-tag-multi-line~1]
    private void continueMultiLineTag(final int lineNumber, final String line)
    {
        final Matcher continuation = CONTINUATION_LINE_PATTERN.matcher(line);
        if (continuation.find())
        {
            this.openTag.append(lineNumber, continuation.start(CONTENT_GROUP), continuation.group(CONTENT_GROUP));
            return;
        }
        final Matcher closing = CLOSING_LINE_PATTERN.matcher(line);
        if (!closing.lookingAt())
        {
            throw new ImporterException("Multi-line coverage tag started at line " + this.openTag.lineNumber
                    + " is not continued with a comma-separated list of specification item IDs "
                    + "or terminated with ']'.");
        }
        final MultiLineTag closedTag = this.openTag;
        this.openTag = null;
        closedTag.append(lineNumber, closing.start(CONTENT_GROUP), closing.group(CONTENT_GROUP));
        final Matcher tag = TAG_PATTERN.matcher(closedTag.text);
        if (!tag.matches())
        {
            throw new IllegalStateException("Joined multi-line coverage tag '" + closedTag.text
                    + "' does not match the coverage tag format.");
        }
        importTag(tag, closedTag.lineNumber, closedTag.lineMatchCount, closedTag::positionOf);
        readLineOutsideOfTag(lineNumber, line, closing.end());
    }

    @Override
    public void finish()
    {
        if (this.openTag != null)
        {
            throw new ImporterException("Multi-line coverage tag started at line " + this.openTag.lineNumber
                    + " is not terminated with ']'.");
        }
    }

    @Override
    public void processMatch(final Matcher matcher, final int lineNumber, final int lineMatchCount)
    {
        importTag(matcher, lineNumber, lineMatchCount, column -> new SourcePosition(lineNumber - 1, column));
    }

    private void importTag(final Matcher matcher, final int lineNumber, final int lineMatchCount,
            final PositionMapper positions)
    {
        final List<SpecificationItemId> coveredIds = parseCoveredIds(matcher.group(COVERED_IDS_GROUP));
        final List<String> neededArtifactTypes = parseNeededArtifactTypes(matcher.group("neededArtifactTypes"));
        final List<SpecificationItemId> generatedIds = createItemIds(matcher, lineNumber, lineMatchCount, coveredIds,
                neededArtifactTypes);

        if (generatedIds.size() > 1)
        {
            assert generatedIds.size() == coveredIds.size();
            for (int i = 0; i < generatedIds.size(); i++)
            {
                addSpecificationItem(lineNumber, matcher, positions, generatedIds.get(i), List.of(coveredIds.get(i)),
                        neededArtifactTypes);
            }
        }
        else
        {
            addSpecificationItem(lineNumber, matcher, positions, generatedIds.get(0), coveredIds,
                    neededArtifactTypes);
        }
    }

    private void addSpecificationItem(final int lineNumber, final Matcher matcher, final PositionMapper positions,
            final SpecificationItemId generatedId, final List<SpecificationItemId> coveredIds,
            final List<String> neededArtifactTypes)
    {
        final SpecificationItem.Builder item = SpecificationItem.builder()
                .id(locatedGeneratedId(matcher, positions, generatedId))
                .location(this.file.getPath(), lineNumber);
        int searchStart = 0;
        for (final SpecificationItemId coveredId : coveredIds)
        {
            final int start = matcher.group(COVERED_IDS_GROUP).indexOf(coveredId.toString(), searchStart);
            searchStart = start + coveredId.toString().length();
            item.addCoveredId(locatedId(positions, matcher.start(COVERED_IDS_GROUP) + start, coveredId));
        }
        neededArtifactTypes.forEach(item::addNeedsArtifactType);
        this.listener.addSpecificationItem(item.build());
        logItem(lineNumber, coveredIds, neededArtifactTypes, generatedId);
    }

    private static LocatedSpecificationItemId locatedGeneratedId(final Matcher matcher,
            final PositionMapper positions, final SpecificationItemId id)
    {
        // [impl->dsn~located-specification-item-id-tag-ranges~1]
        if (matcher.group(CUSTOM_NAME_GROUP) == null)
        {
            return LocatedSpecificationItemId.builder().id(id).build();
        }
        final int start = matcher.start(ARTIFACT_TYPE_GROUP);
        final int end = matcher.end(REVISION_GROUP);
        return LocatedSpecificationItemId.builder().id(id).range(sourceRange(positions, start, end))
                .artifactTypeRange(sourceRange(positions, start, matcher.end(ARTIFACT_TYPE_GROUP)))
                .nameRange(sourceRange(positions, matcher.start(CUSTOM_NAME_GROUP), matcher.end(CUSTOM_NAME_GROUP)))
                .revisionRange(sourceRange(positions, matcher.start(REVISION_GROUP), matcher.end(REVISION_GROUP)))
                .build();
    }

    private static LocatedSpecificationItemId locatedId(final PositionMapper positions, final int start,
            final SpecificationItemId id)
    {
        final String text = id.toString();
        final int typeEnd = text.indexOf('~');
        final int revisionStart = text.lastIndexOf('~') + 1;
        return LocatedSpecificationItemId.builder().id(id).range(sourceRange(positions, start, start + text.length()))
                .artifactTypeRange(sourceRange(positions, start, start + typeEnd))
                .nameRange(sourceRange(positions, start + typeEnd + 1, start + revisionStart - 1))
                .revisionRange(sourceRange(positions, start + revisionStart, start + text.length())).build();
    }

    private static SourceRange sourceRange(final PositionMapper positions, final int start, final int end)
    {
        return new SourceRange(positions.positionOf(start), positions.positionOf(end));
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

    /**
     * Maps an offset in the matched tag text to a position in the source file.
     */
    @FunctionalInterface
    private interface PositionMapper
    {
        SourcePosition positionOf(int offset);
    }

    /**
     * Coverage tag spanning multiple lines, joined into a single line of text.
     */
    // [impl->dsn~import.full-coverage-tag-multi-line~1]
    private static final class MultiLineTag
    {
        private final int lineNumber;
        private final int lineMatchCount;
        private final List<Segment> segments = new ArrayList<>();
        private String text = "";

        private MultiLineTag(final int lineNumber, final int lineMatchCount)
        {
            this.lineNumber = lineNumber;
            this.lineMatchCount = lineMatchCount;
        }

        private void append(final int segmentLineNumber, final int column, final String segmentText)
        {
            if (!this.text.isEmpty())
            {
                this.text += " ";
            }
            this.segments.add(new Segment(this.text.length(), segmentLineNumber, column));
            this.text += segmentText;
        }

        private SourcePosition positionOf(final int offset)
        {
            Segment containingSegment = this.segments.get(0);
            for (final Segment segment : this.segments)
            {
                if (segment.offset() <= offset)
                {
                    containingSegment = segment;
                }
            }
            return new SourcePosition(containingSegment.lineNumber() - 1,
                    containingSegment.column() + offset - containingSegment.offset());
        }

        private record Segment(int offset, int lineNumber, int column)
        {
        }
    }
}
