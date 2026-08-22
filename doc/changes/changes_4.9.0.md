# OpenFastTrace 4.9.0, released 2026-08-07

Code name: Item ID Location

## Summary

Importers for long coverage tags, Markdown and reStructuredText now collect the location of coverage IDs in documents and source code.

The exact location of an item ID is required for IDE plugins in order to support features like syntax highlighting, find occurrences, jump to definition and auto-complete. Adding this feature to OFT helps avoid code duplications and improves reliability.

**Deprecation Notes:** Users of the OFT API and OFT plugin authors please note the deprecation of the following methods:
* Interface `org.itsallcode.openfasttrace.api.importer.ImportEventListener`: methods
  * `void setId(SpecificationItemId id)`
  * `void addCoveredId(SpecificationItemId id)`
  * `void addDependsOnId(SpecificationItemId id)`

## New Features

* #570: Collect source code location of specification item IDs
