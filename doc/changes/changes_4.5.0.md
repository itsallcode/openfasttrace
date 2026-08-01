# OpenFastTrace 4.5.0, released 2026-06-01

Code name: 10 years of OFT

## Summary

OpenFastTrace just turned ten on GitHub. We are on Mastodon now: https://mastodon.social/@OpenFastTrace

In this release we added the option `-h` / `--help` to the command line. Also, the help message now prints the version of OpenFastTrace.

We also refactored the tests around the CLI starter to improve readability and maintainability and made getting the test coverage easier.

We added FXML to the list of supported file formats for the tag importer.

File extensions can now be handled by multiple importers. Importers have a priority order, the first importer that can handle a file extension will be used. Since the XML importer has a peek function to detect SpecObject files, it can decide not to handle an XML file, in which case the tag importer can take over.

When no importer factory is found for a given file format, the importer factory loader will now throw an exception that explains the likely cause. This is helpful in case OFT is used as a library with a custom classloader and thus does not have the right context.

Last but not least, we added the AGENTS.md file as the central entry point to guidance for LLM agents.

## Features

* #352: Tag parsing in XML documents
* #503: Added `-h` / `--help` to the command line.
* #506: Exception when no importer was found.
* #524: Added version number to help text.
* #533: Added AGENTS.md
