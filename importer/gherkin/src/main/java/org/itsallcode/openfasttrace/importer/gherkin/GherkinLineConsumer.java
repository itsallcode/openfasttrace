package org.itsallcode.openfasttrace.importer.gherkin;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.tag.common.CoverageTagParser;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;

/** Stateful parser for the Gherkin lines of one input file. */
// [impl->dsn~gherkin.streaming-import~1]
// [impl->dsn~gherkin.comment-coverage-tags~1]
final class GherkinLineConsumer implements LineConsumer {
    private static final int UNICODE = Pattern.UNICODE_CHARACTER_CLASS;
    private static final Pattern ID_TAG = Pattern.compile("@id:([^\\s]+)", UNICODE);
    private static final Pattern SCENARIO = Pattern.compile("^\\s*Scenario(?: Outline)?:(.*)$", UNICODE);
    private static final Pattern BOUNDARY = Pattern
            .compile("^\\s*(?:Scenario(?: Outline)?|Feature|Rule|Background|Examples):", UNICODE);
    private static final Pattern DIRECTIVE = Pattern.compile("^\\s*#\\s*(Covers|Needs):(.*)$", UNICODE);
    private static final Pattern ARTIFACT_TYPE = Pattern.compile("\\p{IsAlphabetic}+");

    private final InputFile file;
    private final ImportEventListener listener;
    private final LineConsumer coverageTagParser;
    // Legacy tags emit complete items and must not interleave with an open scenario.
    private final LineConsumer delayedCoverageTagParser;
    private final EventBuffer delayedCoverageEvents = new EventBuffer();
    private final Set<SpecificationItemId> importedIds = new LinkedHashSet<>();
    private SpecificationItemId pendingId;
    private Set<SpecificationItemId> coveredIds = new LinkedHashSet<>();
    private Set<String> neededArtifactTypes = new LinkedHashSet<>();
    private boolean hasNeedsDirective;
    private boolean metadataRegion;
    private boolean tagRegion;
    private boolean importingScenario;

    GherkinLineConsumer(final InputFile file, final ImportEventListener listener) {
        this.file = file;
        this.listener = listener;
        this.coverageTagParser = CoverageTagParser.create(null, file, listener);
        this.delayedCoverageTagParser = CoverageTagParser.create(null, file, this.delayedCoverageEvents);
    }

    @Override
    public void readLine(final int lineNumber, final String line) {
        if (line.trim().startsWith("#")) {
            getCoverageTagParser().readLine(lineNumber, line);
        }
        final Matcher scenario = SCENARIO.matcher(line);
        if (scenario.matches()) {
            endScenario();
            beginScenario(lineNumber, scenario.group(1).trim());
            return;
        }
        if (BOUNDARY.matcher(line).find()) {
            endScenario();
            clearMetadata();
            return;
        }
        if (this.importingScenario) {
            if (!line.trim().startsWith("#")) {
                this.listener.appendDescription(line + System.lineSeparator());
            }
            return;
        }
        readMetadata(lineNumber, line);
    }

    @Override
    public void finish() {
        endScenario();
    }

    private LineConsumer getCoverageTagParser() {
        return this.importingScenario ? this.delayedCoverageTagParser : this.coverageTagParser;
    }

    private void readMetadata(final int lineNumber, final String line) {
        if (line.trim().startsWith("@")) {
            readTagRegion(lineNumber, line.trim());
            return;
        }
        final Matcher directive = DIRECTIVE.matcher(line);
        if (this.metadataRegion && directive.matches()) {
            readDirective(lineNumber, directive.group(1), directive.group(2));
            return;
        }
        if (!line.trim().startsWith("#")) {
            clearMetadata();
        }
    }

