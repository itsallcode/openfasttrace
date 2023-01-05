# OpenFastTrace 3.7.0, released 2023-01-04

Code name: Improved console report and Typescript support

## Summary

In this release we added support for JavaScript (`js.`) and TypeScript files (`.ts`) to the `TagImporter`.

The plain text report (aka "console report") got an overhaul to improve readability. We added optional color and font formatting output.
We also reworked how the tracing results are presented in order to make the report more intuitive.

The new change log now has one file per version to make maintaining and reading it easier.

## Features

* #338: Improved console report
* #351: Added support for importing tags from Terraform files, thanks to [g-psantos](https://github.com/g-psantos)!

## Bugfixes

* #308: Fixed running unit tests under macOS, Jacoco code coverage and integration tests
* #344: Added JavaScript (`js.`) and TypeScript files (`.ts`) to the `TagImporter`

## Refactoring

* #299: Added XML namespace support to SpecObject importer
* #336: Added `provides`  tags to `module-info.java` files required by the service loader
* #340: Added builds for macOS and Windows
* #341: Renamed `develop` branch to `main` and deleted `master`
* #342: Switched change log format to [Project Keeper](https://github.com/exasol/project-keeper) standard
* #344: Added support for Java 18