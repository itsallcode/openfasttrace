# GH-563 Support Gherkin .feature Files As OpenFastTrace Specification Documents

## Goal

Allow OpenFastTrace to import specification items from Gherkin scenarios in `.feature` files while preserving the current behavior for legacy coverage-tag-only imports, based on a shared tag-parsing module that is reusable for GH-562 and GH-563.

## Scope

In scope:

* Define and implement a two-PR approach where shared parser refactoring happens in a dedicated, smaller PR before GH-563 feature work.
* Parse `Scenario` and `Scenario Outline` blocks as OFT specification items in `.feature` files.
* Read one OFT specification ID from a dedicated Gherkin tag (`@id:<oft-id>`).
* Parse `# Covers:` and `# Needs:` metadata inside the defined metadata scope.
* Validate and report malformed or ambiguous Gherkin specification input.
* Keep existing tag importer behavior backward compatible.
* Add and update tests for both new behavior and regression coverage.
* Update user-facing documentation for the new syntax.

Out of scope:

* Changing the syntax or behavior of existing long and short coverage tags.
* Importing `Feature`, `Rule`, `Background`, or `Examples` as specification items.
* Adding new external parser dependencies.
* Bundling large parser refactoring and Gherkin feature behavior into one PR.

## Design References

* [System Requirements](../spec/system_requirements.md)
* [Design](../spec/design.md)
* [Quality Requirements](../spec/design/quality_requirements.md)
* [User Guide](../user_guide.md)

## Strategy

1. Create a separate refactoring PR first that extracts shared parsing logic into a new module under `importer/` (proposed module path: `importer/tag-importer-common`, artifact ID `openfasttrace-importer-tag-importer-common`).
2. Move reusable parsing components from `openfasttrace-importer-tag` into the shared module (ID parsing helpers, metadata token parsing, line/region scanning primitives, validation helpers).
3. Adopt the shared module in `openfasttrace-importer-tag` without behavior changes (pure refactoring and compatibility verification).
4. Implement GH-562 and GH-563 feature-specific parser logic in their own PRs on top of the shared module.
5. For GH-563, implement Gherkin scenario parsing as a deterministic state machine over lines so metadata scope and scenario boundaries are explicit and testable.
6. Do not forward all Gherkin lines into tag-regex parsing; forward only comment lines (and only when the line shape can contain OFT directives).
7. Enforce single-pass streaming parsing: each file is read once line-by-line, without full-file buffering.
8. Keep memory usage bounded to current line plus minimal parser state/context needed for scenario and metadata scope handling.
9. Add strict validation and clear error messages for missing, multiple, duplicate, and malformed OFT IDs and metadata entries.
10. Prove compatibility via regression tests for legacy `.feature` coverage tags and non-feature source files.

## Concrete Refactoring Proposal

### New Shared Module

Create new module `importer/tag-importer-common` with artifact ID `openfasttrace-importer-tag-importer-common`.

Primary purpose:

* Provide reusable low-level parsing building blocks for importer implementations that parse OFT tags or OFT-like metadata in text files.
* Keep importer-specific behavior (tag importer vs. Gherkin importer) outside of this module.
* Support streaming importers that process files in one pass with bounded memory.

### Proposed Packages And Classes

Package `org.itsallcode.openfasttrace.importer.common.scan`

* `LineScanner`: reads an `InputFile` line-by-line and emits `(lineNumber, lineContent)` events.
* `LineHandler`: functional interface for line event consumers.
* `CompositeLineHandler`: delegates one line event to multiple handlers in deterministic order.
* `FilteringLineHandler`: delegates only if `LinePredicate` matches (used to avoid unnecessary downstream regex work).
* `LinePredicates`: reusable predicates like `isCommentLine()`, `startsWithAnyPrefix(...)`.
* `StreamingParserContext`: mutable bounded context holder for current parser state (current scenario header, collected tags/metadata for open scenario, line number), explicitly excluding full-file text storage.

Package `org.itsallcode.openfasttrace.importer.common.regex`

* `RegexLineHandler`: base class for handlers that detect repeated regex matches in a line and call `processMatch(matcher, lineNumber, lineMatchCount)`.
* Responsibility split from current tag importer code: regex scanning mechanics are shared, concrete match interpretation stays in importer-specific subclasses.

