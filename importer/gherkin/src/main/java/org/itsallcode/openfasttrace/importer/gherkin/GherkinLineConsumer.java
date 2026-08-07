package org.itsallcode.openfasttrace.importer.gherkin;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.tag.common.CoverageTagParser;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;

/** Stateful parser for the Gherkin lines of one input file. */
// [impl->dsn~gherkin.streaming-import~1]
// [impl->dsn~gherkin.id-detection~1]
// [impl->dsn~gherkin.covers-metadata-validation~1]
// [impl->dsn~gherkin.needs-metadata-validation~1]
// [impl->dsn~gherkin.comment-coverage-tags~1]
final class GherkinLineConsumer implements LineConsumer
{
    private static final Logger LOG = Logger.getLogger(GherkinLineConsumer.class.getName());
    private static final int UNICODE = Pattern.UNICODE_CHARACTER_CLASS;
    private static final Pattern ID_TAG = Pattern.compile("@id:(" + SpecificationItemId.ID_PATTERN.pattern() + ")",
            UNICODE);
    private static final Pattern SCENARIO = Pattern.compile("^\\s*Scenario(?: Outline)?:(.*)$", UNICODE);
    private static final Pattern BOUNDARY = Pattern
            .compile("^\\s*(?:Scenario(?: Outline)?|Feature|Rule|Background|Examples):", UNICODE);
    private static final Pattern OFT_DIRECTIVE = Pattern.compile("^\\s*#\\s*(Covers|Needs):(.*)$", UNICODE);
    private static final Pattern ARTIFACT_TYPE = Pattern.compile("\\p{IsAlphabetic}+");

    private final InputFile file;
    private final ImportEventListener listener;
    private final LineConsumer coverageTagParser;
    private LocatedSpecificationItemId pendingId;
    private Set<LocatedSpecificationItemId> coveredIds = new LinkedHashSet<>();
    private Set<String> neededArtifactTypes = new LinkedHashSet<>();
    private boolean hasNeedsDirective;
    private boolean metadataRegion;
    private boolean tagRegion;
    private boolean invalidMetadata;
    private boolean importingScenario;
    private int pendingIdLine;

    GherkinLineConsumer(final InputFile file, final ImportEventListener listener)
    {
        this.file = file;
        this.listener = listener;
        this.coverageTagParser = CoverageTagParser.create(null, file, listener);
    }

    @Override
    public void readLine(final int lineNumber, final String line)
    {
        if (line.trim().startsWith("#"))
        {
            this.coverageTagParser.readLine(lineNumber, line);
        }
        final Matcher scenario = SCENARIO.matcher(line);
        if (scenario.matches())
        {
            endScenario();
            beginScenario(scenario.group(1).trim());
            return;
        }
        if (BOUNDARY.matcher(line).find())
        {
            endScenario();
            clearMetadata();
            return;
        }
        if (this.importingScenario)
        {
            if (!line.trim().startsWith("#") && !line.trim().isEmpty())
            {
                this.listener.appendDescription(line + System.lineSeparator());
            }
            return;
        }
        readMetadata(lineNumber, line);
    }

    @Override
    public void finish()
    {
        endScenario();
    }

    private void readMetadata(final int lineNumber, final String line)
    {
        if (line.trim().startsWith("@"))
        {
            readTagRegion(lineNumber, line);
            return;
        }
        final Matcher directive = OFT_DIRECTIVE.matcher(line);
        if (this.metadataRegion && directive.matches())
        {
            readDirective(lineNumber, line, directive.group(1), directive.group(2));
            return;
        }
        if (!line.trim().startsWith("#"))
        {
            clearMetadata();
        }
    }

    private void readTagRegion(final int lineNumber, final String line)
    {
        if (!this.tagRegion)
        {
            clearMetadata();
        }
        this.metadataRegion = true;
        this.tagRegion = true;
        if (this.invalidMetadata)
        {
            return;
        }
        final String tags = line.trim();
        final Matcher matcher = ID_TAG.matcher(tags);
        while (matcher.find())
        {
            if (this.pendingId != null)
            {
                invalidateMetadata(lineNumber, "multiple @id tags before a scenario");
                return;
            }
            this.pendingId = locatedId(lineNumber, line.indexOf(tags) + matcher.start(1), matcher.group(1));
            if (this.pendingId == null)
            {
                return;
            }
            this.pendingIdLine = lineNumber;
        }
    }

    private void readDirective(final int lineNumber, final String line, final String name, final String values)
    {
        this.tagRegion = false;
        if (this.invalidMetadata)
        {
            return;
        }
        if (this.pendingId == null)
        {
            invalidateMetadata(lineNumber, name + " directive requires exactly one preceding @id tag");
            return;
        }
        final boolean covers = "Covers".equals(name);
        if (!covers && this.hasNeedsDirective)
        {
            invalidateMetadata(lineNumber, "repeated " + name + " directive");
            return;
        }
        final String[] entries = splitValues(lineNumber, name, values);
        if (covers)
        {
            readCoveredIds(lineNumber, line, entries);
            return;
        }
        readNeededArtifactTypes(lineNumber, entries);
        this.hasNeedsDirective = true;
    }

