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

## Tracing

Tracing can be performed via CLI, Maven, or Gradle.

### CLI Usage

General form: `oft <command> [options] <files/dirs>`

- **Commands**: `trace` (generate report), `convert` (export format), `help` (usage and version).
- **Options for `convert` and `trace`**:
  - `-o, --output-format`: `plain`, `html`, `aspec` (XML).
  - `-f, --output-file`: File path (default STDOUT).
  - `-a, --wanted-artifact-types`: Filter by type (Partial Tracing).
  - `-t, --wanted-tags`: Filter by tags (Partial Tracing). Use `_` for items without tags (e.g., `-t _,MyTag`).
  - `-v, --report-verbosity`: `quiet`, `minimal`, `summary`, `failures`, `failure_summaries`, `failure_details` (default), `all`.
  - `-i, --ignore-artifact-types`: Exclude types from import.

### Maven Integration

- **User Guide**: [openfasttrace-maven-plugin](https://github.com/itsallcode/openfasttrace-maven-plugin)

Add the `openfasttrace-maven-plugin` to your `pom.xml`:

```xml
<plugin>
    <groupId>org.itsallcode.openfasttrace</groupId>
    <artifactId>openfasttrace-maven-plugin</artifactId>
    <version>VERSION</version>
    <executions>
        <execution>
            <goals><goal>trace</goal></goals>
        </execution>
    </executions>
    <configuration>
        <reportFormat>html</reportFormat>
        <reportFile>target/site/tracing.html</reportFile>
    </configuration>
</plugin>
```

- **Run**: `mvn openfasttrace:trace`

### Gradle Integration

- **User Guide**: [openfasttrace-gradle](https://github.com/itsallcode/openfasttrace-gradle)

Apply the plugin in `build.gradle`:

```gradle
plugins {
    id "org.itsallcode.openfasttrace" version "VERSION"
}

openfasttrace {
    reportFormat = "html"
}
```

- **Run**: `gradle trace`

### Partial Tracing & Filtering

Partial tracing allows teams to focus on specific layers of the traceability chain, reducing noise and build time.

**Example Scenario:**
- **Product Owner (PO)**: Writes system requirements (`req`). Traces `feat` → `req` to ensure all features are specified.
- **Architect**: Writes design specifications (`dsn`). Traces `feat` + `req` → `dsn` to ensure requirements are architecturally covered.
- **Developer**: Writes implementation (`impl`) and tests (`utest`). Traces `feat`+ … + `dsn` → `impl`, `utest` to verify complete implementation and testing of the design.

```text
[PO] --(feat)--> [req]
                   |
[Architect] -------+--(feat, req)--> [dsn]
                                       |
[Developer] ---------------------------+--(feat, ..., dsn)--> [impl], [utest]
```

**Usage:**
- Filter by artifact types: `oft trace -a req,dsn <dir>`
- Filter by tags: `oft trace -t MyTag <dir>`
- Combine filters to focus on specific components or requirement levels.

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
