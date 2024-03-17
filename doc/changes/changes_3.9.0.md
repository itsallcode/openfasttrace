# OpenFastTrace 3.9.0, released 2024-03-??

Code name: ???

## Summary

This release refactors the build to ensure that the built artifacts are reproducible. Building from the same Git commit will yield the same result for each build.

You can now specify an item name in coverage tags, e.g. `[impl~validate-password~2->dsn~validate-authentication-request~1]`.

## Features

* #402: Allow specifying item name in coverage tags

## Bugfixes

## Refactoring

* #399: Ensured that the build is reproducible
