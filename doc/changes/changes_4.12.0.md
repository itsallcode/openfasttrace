# OpenFastTrace 4.12.0, released 2026-10-??

Code name: Syntax usability and JSX and TSX tag importer

## Summary

This release includes several usability improvements to the requirement ID and other syntax used in OpenFastTrace. For example, the hash mark (#) can now be used in addition to the tilde (~) as a revision separator. This prevents situations where the name part of the ID is displayed as cross-out text in Markdown. Of course, the previous double-tilde syntax remains valid.

The source tag importer now supports JSX (`.jsx`) and TSX (`.tsx`) source files, the file types of React components written in JavaScript and TypeScript.

## Features

* #638: Allow hash mark in addition to tilde as revision separator
* #647: Add support for JSX and TSX in the source tag importer