    private String[] splitValues(final int lineNumber, final String name, final String values)
    {
        if (values.trim().isEmpty())
        {
            invalidateMetadata(lineNumber, name + " directive requires a non-empty list");
            return new String[0];
        }
        return values.trim().split(",", -1);
    }

    private void readCoveredIds(final int lineNumber, final String line, final String[] entries)
    {
        int searchStart = 0;
        for (final String entry : entries)
        {
            final String value = requireValue(lineNumber, "Covers", entry);
            if (value == null)
            {
                return;
            }
            final int column = line.indexOf(value, searchStart);
            searchStart = column + value.length();
            final SpecificationItemId id = parseId(lineNumber, value);
            if (id == null)
            {
                return;
            }
            if (this.coveredIds.stream().anyMatch(locatedId -> locatedId.getId().equals(id)))
            {
                invalidateMetadata(lineNumber, "Covers directive contains duplicate value '" + id + "'");
                return;
            }
            this.coveredIds.add(locatedId(lineNumber, column, value, id));
        }
    }

    private void readNeededArtifactTypes(final int lineNumber, final String[] entries)
    {
        for (final String entry : entries)
        {
            final String value = requireValue(lineNumber, "Needs", entry);
            if (value == null)
            {
                return;
            }
            if (!ARTIFACT_TYPE.matcher(value).matches())
            {
                invalidateMetadata(lineNumber, "Needs directive contains invalid artifact type '" + value + "'");
                return;
            }
            if (!this.neededArtifactTypes.add(value))
            {
                invalidateMetadata(lineNumber, "Needs directive contains duplicate value '" + value + "'");
                return;
            }
        }
    }

    private String requireValue(final int lineNumber, final String name, final String entry)
    {
        final String value = entry.trim();
        if (value.isEmpty())
        {
            invalidateMetadata(lineNumber, name + " directive contains an empty value");
            return null;
        }
        return value;
    }

    private SpecificationItemId parseId(final int lineNumber, final String value)
    {
        if (!SpecificationItemId.ID_PATTERN.matcher(value).matches())
        {
            invalidateMetadata(lineNumber, "invalid specification item ID '" + value + "'");
            return null;
        }
        return SpecificationItemId.parseId(value);
    }

    private void beginScenario(final String title)
    {
        if (this.pendingId == null || this.invalidMetadata)
        {
            clearMetadata();
            return;
        }
        this.listener.beginSpecificationItem();
        this.listener.setLocation(this.file.getPath(), this.pendingIdLine);
        this.listener.setId(this.pendingId);
        this.listener.setTitle(title);
        this.coveredIds.forEach(this.listener::addCoveredId);
        this.neededArtifactTypes.forEach(this.listener::addNeededArtifactType);
        this.importingScenario = true;
        clearMetadata();
    }

    private void endScenario()
    {
        if (this.importingScenario)
        {
            this.listener.endSpecificationItem();
            this.importingScenario = false;
        }
    }

    private void clearMetadata()
    {
        this.pendingId = null;
        this.coveredIds = new LinkedHashSet<>();
        this.neededArtifactTypes = new LinkedHashSet<>();
        this.hasNeedsDirective = false;
        this.metadataRegion = false;
        this.tagRegion = false;
        this.invalidMetadata = false;
        this.pendingIdLine = 0;
    }

    private void invalidateMetadata(final int lineNumber, final String reason)
    {
        LOG.warning(() -> "Skipping Gherkin scenario metadata in " + this.file.getPath() + " at line " + lineNumber
                + ": " + reason);
        this.pendingId = null;
        this.coveredIds.clear();
        this.neededArtifactTypes.clear();
        this.invalidMetadata = true;
    }

    private static LocatedSpecificationItemId locatedId(final int lineNumber, final int column, final String value)
    {
        return locatedId(lineNumber, column, value, SpecificationItemId.parseId(value));
    }

    private static LocatedSpecificationItemId locatedId(final int lineNumber, final int column, final String value,
            final SpecificationItemId id)
    {
        // [impl->dsn~located-specification-item-id-text-ranges~1]
        final int line = lineNumber - 1;
        final SourceRange range = new SourceRange(new SourcePosition(line, column),
                new SourcePosition(line, column + value.length()));
        final int artifactEnd = value.indexOf('~');
        final int revisionStart = value.lastIndexOf('~') + 1;
        return LocatedSpecificationItemId.builder().id(id).range(range)
                .artifactTypeRange(componentRange(line, column, column + artifactEnd))
                .nameRange(componentRange(line, column + artifactEnd + 1, column + revisionStart - 1))
                .revisionRange(componentRange(line, column + revisionStart, column + value.length())).build();
    }

    private static SourceRange componentRange(final int line, final int start, final int end)
    {
        return new SourceRange(new SourcePosition(line, start), new SourcePosition(line, end));
    }
}