Package `org.itsallcode.openfasttrace.importer.common.oft`

* `CoverageListParser`: parses comma-separated specification item IDs and returns validated `List<SpecificationItemId>`.
* `NeededTypesParser`: parses comma-separated needed artifact types and returns normalized `List<String>`.
* `GherkinOftTagParser`: parses OFT ID tags in Gherkin syntax (`@id:<spec-item-id>`), validates exactly one ID token per scenario tag region.
* `MetadataDirectiveParser`: parses OFT directives in comments (`# Covers: ...`, `# Needs: ...`) and returns structured values.

Package `org.itsallcode.openfasttrace.importer.common.validation`

* `ImportValidationException`: common exception type for importer parsing/validation errors with location context.
* `ParserErrorMessages`: centralized message templates so tag importer and Gherkin importer report errors consistently.

### Extraction Mapping From Existing Code

Refactoring PR should extract or adapt the following existing pieces from `importer/tag` into the shared module:

* `LineReader` -> `LineScanner`
* `LineReader.LineConsumer` -> `LineHandler`
* `DelegatingLineConsumer` -> `CompositeLineHandler`
* `AbstractRegexLineConsumer` -> `RegexLineHandler`
* `LongTagImportingLineConsumer.parseCoveredIds(...)` logic -> `CoverageListParser`
* `LongTagImportingLineConsumer.parseNeededArtifactTypes(...)` logic -> `NeededTypesParser`

Keep these in `openfasttrace-importer-tag` (not shared):

* `LongTagImportingLineConsumer`
* `ShortTagImportingLineConsumer`
* `TagImporter`
* `TagImporterFactory`
* `ChecksumCalculator` (tag importer specific ID-generation detail)

### Responsibility Boundaries

Shared module responsibilities:

* Input scanning and regex match iteration mechanics.
* Generic OFT token and list parsing with validation and normalization.
* Reusable error abstraction and error message consistency.
* Preserve streaming semantics (single pass, bounded context state).

Importer module responsibilities:

* File type decisions and importer factory registration.
* Mapping parsed primitives to `ImportEventListener` events.
* Importer-specific semantics and state machines (tag grammar, Gherkin scenario boundaries).
* Routing optimization decisions, e.g. only forwarding comment lines to handlers that parse `Covers`/`Needs` directives.

### PR-1 Refactoring Acceptance Criteria

* `openfasttrace-importer-tag` compiles against `openfasttrace-importer-tag-importer-common`.
* Existing tag importer behavior is unchanged (prove through current unit tests).
* No new feature behavior is introduced in PR-1.
* Public API exposure is minimized to package-private where possible.
* Scanner and handler abstractions are usable in single-pass mode only; no API requires buffering complete file contents.

### PR-2 And PR-3 Follow-Up

* GH-562 PR: implement ticket-specific parsing behavior using shared module primitives.
* GH-563 PR: implement Gherkin scenario specification import using shared module primitives and dedicated scenario state machine.
* GH-563 PR: use `FilteringLineHandler` (or equivalent) so only comment lines are forwarded to OFT metadata/tag regex handlers.

## Task List

- [ ] Create and checkout branch `feature/563_support_gherkin_feature_specification_documents`

### PR Split Proposal

- [ ] Create a dedicated refactoring PR (separate from GH-563 and GH-562) that introduces `importer/tag-importer-common` (`openfasttrace-importer-tag-importer-common`) as a reusable parsing module.
- [ ] Limit the refactoring PR to code moves/extractions plus compatibility tests, with no functional behavior changes.
- [ ] Make both follow-up tickets depend on the refactoring PR:
	- GH-562 consumes the shared parsing module.
	- GH-563 consumes the shared parsing module.
- [ ] Merge the refactoring PR first, then continue with GH-562 and GH-563 feature PRs.

### Requirements And Design