    private void readTagRegion(final int lineNumber, final String tags) {
        if (!this.tagRegion) {
            clearMetadata();
        }
        this.metadataRegion = true;
        this.tagRegion = true;
        final Matcher matcher = ID_TAG.matcher(tags);
        while (matcher.find()) {
            if (this.pendingId != null) {
                fail(lineNumber, "multiple @id tags before a scenario");
            }
            this.pendingId = parseId(lineNumber, matcher.group(1));
        }
    }

    private void readDirective(final int lineNumber, final String name, final String values) {
        this.tagRegion = false;
        if (this.pendingId == null) {
            fail(lineNumber, name + " directive requires exactly one preceding @id tag");
        }
        final boolean covers = "Covers".equals(name);
        if (!covers && this.hasNeedsDirective) {
            fail(lineNumber, "repeated " + name + " directive");
        }
        final String[] entries = splitValues(lineNumber, name, values);
        if (covers) {
            readCoveredIds(lineNumber, entries);
            return;
        }
        readNeededArtifactTypes(lineNumber, entries);
        this.hasNeedsDirective = true;
    }

    private String[] splitValues(final int lineNumber, final String name, final String values) {
        if (values.trim().isEmpty()) {
            fail(lineNumber, name + " directive requires a non-empty list");
        }
        return values.trim().split(",", -1);
    }

    private void readCoveredIds(final int lineNumber, final String[] entries) {
        for (final String entry : entries) {
            final String value = requireValue(lineNumber, "Covers", entry);
            final SpecificationItemId id = parseId(lineNumber, value);
            if (!this.coveredIds.add(id)) {
                fail(lineNumber, "Covers directive contains duplicate value '" + id + "'");
            }
        }
    }

    private void readNeededArtifactTypes(final int lineNumber, final String[] entries) {
        for (final String entry : entries) {
            final String value = requireValue(lineNumber, "Needs", entry);
            if (!ARTIFACT_TYPE.matcher(value).matches()) {
                fail(lineNumber, "Needs directive contains invalid artifact type '" + value + "'");
            }
            if (!this.neededArtifactTypes.add(value)) {
                fail(lineNumber, "Needs directive contains duplicate value '" + value + "'");
            }
        }
    }

    private String requireValue(final int lineNumber, final String name, final String entry) {
        final String value = entry.trim();
        if (value.isEmpty()) {
            fail(lineNumber, name + " directive contains an empty value");
        }
        return value;
    }

    private SpecificationItemId parseId(final int lineNumber, final String value) {
        if (!SpecificationItemId.ID_PATTERN.matcher(value).matches()) {
            fail(lineNumber, "invalid specification item ID '" + value + "'");
        }
        return SpecificationItemId.parseId(value);
    }

    private void beginScenario(final int lineNumber, final String title) {
        if (this.pendingId == null) {
            clearMetadata();
            return;
        }
        if (!this.importedIds.add(this.pendingId)) {
            fail(lineNumber, "duplicate Gherkin ID '" + this.pendingId + "'");
        }
        this.listener.beginSpecificationItem();
        this.listener.setLocation(this.file.getPath(), lineNumber);
        this.listener.setId(this.pendingId);
        this.listener.setTitle(title);
        this.coveredIds.forEach(this.listener::addCoveredId);
        this.neededArtifactTypes.forEach(this.listener::addNeededArtifactType);
        this.importingScenario = true;
        clearMetadata();
    }

    private void endScenario() {
        if (this.importingScenario) {
            this.listener.endSpecificationItem();
            this.importingScenario = false;
            this.delayedCoverageEvents.replay(this.listener);
        }
    }

    private void clearMetadata() {
        this.pendingId = null;
        this.coveredIds = new LinkedHashSet<>();
        this.neededArtifactTypes = new LinkedHashSet<>();
        this.hasNeedsDirective = false;
        this.metadataRegion = false;
        this.tagRegion = false;
    }

    private void fail(final int lineNumber, final String reason) {
        throw new IllegalArgumentException(this.file.getPath() + ":" + lineNumber + ": " + reason);
    }
}
