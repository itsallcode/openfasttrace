# OpenFastTrace 4.6.0, released 2026-07-??

Code name: Status Filter at Import

## Summary

We added a new feature to filter specification items by status at import time via `-w` or `--wanted-statuses` CLI parameters.
This is helpful when your project uses specification documents for planning future requirements (marked by the status "draft" or "porposed").

We also updated test and build dependencies to fix vulnerabilities. Runtime code is not affected, so no update is required.
 
We moved some GitHub action permissions from workflow-level to job-level. Sonar findings that accumulated with Sonar introducing new code rules were reduced a lot. Since OFT is used in safety-critical projects, code quality is crucial to us.
 
## New Features
 
* #519: Added support for filtering by specification item status.
 
## Security

* #556: Updated Junit, PlantUML, Jacoco Maven plugin and Central publishing Plugin dependencies to fix vulnerabilities

## Refactoring

* #554: Tag importer supports import from doxygen files
* #552: Parameterized tests in `TestSpecobjectImporter`
* #546: Replaced `OsDetector` with JUnit5's `EnabledOnOs` annotation.
* #544: Replaced optional parameter with null check
* #543: Made `CliException` a `RuntimeException`

## Features

* #553 Tag importer supports multiple covered IDs
