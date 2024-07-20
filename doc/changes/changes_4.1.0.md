# OpenFastTrace 4.1.0, released 2024-06-??

Code name: Third-party plugins

## Summary

This release adds support for loading third-party plugins from external JAR files. See the documentation for details:

* [Installation](../plugins.md)
* [Plugin developer guide](../plugin_developer_guide.md)

The release also adds command line option `--log-level` that allows configuring the log level. Possible values are `OFF`, `SEVERE`, `WARNING`, `INFO`, `CONFIG`, `FINE`, `FINER`, `FINEST`, `ALL`. The default log level is `WARNING`.

## Features

* #413: Added support for third-party plugins
