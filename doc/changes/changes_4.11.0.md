# OpenFastTrace 4.11.0, released 2026-10-??

Code name: Configurable Plugins

## Summary

OFT now allows embedders to pass plugin JARs to OFT at runtime through the Java API, so plugins
resolved through an external dependency mechanism no longer need to be installed in the
predefined plugin directory.

The source tag importer now supports Visual Basic (`.vb`) source files.

The user guide documentation now has automatic lateral navigation.

## Features

* #610: Allow adding plugins via configuration using `Oft.builder().addPlugin(name, jars...)`
* #612: Add support for Visual Basic in the source tag importer

## Documentation

* #604: Fixed link to Product Lifecycle in SECURITY.md
* #605: Added automatic lateral navigation to the user guide documentation
* #606: Fixed typo in AGENTS.md for "functions"
* #608: Fixed formatting and updated links in README.md
* #614: Fixed markdown syntax in the AI agent skill
