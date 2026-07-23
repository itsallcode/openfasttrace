package org.itsallcode.openfasttrace.importer.gherkin;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.tag.common.CoverageTagParser;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;

/** Imports annotated Gherkin scenarios while streaming the input once. */
// [impl->dsn~gherkin.streaming-import~1]
// [impl->dsn~gherkin.comment-coverage-tags~1]
final class GherkinImporter implements Importer, LineConsumer {
    private static final Pattern ID_TAG = Pattern.compile("@id:(\\S+)");
    private static final Pattern SCENARIO = Pattern.compile("^\\s*Scenario(?: Outline)?:\\s*(.*)$");
    private static final Pattern BOUNDARY = Pattern
            .compile("^\\s*(?:Scenario(?: Outline)?|Feature|Rule|Background|Examples):");
    private static final Pattern DIRECTIVE = Pattern.compile("^\\s*#\\s*(Covers|Needs):(.*)$");

    private final InputFile file;
    private final ImportEventListener listener;
    private final LineConsumer coverageTagParser;
    // Legacy tags emit complete items and therefore must not interleave with an open scenario item.
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

    GherkinImporter(final InputFile file, final ImportEventListener listener) {
        this.file = file;
        this.listener = listener;
        this.coverageTagParser = CoverageTagParser.create(null, file, listener);
        this.delayedCoverageTagParser = CoverageTagParser.create(null, file, this.delayedCoverageEvents);
    }

    @Override
    public void runImport() {
        LineReader.create(this.file).readLines(this);
        endScenario();
    }

    @Override
    public void readLine(final int lineNumber, final String line) {
        if (line.trim().startsWith("#")) {
            getCoverageTagParser().readLine(lineNumber, line);
        }
        final Matcher scenario = SCENARIO.matcher(line);
        if (scenario.matches()) {
            endScenario();
            beginScenario(lineNumber, scenario.group(1));
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
        final String[] entries = values.trim().split(",", -1);
        if (entries.length == 0 || values.trim().isEmpty()) {
            fail(lineNumber, name + " directive requires a non-empty list");
        }
        for (final String entry : entries) {
            if (entry.trim().isEmpty()) {
                fail(lineNumber, name + " directive contains an empty value");
            }
            if (covers) {
                final SpecificationItemId id = parseId(lineNumber, entry.trim());
                if (!this.coveredIds.add(id)) {
                    fail(lineNumber, "Covers directive contains duplicate value '" + id + "'");
                }
            } else if (!entry.trim().matches("\\p{Alpha}+")) {
                fail(lineNumber, "Needs directive contains invalid artifact type '" + entry.trim() + "'");
            } else if (!this.neededArtifactTypes.add(entry.trim())) {
                fail(lineNumber, "Needs directive contains duplicate value '" + entry.trim() + "'");
            }
        }
        if (!covers) {
            this.hasNeedsDirective = true;
        }
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

    /** Buffers legacy coverage-tag events until the current scenario has ended. */
    private static final class EventBuffer implements ImportEventListener {
        private final List<Consumer<ImportEventListener>> events = new ArrayList<>();

        @Override
        public void beginSpecificationItem() {
            this.events.add(ImportEventListener::beginSpecificationItem);
        }

        @Override
        public void setId(final SpecificationItemId id) {
            this.events.add(listener -> listener.setId(id));
        }

        @Override
        public void setTitle(final String title) {
            this.events.add(listener -> listener.setTitle(title));
        }

        @Override
        public void setStatus(final ItemStatus status) {
            this.events.add(listener -> listener.setStatus(status));
        }

        @Override
        public void appendDescription(final String fragment) {
            this.events.add(listener -> listener.appendDescription(fragment));
        }

        @Override
        public void appendRationale(final String fragment) {
            this.events.add(listener -> listener.appendRationale(fragment));
        }

        @Override
        public void appendComment(final String fragment) {
            this.events.add(listener -> listener.appendComment(fragment));
        }

        @Override
        public void addCoveredId(final SpecificationItemId id) {
            this.events.add(listener -> listener.addCoveredId(id));
        }

        @Override
        public void addDependsOnId(final SpecificationItemId id) {
            this.events.add(listener -> listener.addDependsOnId(id));
        }

        @Override
        public void addNeededArtifactType(final String artifactType) {
            this.events.add(listener -> listener.addNeededArtifactType(artifactType));
        }

        @Override
        public void addTag(final String tag) {
            this.events.add(listener -> listener.addTag(tag));
        }

        @Override
        public void setLocation(final String path, final int line) {
            this.events.add(listener -> listener.setLocation(path, line));
        }

        @Override
        public void endSpecificationItem() {
            this.events.add(ImportEventListener::endSpecificationItem);
        }

        @Override
        public void setLocation(final Location location) {
            this.events.add(listener -> listener.setLocation(location));
        }

        @Override
        public void setForwards(final boolean forwards) {
            this.events.add(listener -> listener.setForwards(forwards));
        }

        void replay(final ImportEventListener listener) {
            this.events.forEach(event -> event.accept(listener));
            this.events.clear();
        }
    }
}
