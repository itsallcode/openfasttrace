# OpenFastTrace 3.6.0, released 2022-08-21

Code name: C# and Robot Framework support in `TagImporter`

## Summary

In this release we added support for C# (`.cs`) and Robot Framework files (`.robot`) in the `TagImporter`.

We now also upload a self-tracing report as part of the CI build and added support for Java 9 modules by outfitting all submodules with `module-info.java` files.

## Features

* #302: Added support for Robot Framework with postfix `.robot`
* #326: Added support for C# files with postfix `.cs`

## Refactoring

* #246: Upload self-tracing report to GitHub Action result in CI build
* #329: Fixed static code analysis warnings in unit tests
* #330: upgraded test and build dependencies
* #331: Removed license headers from sources and `license-maven-plugin`
* #334: Added `module-info.java` files to support Java modules
