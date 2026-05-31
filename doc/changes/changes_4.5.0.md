# OpenFastTrace 4.4.0, released 2026-05-??

Code name: ???

## Summary

In this release we added the option `-h` / `--help` to the command line. Also, the help message now prints the version of OpenFastTrace.

We also refactored the tests around the CLI starter to improve readability and maintainability and made getting the test coverage easier.

We added FXML to the list of supported file formats for the tag importer.

File extensions can now be handled by multiple importers. Importers have a priority order, the first importer that can handle a file extension will be used. Since the XML importer has a peek function to detect SpecObject files, it can decide not to handle an XML file, in which case the tag importer can take over.

When no importer factory is found for a given file format, the importer factory loader will now throw an exception that explains the likely cause. This is helpful in case OFT is used as a library with a custom classloader and thus does not have the right context.

## Features

* #352: Tag parsing in XML documents
* #503: Added `-h` / `--help` to the command line.
* #524: Added version number to help text.
* #506: Exception when no importer was found.
