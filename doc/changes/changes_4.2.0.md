# OpenFastTrace 4.2.0, released 2025-05-25

Code name: Markdown code blocks

## Summary

In this release we changed the behavior of the Markdown importer, so that if we are inside a code block, no OFT specification items are found. This is a corner case, but we think that this behavior is what users would expect (#480).

We also added a whole section about understanding and fixing broken links between specification items to the user guide.

The new token `oft:on|off` allows switching off OFT parsing for certain text passages in Markdown and RST documents.

The Central Repository changed its deployment mechanism. We adapted the project accordingly.

Finally, we updated test dependencies and Maven plugins.

## Features

* #437: Upgrade build and test dependencies on top of 4.1.0
* #480: Ignore specification items inside Markdown code blocks

## Documentation

* #427: Removed old `CHANGELOG.md` file and merged missing parts into release history.
* #431: Documented "unwanted coverage" in user guide.
* #449: Fix parsing past end of "needs" paragraph.
* #440: Added Tag importer support for TOML files.
* #442: Added support for javascript file extensions `.cjs`, `.mjs` and `.ejs`

## Refactoring

* #452: Migrated Deployment to new Central Repository mechanism
* #454: Fixed deployment to Maven Central
