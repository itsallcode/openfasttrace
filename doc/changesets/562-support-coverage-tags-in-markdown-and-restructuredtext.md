# GH-562 Support Coverage Tags in Markdown and reStructuredText

## Goal

Allow Markdown (`.md`, `.markdown`) and reStructuredText (`.rst`)
documentation to define full and configured short OpenFastTrace coverage tags.
Documentation artifacts such as user guides, user manuals, and operator manuals
can therefore explicitly cover specification items while their dedicated
importers continue to parse ordinary specification items.

## Scope

In scope:

* Import existing full and configured short coverage tags from Markdown and RST
  inputs.
* Support documentation artifacts such as user guides, user manuals, and
  operator manuals as explicit coverage of specification items.
* Reuse the shared coverage-tag parser without changing its parsing semantics.
* Preserve the dedicated importer selection and existing markup parsing.
* Update traced specifications, user documentation, and the 4.7.0 changelog.

Out of scope:

* Changing importer priorities or routing Markdown/RST files through the Tag
  Importer.
* Adding third-party dependencies or a public API.
* Multi-line or inline Markdown HTML comments, multi-line RST comments, and
  RST directives. Coverage tags are supported only in single-line native
  comments.

## Design References

* [System Requirements](../spec/system_requirements.md)
* [Design](../spec/design.md)
* [Quality Requirements](../spec/design/quality_requirements.md)
* [User Guide](../user_guide.md)

## Strategy

The recently introduced `CoverageTagParser` already contains the full- and
short-tag semantics and creates complete specification items through the atomic
`ImportEventListener.addSpecificationItem()` API. Add it to the shared
lightweight-markup importer and pass it the first matching `PathConfig` from
the dedicated Markdown or RST importer factory, using the same configuration
order as the Tag Importer. The common base owns parser construction, while each
format identifies its native, single-line comment candidates: Markdown forwards
only lines consisting of optional whitespace and a complete `<!-- ... -->`
comment; RST forwards only lines beginning with optional whitespace, `..`, and
whitespace that are not directives. Multi-line and inline HTML comments,
multi-line RST comments, and RST directives are not coverage-tag candidates.
Factories obtain configuration from their initialized
`ImporterContext`; tests that create the Markdown or RST factories must
initialize that context. The current markup state machine continues to process
every line. This keeps coverage-tag recognition comment-safe without duplicating
the shared parsing behavior or changing file-selection priorities.

## Task List

- [ ] Confirm the branch name follows the project convention:
      `feature/562_support_coverage_tags_in_markdown_and_restructuredtext`.

### Requirements And Design

- [ ] Extend `feat~coverage-tag-import~1`,
      `req~import.full-coverage-tag-format~1`, and
      `req~import.short-coverage-tag-format~1` in
      `doc/spec/system_requirements.md` so they cover supported documentation
      files as well as source files.
- [ ] Add one Markdown and one RST acceptance scenario. Each scenario must
      require import of full and configured short tags embedded in single-line
      native comments as a documentation artifact at the tag-line location
      while ordinary markup specification items remain unchanged; tag-shaped
      non-comment text must not create coverage items.
- [ ] Add one runtime `dsn` item per scenario in `doc/spec/design.md`. Specify
      delegation from lightweight-markup line processing to the shared parser
      with the matching path configuration only for the defined single-line
      native comment candidates; retain dedicated importer selection and
      existing state-machine processing.
- [ ] Review the requirements and design changes before implementation.

### Implementation

- [ ] Add `openfasttrace-importer-tag-importer-common` as a direct dependency
      of `openfasttrace-importer-lightweightmarkup` and add its JPMS module
      requirement.
- [ ] In the Markdown and RST importer factories, select the first matching
      `PathConfig` from `ImportSettings` in configured order, matching the Tag
      Importer's precedence, and pass it to their importer instances. A missing
      matching configuration must retain full-tag support and omit short-tag
      parsing. Obtain the settings from the initialized `ImporterContext` and
      initialize those factories in direct importer tests.
- [ ] In `AbstractLightWeightMarkupImporter`, create the shared parser with
      the supplied `PathConfig` and provide a format-specific seam for deciding
      whether a line is a coverage-tag comment candidate.
- [ ] In `MarkdownImporter`, forward only lines containing a complete,
      single-line HTML comment (`<!-- ... -->`) apart from optional surrounding
      whitespace; in
      `RestructuredTextImporter`, forward only lines starting with optional
      whitespace, `..`, and whitespace that do not use RST directive syntax.
      Do not treat multiline or inline comments, or RST directives, as
      candidates. Continue to run the existing markup state machine for all
      input lines.
- [ ] Retain the existing full-tag behavior for generated IDs, explicit names
      and revisions, multiple covered IDs, needed artifact types, source
      locations, malformed tags, and listener events. Retain existing
      configuration-driven short-tag semantics; do not expose a new API or
      alter the Tag Importer.
- [ ] Add narrow `impl` coverage markers to the shared integration point for
      both new design items.

### Verification

- [ ] Add Markdown and RST importer tests for a `doc` artifact covering a
      requirement, including the tag-line location and unchanged ordinary item
      parsing.
- [ ] Add negative tests proving tag-shaped text outside native Markdown or RST
      comments is not forwarded to the tag parser and produces no coverage item.
      Cover unsupported multi-line/inline Markdown and multi-line RST comment
      forms, plus RST directives containing tag-shaped text.
- [ ] Add parameterized or focused tests for full-tag variants: multiple
      covered IDs, explicit revision, explicit name and revision, needed
      coverage, and malformed tags that produce no items.
- [ ] Add Markdown and RST factory/importer tests for matching and non-matching
      `PathConfig` values, first-match precedence, configured short-tag import,
      unchanged full-tag import when no configuration matches, and initialized
      factory contexts in direct importer tests.
- [ ] Add product-level integration tests for `.md`, `.markdown`, and `.rst`
      inputs to prove their dedicated importers produce the coverage items
      ahead of the Tag Importer.
- [ ] Add `utest` and `itest` coverage markers at the narrowest applicable
      test scope.
- [ ] Run focused module tests, `./oft-self-trace.sh`, and `mvn -T 1C verify`.

### Documentation And Changelog

- [ ] Extend `doc/user_guide.md` with comment-safe Markdown and RST examples,
      such as `<!-- [doc->req~user-guide~1] -->` and
      `.. [doc->req~user-guide~1]`, plus configured short-tag examples and the
      applicable path-configuration requirement.
- [ ] Update `doc/changes/changes_4.7.0.md` with a GH-562 feature entry and
      include the documentation coverage-tag capability in its summary.
