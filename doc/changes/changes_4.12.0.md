# OpenFastTrace 4.12.0, released 2026-10-??

Code name: Syntax usability improvements

## Summary

This release includes several usability improvements to the requirement ID and other syntax used in OpenFastTrace. For example, the hash mark (#) can now be used in addition to the tilde (~) as a revision separator. This prevents situations where the name part of the ID is displayed as cross-out text in Markdown. Of course, the previous double-tilde syntax remains valid.

## Features

* #638: Allow hash mark in addition to tilde as revision separator