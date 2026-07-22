# GH-563 Support Gherkin `.feature` Files As OpenFastTrace Specification Documents

## Goal

Allow OpenFastTrace to import specification items from Gherkin `Scenario` and
`Scenario Outline` blocks in `.feature` files. Preserve legacy coverage tags
when they are written in Gherkin comments, while avoiding coverage-tag regular
expression evaluation for executable Gherkin lines.

## Scope

In scope:

* Extract shared line scanning and long/short coverage-tag parsing into a
  prerequisite module.
* Add a dedicated Gherkin importer that composes the shared tag parser.
* Import OFT scenario items identified by `@id:<oft-id>`.
* Parse scoped `# Covers:` and `# Needs:` metadata strictly.
* Preserve comment-based legacy coverage tags in `.feature` files and all
  existing tag importer behavior for non-`.feature` inputs.
* Update traced requirements, design, tests, and user documentation.

Out of scope:

* Importing `Feature`, `Rule`, `Background`, or `Examples` as specification
  items.
* Supporting coverage tags in executable Gherkin lines.
* Adding an external Gherkin parser dependency.
* Moving Gherkin grammar or validation into the shared parser module.

## Design References

* [System Requirements](../spec/system_requirements.md)
* [Design](../spec/design.md)
* [Quality Requirements](../spec/design/quality_requirements.md)
* [User Guide](../user_guide.md)

## Strategy

1. Merge a behavior-preserving refactoring PR that introduces a shared module
   for line scanning and long/short coverage-tag parsing. It retains `.feature`
   support in the tag importer.
2. Atomically move `.feature` ownership from the tag importer to a new Gherkin
   importer with higher precedence when the Gherkin importer is registered.
3. Implement Gherkin parsing as a single-pass state machine. It receives every
   line but forwards only comment lines to the shared coverage-tag parser.
4. Keep all Gherkin syntax, state, validation, and `ImportEventListener`
   mapping in the Gherkin importer.

## Gherkin Syntax And Behavior

* A Gherkin scenario is an OFT item only when its immediately preceding,
  contiguous Gherkin tag region contains exactly one
  `@id:<SpecificationItemId>` tag. Other Gherkin tags are ignored.
* Directives are recognized only in `#` comment lines after that ID tag region
  and before the associated `Scenario:` or `Scenario Outline:` header.
  Comments elsewhere are ignored by Gherkin metadata parsing.
* `# Covers:` and `# Needs:` are case-sensitive. Each is optional but may occur
  at most once. When present, it must contain a non-empty comma-separated list;
  duplicate values and malformed IDs or artifact types are errors.
* A repeated or invalid `@id` tag, or a scoped directive without exactly one
  valid ID, fails the import with an `ImporterException` containing the file,
  line, and reason. Scenarios without OFT metadata remain ignored.
* The scenario header line is the item location. Text after `Scenario:` or
  `Scenario Outline:` is the title. The importer streams the scenario-step
  block into the description, excluding comments and `Examples`; it ends the
  item at the next `Scenario`, `Scenario Outline`, `Feature`, `Rule`,
  `Background`, `Examples`, or end of file.
* The importer retains only active metadata and previously imported Gherkin IDs
  for duplicate detection. It does not buffer a complete file or description.

## Task List

- [ ] Create and checkout branch
      `feature/563_support_gherkin_feature_specification_documents`.

### PR 1: Shared Coverage-Tag Parser Refactoring

- [ ] Add `importer/tag-importer-common` with artifact ID
      `openfasttrace-importer-tag-importer-common` to the Maven reactor.
- [ ] Add JPMS module `org.itsallcode.openfasttrace.importer.tag.common` and
      export only `LineReader` (including its line-consumer contract) and
      `CoverageTagParser` from
      `org.itsallcode.openfasttrace.importer.tag.common`.
- [ ] Move line scanning, line-handler composition, regex matching, long/short
      coverage-tag parsing, and CRC32 ID generation from `importer/tag` into
      the shared module without changing parsing semantics, generated IDs,
      listener events, logging, or exception wrapping.
- [ ] Define `CoverageTagParser.create(PathConfig, InputFile,
      ImportEventListener)` to compose the long-tag parser and, when a path
      configuration is present, the short-tag parser into one line consumer.
      Keep parser implementation classes encapsulated.
- [ ] Refactor `openfasttrace-importer-tag` into a thin adapter that creates
      the shared parser and scans its input once with the shared `LineReader`.
      Keep all supported extensions, including `.feature`, unchanged in this
      refactoring PR.
- [ ] Move scanner tests to the shared module and add focused shared-parser
      tests for representative long and configured short tags, asserting
      listener events, locations, generated IDs, coverage links, and needed
      artifact types.
- [ ] Keep the existing tag-importer parsing and factory/configuration tests as
      regression tests, including `.feature` support, to prove that the
      refactoring is behavior-preserving.

### Requirements And Design

- [ ] Add requirements for importing Gherkin scenarios and outlines, strict
      scoped metadata validation, Gherkin importer selection, and comment-only
      legacy coverage-tag compatibility.
- [ ] Stop and ask user for review of the updated system requirements.
- [ ] Add design items for factory precedence, the streaming Gherkin state
      machine, metadata scope, event mapping, and shared-parser delegation.
- [ ] Stop and ask user for review of the updated design.

### PR 2: Gherkin Importer

- [ ] Add `importer/gherkin` with artifact ID
      `openfasttrace-importer-gherkin`; register it in the Maven reactor and
      product dependencies.
- [ ] Provide a Gherkin importer factory for `.feature` files with priority
      `9000`, ahead of the tag importer's priority `10000`.
- [ ] Implement the defined single-pass Gherkin state machine and map imported
      scenario fragments to `ImportEventListener` events.
- [ ] Inject the shared coverage-tag parser and forward only lines whose
      trimmed form starts with `#` to it.
- [ ] Implement the specified `@id:`, `Covers`, `Needs`, title, description,
      boundary, duplicate-ID, and error behavior.
- [ ] Preserve the shared parser's existing `ImporterException` behavior for
      legacy coverage tags; do not introduce a new shared validation exception.

### Verification

- [ ] Add Gherkin importer unit tests for valid scenarios and outlines,
      location/title/description extraction, coverage metadata, non-OFT tags,
      and ignored ordinary scenarios.
- [ ] Add validation tests for invalid or multiple IDs, orphan directives,
      repeated or empty directives, malformed list entries, duplicate metadata
      values, and duplicate Gherkin IDs. Assert exception type and relevant
      message content.
- [ ] Add regression tests proving comment coverage tags import in `.feature`
      files, non-comment coverage-tag text is ignored in `.feature` files, and
      tag importer behavior for non-`.feature` inputs is unchanged.
- [ ] Add pipeline tests proving each Gherkin file is scanned once and only
      comment lines reach the shared coverage-tag parser.
- [ ] Add product-level tests for Gherkin importer precedence and mixed
      scenario specifications with comment-based legacy coverage tags.
- [ ] Run `./oft-self-trace.sh` and ensure the trace stays clean.
- [ ] Run `mvn -T 1C verify` and ensure all quality gates pass.

### Documentation And Changelog

- [ ] Extend [doc/user_guide.md](../user_guide.md) with the `.feature` syntax,
      placement rules, validation behavior, and examples.
- [ ] Update [.agents/skills/openfasttrace/SKILL.md](../../.agents/skills/openfasttrace/SKILL.md)
      with the Gherkin syntax and comment-only compatibility rule.
- [ ] Add the GH-563 entry to [doc/changes/changes_4.6.0.md](../changes/changes_4.6.0.md).
