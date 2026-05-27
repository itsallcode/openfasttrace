# OpenFastTrace (OFT) Skill

OpenFastTrace is a tool for requirement tracing across various artifacts (specifications, code, tests).

## Core Concepts

- **Specification Items**: Normative pieces of specification or coverage markers.
- **Artifact Types**: Dynamic and not hard-coded. New types exist automatically when used in a document.
  - Common: `feat`, `req`, `arch`, `dsn`, `impl`, `utest`, `itest`, `stest`, `uman`, `oman`.
- **ID Syntax**: `type~name~revision` (e.g., `req~login-feature~1`).
  - `name`: Hierarchical with dots (e.g., `ui.button.save`).
  - `revision`: Integer used for versioning.
    - Incrementing the revision breaks all incoming links (coverage and dependencies).
    - This forces covering items to be updated and re-verified.
- **Keywords**:
  - `Covers: <ID>`: Current item implements/details the target ID.
  - `Needs: <types>`: Artifact types required to cover this item.
  - `Status: <status>`: `draft`, `proposed`, `approved`.
  - `Depends: <IDs>`: Defines dependencies (no effect on coverage).
  - `Description: <text>`: Optional keyword to start description.
  - `Rationale: <text>`, `Comment: <text>`.

## Syntax (Markdown)

```markdown
### Title
`req~id~1`
Description of the requirement.

Rationale: Why this is needed.

Covers: feat~parent~1

Needs: dsn, impl, utest
```

- **Forwarding**: `arch --> dsn : req~id~1` (delegates coverage without repeating).
- **Exclusion**: Use `<!-- oft:off -->` and `<!-- oft:on -->` to skip parsing.

## CLI Usage

General form: `oft <command> [options] <files/dirs>`

- **Commands**: `trace` (generate report), `convert` (export format), `help` (usage and version).
- **Options for `convert` and `trace`**:
  - `-o, --output-format`: `plain`, `html`, `aspec` (XML).
  - `-f, --output-file`: File path (default STDOUT).
  - `-a, --wanted-artifact-types`: Filter by type (Partial Tracing).
  - `-t, --wanted-tags`: Filter by tags (Partial Tracing). Use `_` for items without tags (e.g., `-t _,MyTag`).
  - `-v, --report-verbosity`: `quiet`, `minimal`, `summary`, `failures`, `failure_summaries`, `failure_details` (default), `all`.
  - `-i, --ignore-artifact-types`: Exclude types from import.

## Tracing

### Partial Tracing & Filtering

To trace only specific parts of the project:
- Filter by artifact types: `oft trace -a req,dsn <dir>`
- Filter by tags: `oft trace -t MyTag <dir>`
- Combine filters to focus on specific components or requirement levels.

### Build Framework Integration

OFT is typically integrated into CI builds via plugins:

- **Maven**: `openfasttrace-maven-plugin`
- **Gradle**: `openfasttrace-gradle`

## LLM Interaction Guidelines

- When identifying coverage, look for `impl~<ID>`, `utest~<ID>`, `itest~<ID>` and `stest~<ID>` in comments.
- Place markers at the narrowest possible scope (method/class).
- Ensure ID consistency across specifications and code.
- **Semantic Changes**: Increment the revision when the meaning of a requirement changes. This enforces a check of all covering items as their links become invalid.
- Verify changes by running tracing.

## Exit Codes

- `0`: Success.
- `1`: OFT error.
- `2`: Command line error.
