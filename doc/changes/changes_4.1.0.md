# OpenFastTrace 4.1.0, released 2024-08-11

Code name: Third-party plugins

## Summary

This release adds support for loading third-party plugins from external JAR files. See the documentation for details:

* [Installation](../plugins.md)
* [Plugin developer guide](../plugin_developer_guide.md)

The release also adds command line option `--log-level` that allows configuring the log level. Possible values are `OFF`, `SEVERE`, `WARNING`, `INFO`, `CONFIG`, `FINE`, `FINER`, `FINEST`, `ALL`. The default log level is `WARNING`.

The release also adds support for using [Gherkin](https://cucumber.io/docs/gherkin/) `feature` files with OFT, thanks to [@sophokles73](https://github.com/sophokles73) for his contribution!

## Features

* #413: Added support for third-party plugins
* #425: Add support for reading Tags from Gherkin feature files ([@sophokles73](https://github.com/sophokles73))
