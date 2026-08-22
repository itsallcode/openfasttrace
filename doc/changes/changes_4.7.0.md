# OpenFastTrace 4.7.0, released 2026-07-28

Code name: Gherkin Importer

## Summary

OpenFastTrace can now import traced specifications directly from Gherkin
`.feature` files. Annotate scenarios or scenario outlines with an OFT ID and
optional `Covers` and `Needs` metadata; existing coverage tags remain supported
when written in Gherkin comments.

Markdown and RST documentation can now also cover specification items with coverage tags in their native comments.

## New Features

* #563: Added Gherkin `.feature` specification import for annotated scenarios and scenario outlines.
* #562: Added coverage tag import from Markdown and RST comments.
