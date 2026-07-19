# OpenFastTrace 4.6.0, released 2026-07-19

Code name: Status Filter at Import

## Summary

We added a new feature to filter specification items by status at import time via `-w` or `--wanted-statuses` CLI parameters.
This is helpful when your project uses specification documents for planning future requirements (marked by the status "draft" or "porposed").

Big thanks to first-time contributor @Davidius86 who added a dark theme to OFT's HTML report. If your browser is set to a dark theme, the report will automatically switch. He alo added Doxygen file support to the Tag importer and added a convenience notation that lets you cover multiple specification items in a single tag:

```C++
// [impl -> dsn~a-covered-item~1, dsn~another~covered-item~2]
```

We also updated test and build dependencies to fix vulnerabilities. Runtime code is not affected, so no update is required.
 
We moved some GitHub action permissions from workflow-level to job-level. Sonar findings that accumulated with Sonar introducing new code rules were reduced a lot. Since OFT is used in safety-critical projects, code quality is crucial to us.
 
## New Features
 
* #519: Added support for filtering by specification item status.
 
## Security

* #556: Updated JUnit, PlantUML, Jacoco Maven plugin and Central publishing Plugin dependencies to fix vulnerabilities

## Refactoring

* #552: Parameterized tests in `TestSpecobjectImporter`
* #546: Replaced `OsDetector` with JUnit5's `EnabledOnOs` annotation.
* #544: Replaced optional parameter with null check
* #543: Made `CliException` a `RuntimeException`

## Features

* #554: Tag importer supports import from doxygen files
* #553: Tag importer supports multiple covered IDs
* #519: Filter imports by specification item status (draft, proposed, approved and rejected)
