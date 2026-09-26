# OpenFastTrace 4.11.0, released 2026-XX-XX

Code name: Configurable Plugins

## Summary

OFT now allows embedders to pass plugin JARs to OFT at runtime through the Java API, so plugins
resolved through an external dependency mechanism no longer need to be installed in the
predefined plugin directory.

## Feature

* #610: Allow adding plugins via configuration using `Oft.builder().addPlugin(name, jars...)`
