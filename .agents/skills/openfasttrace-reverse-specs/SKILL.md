---
name: openfasttrace-reverse-specs
description: Reverse-engineer missing or incomplete OpenFastTrace system requirements and arc42-style design documentation from a project's user guide, existing documentation, tests, and code. Use when the agent must draft or repair `doc/system_requirements.md`, `doc/design.md`, and `doc/design/` chapters; infer features, requirements, scenarios, and design items; align design coverage with requirements; and report contradictions or open issues found during reverse engineering.
---

# OpenFastTrace Reverse Specs

Reverse-engineer OFT specifications from existing project artifacts. Treat user-facing documentation as the primary source for intent and source code as evidence for implemented behavior and design.

Use the bundled templates when creating new documentation:

- `assets/system_requirements_template.md`
- `assets/design_index_template.md`
- `assets/design/*.md`

For OFT syntax and trace behavior, use the `openfasttrace-skill` if it is available in the session.

## Operating Rules

- DO NOT change any code. The only exception are coverage marker comments in [Step 3](#step-3-add-coverage-markers-in-implementation-and-tests).
- DO NOT change the documents from which you reverse engineer specification (user guide, README).
- Preserve existing project specification documents unless the user explicitly asks to replace them. If a target file exists, patch it carefully or create a clearly named draft beside it.
- Keep a source inventory while working. For every inferred requirement or design item, know whether it came from user guide, README, tests, source code, configuration, build files, issue text, or runtime behavior.
- Prefer explicit evidence over speculation. Mark weakly supported conclusions in `Open Issues`.
- Do not hide contradictions. Summarize them in the final answer and record them in the generated document's `Open Issues` section.
- Use stable OFT item IDs in lower-kebab-case unless the project already uses another convention.
- Use revision `~1` for newly inferred items unless replacing an existing item with a semantically incompatible version.
- Keep generated text in draft status when confidence is incomplete.

## Evidence Order

Inspect artifacts in this order:

1. User guide, manual, tutorial, screenshots, examples, CLI help, public API docs, README usage sections, and release notes.
2. Existing requirements, design, architecture decision records, issue plans, and trace files.
3. Tests, examples, fixtures, and golden files, especially acceptance, integration, CLI, API, and UI tests.
4. Public entry points in code: commands, controllers, UI actions, service APIs, plugin extension points, exported classes, configuration keys, and error messages.
5. Internal code structure, dependency declarations, build scripts, packaging, deployment manifests, and runtime integration points.

The user guide is closest to product intent. If code and user guide disagree, prefer neither silently. Record the contradiction and ask the user to decide.

In absence of a user guide, tests are the next best option to extract product intent.

## Preconditions

OpenFastTrace (OFT) should be available to be able to check the created specifications.

If it is not installed, download the JAR artifact from OFT's latest GitHub release (https://github.com/itsallcode/openfasttrace/releases) and use that for the trace.

Use `oft` in the commands below. If only the JAR is available, replace `oft` with `java -jar /path/to/openfasttrace.jar`.

## Trace Commands

Run traces with the narrowest artifact scope for the current reverse-engineering phase. The `-a` switch is a whitelist of artifact types included in the scan.

### System Requirements Only

Run this after Step 1 to check feature, requirement, and scenario links inside `doc/system_requirements.md`:

```bash
oft trace -a feat,req,scn doc/system_requirements.md
```

Expected result: feature, requirement, and scenario links are complete. Downstream design coverage is intentionally out of scope in this trace, even when scenarios declare `Needs: dsn`.

### System Requirements to Design

Run this after Step 2 to check coverage from system requirements down to design:

```bash
oft trace -a feat,req,scn,constr,dsn doc/system_requirements.md doc/design.md doc/design
```

Expected result: scenarios are covered by design items or OFT forwarding notation. Implementation and test coverage are intentionally out of scope.

### Full Project Coverage

Run this after Step 3 to check coverage from requirements through design, implementation, tests, and build markers:

```bash
oft trace .
```

Expected result: the full requirement network is covered across all project files.

## Decision Checkpoints

Use decision checkpoints to resolve open issues without derailing the reverse-engineering pass.

At the end of Step 1 and Step 2, summarize unresolved issues as a numbered list of concrete questions. For each question, include the conflicting or missing evidence, the decision needed, and a recommended default when the evidence supports one. If the user answers, update the document and remove resolved issues. If the user cannot answer or does not answer yet, keep the issue in `Open Issues` and keep the affected items in draft status.

Ask earlier only when a contradiction blocks writing a traceable draft.

## Step 1: Draft System Requirements

Create or update `doc/system_requirements.md` using `assets/system_requirements_template.md`.

Build the document in this order:

1. Write the introduction from the user guide and README, not from implementation structure.
2. Define terms and user roles before feature details.
3. Extract product-level `feat` items from user-visible capabilities. Extract features sparingly. The level of granularity should be what would be listed on a product flyer.
4. Refine each feature into `req` items that describe user-visible needs and constraints.
5. Add `scn` items as Given-When-Then acceptance scenarios.
6. Add `Needs` and `Covers` links:
   - `feat` items need `req`.
   - `req` items cover `feat` and need `scn`.
   - `scn` items cover `req` and need `dsn`.
7. Fill gaps from tests and code only after user-guide-derived intent is represented.
8. Record uncertain inferences, missing intent, duplicate behavior, and contradictions in `Open Issues`.
9. Run a [Decision Checkpoint](#decision-checkpoints) for open issues that affect system requirements.
10. Run the [System Requirements Only](#system-requirements-only) trace. Verify the trace is clean for the included artifact types.
11. After drafting system requirements, stop for user review unless the user explicitly requested a complete requirements-and-design reverse-engineering pass in one turn. Ask the user to remove the draft marker from requirements they reviewed and approved.
12. Report draft / total counts by artifact type and overall.

## Step 2: Draft Design

1. Create or update `doc/design.md` and `doc/design/` using:

   - `assets/design_index_template.md`
   - files under `assets/design/`

2. Derive design from code and tests, then match it against `doc/system_requirements.md`.

   Use this design structure:

   - Introduction and goals in `doc/design.md`
   - Architecture constraints
   - Context and scope
   - Solution strategy
   - Building block view
   - Runtime view
   - Deployment view
   - Crosscutting concepts
   - Architecture decisions
   - Quality requirements
   - Risks and technical debt
   - Glossary
   - Open issues

3. Add `dsn` items where design decisions or runtime behavior cover `scn` and `constr` items. Prefer one runtime `dsn` per scenario or constraint. If a design layer adds no information, use OFT forwarding notation rather than inventing redundant design text.
4. If the documents contain information about intentional technical constraints, document them as `constr` in section `Architecture constraints`. Each `constr` item needs `dsn` coverage, and at least one `dsn` item must cover it. For example the project documentation states that only builds for x86 are supplied. Try not to infer constraints from the code, because they might not be intentional.
5. Record contradictions under `Open Issues`, especially when:

   - a scenario from system requirements is not implemented by the observed design,
   - code implements behavior with no requirement,
   - design structure conflicts with user-facing intent,
   - tests assert behavior that differs from user-guide wording,
   - public configuration or API behavior is undocumented,
   - dependencies, persistence, network access, security, or deployment behavior contradict stated constraints.

6. Run a [Decision Checkpoint](#decision-checkpoints) for open issues that affect design or design coverage.
7. Run the [System Requirements to Design](#system-requirements-to-design) trace. Verify the trace is clean for the included artifact types.
8. After drafting the design, stop for user review unless the user explicitly requested a complete requirements-and-design reverse-engineering pass in one turn. Ask the user to remove the draft marker from requirements they reviewed and approved.
9. Report draft / total counts by artifact type and overall.

## Step 3: Add Coverage Markers in Implementation and Tests

Do not change code logic. In this step, only add OFT coverage markers as comments.

1. For each `dsn` item, read its `Needs` field and add exactly the requested downstream marker types:

   - `impl` for implementation code
   - `utest` for unit tests
   - `itest` for integration tests
   - `stest` for system tests
   - `bld` for build configuration

2. Use the OFT tag notation `[<covering-type>-><covered-item-id>]` in the host file's comment syntax. Examples:

   - Java, JavaScript, TypeScript, C, C++, C#, Go: `// [impl->dsn~drop-database-objects~1]`
   - Python, shell, YAML, TOML: `# [utest->dsn~drop-database-objects~1]`
   - SQL: `-- [itest->dsn~drop-database-objects~1]`

3. Place each marker directly above the smallest stable code element that implements or verifies the design item: method, function, class, test method, test class, build block, or configuration entry. Prefer narrow locations over file-level markers.
4. Do not add marker types that are not listed in the `dsn` item's `Needs`. If the observed project lacks the requested implementation or test evidence, do not invent coverage. Record the missing coverage in the design document's `Open Issues` section.
5. Run the [Full Project Coverage](#full-project-coverage) trace. Verify the trace is clean.

## Reverse-Engineering Checklist

Use `references/evidence_checklist.md` when the project is large or unfamiliar.

For each feature area, collect:

- intent source: guide, README, API docs, screenshots, examples, or release notes
- behavioral evidence: tests, examples, CLI output, UI actions, endpoint definitions, logs, errors
- design evidence: modules, packages, extension points, data flow, persistence, network calls, external systems
- verification evidence: unit tests, integration tests, acceptance tests, build gates
- confidence: high, medium, or low
- open issues: contradictions, missing source, unclear terminology, scope questions

## Output Discipline

When reporting back to the user:

- summarize created or changed files,
- list contradictions and open issues first if any exist,
- state which evidence sources were used,
- state whether the next step is user review or design extraction,
- mention if OFT tracing was not run.

Do not claim the reverse-engineered specification is complete. It is a structured draft until the user resolves intent and contradiction questions.
