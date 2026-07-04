# OpenFastTrace 4.6.0, released 2026-07-??

Code name: ??

We also updated test and build dependencies to fix vulnerabilities. Runtime code is not affected, so no update is required.

## Summary

We moved some GitHub action permissions from workflow-level to job-level.

## Security

* #556: Updated Junit, PlantUML, Jacoco Maven plugin and Central publishing Plugin dependencies to fix vulnerabilities

## Refactoring

* #552: Parameterized tests in `TestSpecobjectImporter`
* #546: Replaced `OsDetector` with JUnit5's `EnabledOnOs` annotation.
* #544: Replaced optional parameter with null check
* #543: Made `CliException` a `RuntimeException`
