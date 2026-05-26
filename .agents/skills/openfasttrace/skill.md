# OpenFastTrace (OFT) Skill

OpenFastTrace is a tool for requirement tracing across various artifacts (specifications, code, tests).

## Core Concepts

- **Specification Items**: Normative pieces of specification or coverage markers.
- **ID Syntax**: `type~name~revision` (e.g., `req~login-feature~1`).
  - `type`: `feat` (feature), `req` (requirement), `dsn` (design), `impl` (code), `utest`/`itest`/`stest` (tests).
  - `name`: Hierarchical with dots (e.g., `ui.button.save`).
  - `revision`: Integer (voids links if incremented).
- **Linking**:
  - `Covers: <ID>`: Current item implements/details the target ID.
  - `Needs: <types>`: Artifact types required to cover this item.

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
  - `-a, --wanted-artifact-types`: Filter by type.
  - `-t, --wanted-tags`: Filter by tags.

## Integration

OFT is typically integrated into CI builds via plugins:

- **Maven**: `openfasttrace-maven-plugin`
- **Gradle**: `openfasttrace-gradle`

## LLM Interaction Guidelines

- When identifying coverage, look for `impl~<ID>`, `utest~<ID>`, `itest~<ID>` and `stest~<ID>` in comments.
- Place markers at the narrowest possible scope (method/class).
- Ensure ID consistency across specifications and code.
