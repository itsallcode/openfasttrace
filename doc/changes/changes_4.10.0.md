# OpenFastTrace 4.10.0, released 2026-08-??

Code name: Lifecycle Information and SPDX3 SBOM

## Summary

The project lifecycle documentation now describes planned deprecations and removals in OFT 5.0.0, including migration guidance for the SpecObject format and short coverage tags.

Each release now includes an SPDX 3 SBOM for the product JAR and a SHA-256 checksum for the SBOM.

## Feature

* #542: CI and releases now provide an SPDX 3 SBOM.

## Bugfixes

* #582: Fixed the Markdown importer silently dropping all specification items after a fenced code block that directly follows a section title.

## Documentation

* #579: Documented planned deprecations and removals.
