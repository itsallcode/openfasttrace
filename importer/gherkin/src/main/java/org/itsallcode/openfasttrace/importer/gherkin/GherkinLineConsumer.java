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
    // Legacy tags emit complete items and must not interleave with an open
    // scenario.
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
        if (importingScenario) {
            if (!line.trim().startsWith("#")) {
                listener.appendDescription(line + System.lineSeparator());
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
        return importingScenario ? delayedCoverageTagParser : coverageTagParser;
    }

    private void readMetadata(final int lineNumber, final String line) {
        if (line.trim().startsWith("@")) {
            readTagRegion(lineNumber, line.trim());
            return;
        }
        final Matcher directive = DIRECTIVE.matcher(line);
        if (metadataRegion && directive.matches()) {
            readDirective(lineNumber, directive.group(1), directive.group(2));
            return;
        }
        if (!line.trim().startsWith("#")) {
            clearMetadata();
        }
    }

    private void readTagRegion(final int lineNumber, final String tags) {
        if (!tagRegion) {
            clearMetadata();
        }
        metadataRegion = true;
        tagRegion = true;
        final Matcher matcher = ID_TAG.matcher(tags);
        while (matcher.find()) {
            if (pendingId != null) {
                fail(lineNumber, "multiple @id tags before a scenario");
            }
            pendingId = parseId(lineNumber, matcher.group(1));
        }
    }

    private void readDirective(final int lineNumber, final String name, final String values) {
        tagRegion = false;
        if (pendingId == null) {
            fail(lineNumber, name + " directive requires exactly one preceding @id tag");
        }
        final boolean covers = "Covers".equals(name);
        if (!covers && hasNeedsDirective) {
            fail(lineNumber, "repeated " + name + " directive");
        }
        final String[] entries = splitValues(lineNumber, name, values);
        if (covers) {
            readCoveredIds(lineNumber, entries);
            return;
        }
        readNeededArtifactTypes(lineNumber, entries);
        hasNeedsDirective = true;
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
        if (pendingId == null) {
            clearMetadata();
            return;
        }
        if (!importedIds.add(pendingId)) {
            fail(lineNumber, "duplicate Gherkin ID '" + pendingId + "'");
        }
        listener.beginSpecificationItem();
        listener.setLocation(file.getPath(), lineNumber);
        listener.setId(pendingId);
        listener.setTitle(title);
        coveredIds.forEach(listener::addCoveredId);
        neededArtifactTypes.forEach(listener::addNeededArtifactType);
        importingScenario = true;
        clearMetadata();
    }

    private void endScenario() {
        if (importingScenario) {
            listener.endSpecificationItem();
            importingScenario = false;
            delayedCoverageEvents.replay(listener);
        }
    }

    private void clearMetadata() {
        pendingId = null;
        coveredIds = new LinkedHashSet<>();
        neededArtifactTypes = new LinkedHashSet<>();
        hasNeedsDirective = false;
        metadataRegion = false;
        tagRegion = false;
    }

    private void fail(final int lineNumber, final String reason) {
        throw new IllegalArgumentException(file.getPath() + ":" + lineNumber + ": " + reason);
    }
}
