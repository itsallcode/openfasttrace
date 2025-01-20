# OpenFastTrace 3.0.0, released 2019-04-21

Code name: Broader Tag Importer Support

## Summary

In this release we ported OFT to Java 11 which is a breaking change.

We also broadened the support of file types in the `TagImporter`.

## Features

* #238: Added more file suffixes to the `TagImporter`
* #239: Added file suffix for SQL to the `TagImporter`

## Refactoring

* #237: Ported to Java 11. Renamed Java packages so that they are unique for each module.
