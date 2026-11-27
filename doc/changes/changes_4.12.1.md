# OpenFastTrace 4.12.1, Unreleased on GitHub

Code name: Compressed Native Release Packages

## Summary

Native command-line executables in GitHub releases are now compressed for faster downloads and reduced storage use. Linux and macOS packages use `.tar.gz`; the Windows package uses `.zip`.
Each package contains the executable named `oft` and has a SHA-256 checksum for the compressed package.

## Features

* #656: Compress native binaries in GitHub releases
