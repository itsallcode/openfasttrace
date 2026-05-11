# Quality Requirements

This chapter documents architecture-relevant quality requirements and technical quality goals.

User-facing acceptance scenarios are defined in [System Requirements](../system_requirements.md).

## Requirement Quality

Use this OFT hierarchy unless the project already defines a different one:

1. `feat`: top-level feature
2. `req`: user requirement
3. `scn`: Given-When-Then acceptance scenario
4. `constr`: architecture constraint
5. `dsn`: design requirement covering scenarios and constraints
6. `impl`: implementation
7. `utest`: unit test
8. `itest`: integration test

Runtime design requirements `dsn` should cover one scenario or constraint at a time. Use OFT forwarding notation if a design layer adds no new information.

## Code Quality

`<Summarize code quality rules inferred from contributing docs, linters, formatters, build settings, or code style.>`

## Test Quality

`<Summarize test framework, naming conventions, assertion style, coverage rules, and test data conventions.>`

## Dependency Policy

`<Summarize dependency constraints, vulnerability checks, lock files, verification metadata, allowed dependency categories, and approval rules.>`

## Static Analysis and Security Gates

`<Summarize linters, static analysis, security checks, CI gates, and release blockers.>`

## Testability and Coverage

`<Summarize coverage thresholds, test layers, integration test strategy, and areas that require manual verification.>`

## Open Issues

* `<quality-rule contradiction, missing gate, or unclear verification expectation>`