- [ ] Update [doc/spec/system_requirements.md](../spec/system_requirements.md) with additive requirements for importing OFT specification items from Gherkin `Scenario` and `Scenario Outline`.
- [ ] Add explicit backward-compatibility requirement: existing `.feature` coverage-tag imports remain unchanged.
- [ ] Add validation requirements for missing ID, invalid ID, duplicate IDs, multiple IDs per scenario, malformed `# Covers:`, and malformed `# Needs:`.
- [ ] Stop and ask user for review of the updated system requirements.
- [ ] Update [doc/spec/design.md](../spec/design.md) with runtime design for the `.feature` parser flow, metadata scope boundaries, and error handling behavior.
- [ ] Add or update `dsn` items that cover the new and changed requirements.
- [ ] Stop and ask user for review of the updated design.

### Implementation

- [ ] Add `importer/tag-importer-common` module with reusable parser building blocks and register it in the multi-module Maven build.
- [ ] Add module dependency from `openfasttrace-importer-tag` to `openfasttrace-importer-tag-importer-common`.
- [ ] Add module dependency from the new Gherkin importer to `openfasttrace-importer-tag-importer-common`.
- [ ] Extract scanning infrastructure: move/adapt `LineReader` + nested consumer contract into `LineScanner`/`LineHandler`/`CompositeLineHandler` in the shared module.
- [ ] Extract regex scanning infrastructure: move/adapt `AbstractRegexLineConsumer` into shared `RegexLineHandler`.
- [ ] Extract reusable token parsers: move/adapt covered-ID and needed-types parsing into `CoverageListParser` and `NeededTypesParser`.
- [ ] Add shared parser support for Gherkin OFT tags and metadata directives (`GherkinOftTagParser`, `MetadataDirectiveParser`).
- [ ] In GH-563 implementation PR, add parser logic (using shared module components) to import specification items from Gherkin `Scenario` and `Scenario Outline` blocks.
- [ ] Restrict GH-563 specification-item import logic to `.feature` input while preserving existing tag parsing in all supported extensions.
- [ ] Add selective forwarding in GH-563 parser pipeline: only comment lines are passed to metadata/tag regex handlers.
- [ ] Ensure GH-563 parser pipeline remains single-pass: no second read over input, no buffering of full file content.
- [ ] Keep parser memory bounded to current line and minimal scenario/metadata state required to emit `ImportEventListener` events.
- [ ] Parse scenario title as specification item title and scenario steps as description.
- [ ] Parse metadata region between OFT ID tag and next boundary (next OFT ID tag, next scenario header, next feature header, or end of file).
- [ ] Keep non-OFT Gherkin tags and unrelated comments ignored.
- [ ] Keep old full/short coverage tag support behavior unchanged.

### Verification

- [ ] In the refactoring PR, run importer/tag unit tests as regression proof of no behavior changes.
- [ ] Add unit tests in `importer/tag` for valid `.feature` scenarios with `@id:`, `# Covers:`, and `# Needs:`.
- [ ] Add unit tests in `importer/tag` for invalid `.feature` inputs (missing/multiple/invalid IDs, malformed metadata, duplicate IDs).
- [ ] Add regression tests proving legacy coverage tag imports still behave unchanged in `.feature` files and non-`.feature` files.
- [ ] Add parser-pipeline tests proving non-comment lines are not forwarded to metadata/tag regex handlers.
- [ ] Add parser-pipeline tests proving one-pass behavior (line scanner invoked once, no re-read) and no full-file buffering.
- [ ] Add integration test coverage in `product` for mixed input artifacts that include both legacy tags and new Gherkin specification syntax.
- [ ] Run `./oft-self-trace.sh` and ensure trace stays clean.
- [ ] Run `mvn -T 1C verify` and ensure all quality gates pass.

### Documentation

- [ ] Extend [doc/user_guide.md](../user_guide.md) with the `.feature` specification syntax and examples.
- [ ] Update [.agents/skills/openfasttrace/SKILL.md](../../.agents/skills/openfasttrace/SKILL.md) to document the new `.feature` syntax (`@id:...`, `# Covers:`, `# Needs:`), boundaries, and backward-compatibility expectations.
- [ ] Add examples that show traceability links between Gherkin scenarios and design/implementation/test artifacts.

### Version And Changelog

- [ ] Add a changelog entry in [doc/changes/changes.md](../changes/changes.md) for GH-563.
