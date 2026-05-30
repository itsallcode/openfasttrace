# OpenFastTrace 4.4.0, released 2026-05-??

Code name: 10 years of OFT

## Summary

OpenFastTrace just turned ten counting from the first release. We are on Mastodon now: https://mastodon.social/@OpenFastTrace

In this release we added the option `-h` / `--help` to the command line. Also, the help message now prints the version of OpenFastTrace.

We also refactored the tests around the CLI starter to improve readability and maintainability and made getting the test coverage easier.

We added FXML to the list of supported file formats for the tag importer.

When no importer factory is found for a given file format, the importer factory loader will now throw an exception that explains the likely cause. This is helpful in case OFT is used as a library with a custom classloader and thus does not have the right context.

## Features

* #503: Added `-h` / `--help` to the command line.
* #506: Exception when no importer was found.
* #524: Added version number to help text.
