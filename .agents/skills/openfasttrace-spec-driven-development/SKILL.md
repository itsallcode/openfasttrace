---
name: oft-spec-driven-development
description: Support spec-driven development in projects that use OpenFastTrace, a system-requirements document in `doc/system_requirements.md`, an arc42-style design in `doc/design.md`, and per-issue task plans in `doc/changesets/`. Use when AI agent must turn a new GitHub issue or Jira ticket into a changeset plan, update traced requirements and design before or alongside code, and keep all implementation and verification work aligned with the project's quality requirements.
---

# OFT Spec-Driven Development

Work from specification to implementation. Treat the requirements, design, and quality documents as the source of truth for both planning and changes.

## Project Contract

Use these repository conventions unless the user explicitly says this project differs:

- System requirements live in `doc/system_requirements.md` and use OpenFastTrace notation.
- Design lives in `doc/design.md` and its referenced arc42 chapters under `doc/design/`.
- Per-issue implementation plans live in `doc/changesets/` using `<issue-number>-<short-kebab-title>.md`.
- Quality requirements are authoritative for code, tests, tracing, and build gates. See `doc/design/quality_requirements.md` or `doc/spec/design/quality_requirements.md`.

## OFT Reference

For OFT syntax, tracing behavior, selective tracing, and common error handling, read the upstream OpenFastTrace skill first:

`https://raw.githubusercontent.com/itsallcode/openfasttrace/refs/heads/main/skills/openfasttrace-skill/SKILL.md`

Do not restate OFT rules from memory when the upstream reference is available. Use it as the normative workflow for:

- specification item syntax and IDs
- `Needs`, `Covers`, forwarding, and coverage tags
- selective tracing and trace interpretation
- diagnosing broken trace links

## Operating Mode

When the user gives you a new issue link, derive the work plan from:

1. the issue or ticket itself
2. `doc/system_requirements.md`
3. `doc/design.md` and the relevant linked design chapters
4. the quality requirements document
5. the current code and tests

Do not jump straight to code. First determine whether the issue changes:

- user-visible behavior
- traced requirements or scenarios
- design decisions, building blocks, runtime behavior, or constraints
- verification expectations from the quality requirements

If the ticket conflicts with the existing specification or design, surface that conflict and repair the documents before or together with implementation.

## Required Workflow

### 1. Read Before Planning

Before drafting or editing a changeset, inspect:

- `doc/system_requirements.md`
- `doc/design.md`
- the specific design chapters relevant to the issue
- `doc/design/quality_requirements.md`
- `doc/changesets/README.md`
- existing changesets that touch the same feature area

Read code only after you understand the spec and design impact.

### 2. Create Or Update The Changeset

For a new issue, create `doc/changesets/<issue-number>-<short-kebab-title>.md`.

Use the established repository style:

- title with tracker reference and short summary
- `Goal`
- `Scope` with explicit in-scope and out-of-scope bullets
- `Design References`
- optional `Strategy` when the issue needs implementation direction
- `Task List`

Keep the task list ordered. Put specification and design work before production-code work unless the issue is strictly internal and does not affect traced artifacts.

### 3. Derive The Plan From The Spec

When building the task list, map the issue onto the requirement hierarchy from the quality requirements:

- `feat`
- `req`
- `scn`
- `dsn`
- `impl`
- `utest` or `itest`

Use that mapping to decide what must change:

- If behavior changes for users, update or add `req` and `scn` items in `doc/system_requirements.md`.
- If architecture or technical behavior changes, update or add `dsn` items and relevant arc42 design sections.
- If a lower layer adds no new information, prefer OFT forwarding instead of redundant new items.
- Ensure each runtime design item covers only one scenario unless forwarding is the better fit.

### 4. Treat Quality Requirements As Non-Negotiable

Every plan and implementation must satisfy `doc/design/quality_requirements.md`.

Always derive verification tasks from that document, including as applicable:

- clean trace results for the requirement and design artifact types in scope
- tests that follow the naming, assertion, and parameter-validation rules
- coverage expectations
- plugin verification and build checks
- dependency-policy constraints
- static-analysis and security gates

If an issue required violating a documented quality rule, stop and raise it explicitly instead of silently proceeding.

### 5. Implement In Spec-First Order

Preferred order:

1. requirements updates
2. design updates
3. changeset task-plan update
4. production code
5. tests
6. OFT trace and project verification

For small internal refactors, you may keep the docs unchanged only when the traced requirements and design remain fully accurate.

## Changeset Template

Use this template and adapt it to the issue:

```md
# GH-<number> <title>

## Goal

<What the issue achieves and why it matters.>

## Scope

In scope:

* <planned change>

Out of scope:

* <explicit non-goal>

## Design References

* [System Requirements](<path-to-specs>/system_requirements.md)
* [Quality Requirements](<path-to-specs>/design/quality_requirements.md)
* [<Relevant design chapter>](<path-to-specs>/design/<chapter>.md)

## Strategy

<Only include when useful. Describe the intended implementation direction.>

## Task List

- [ ] Create and checkout a new Git branch `<issue-type>/<issue-number>-<issue-title-lower-kebab-case>`

### Requirements And Design

- [ ] <system requirements update>
- [ ] Stop and ask user for a review of the system requirements
- [ ] <design update>
- [ ] Stop and ask user for a review of the design

### Implementation

- [ ] <production code task>

### Verification

- [ ] <tests derived from quality requirements>
- [ ] Keep the OpenFastTrace trace clean
- [ ] Keep required build and plugin verification tasks green

### Update user documentation

- [ ] Update the end user documentation in `README.md` and / or `doc/user_guide/user_guide.md`

## Version and Changelog Update

- [ ] Raise the version to 0.3.0 (this is a feature release)
- [ ] Write the changelog entry for 0.3.0
```

Keep verification items concrete. Do not leave them as generic "run tests" placeholders when the quality requirements demand more specific checks.

## Issue Intake Rules

When the user provides a tracker link:

- extract the issue number for the changeset filename
- derive a short kebab-case title from the issue title
- summarize the requested outcome in repository terms
- identify impacted requirements, scenarios, design sections, code areas, and tests
- draft the changeset before substantial implementation

If the issue text is unavailable, ask the user for the issue content or a short summary. Do not invent requirements from the URL alone.

## Editing Rules

- Preserve existing OFT IDs unless the semantics changed.
- When semantics change, revise the item thoughtfully instead of making arbitrary ID churn.
- Keep requirements user-facing and design technical; do not duplicate the same text in both places.
- Prefer precise task items over broad epics in changesets.
- Keep comments in code minimal and let naming carry intent, consistent with the quality requirements.
- Avoid adding dependencies unless a documented design decision approves them.

## Verification Rules

Before closing work, verify at the level the issue changed:

- OFT artifacts stay syntactically valid and traceable.
- Requirement, scenario, design, implementation, and tests remain consistent.
- Automated tests cover the new behavior at the correct level.
- Repository quality gates required by `doc/design/quality_requirements.md` are either run or explicitly called out as not run.

If you cannot run a required verification step, state that clearly in the final report and leave the changeset task unchecked.
