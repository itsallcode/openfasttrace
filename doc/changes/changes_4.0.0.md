# OpenFastTrace 4.0.0, released 2024-04-02

Code name: RST Importer

## Summary

Good news for our 🐍 Python friends: in this release we derived a parser for Restructured Text (RST) from our existing Markdown parser.

The Markdown parser in the process now accepts specification item titles underlined with either "=" (H1) or "-" (H2).

Also, the Markdown parser now ignores whitespace in `Needs` and `Tags` entries and correctly parses `Tags` at the start of a requirement item.

The test coverage for the Markdown importer is now at 100%, and we were able to remove tests that were done with mocks in favor or more robust low-level integration tests.

We also improved detection of requirement forwarding markup:

    currentArtifactType-->forwaredTo:originalArtifactType~name~revision

This now works directly after:

1. Markdown title
2. "Needs" section
3. "Depends" section
4. "Covers" section
5. "Tags" section

### Breaking Changes

### Java 17

We dropped support for Java 11 for a couple of reasons:

1. Oracle JDK does not support version 11 anymore
2. AdoptJDK will likely end support for version 11 in September 2024
3. Most machines now come preinstalled with 17 or later
4. Java 17 has a couple of features that allow for cleaner, safer and more readable code

### Removed Deprecated Elektrobit-proprietary Specification Item ID Format

Support for the previously deprecated Elektrobit-proprietary specification item ID format

    req:name, v1

has now been removed from the Markdown format. Please migrate to OFT's native format:

    req~name~1

## Features

* #378: Added an RST importer
* #412: Added automatic release for Maven Central and GitHub
