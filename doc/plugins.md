# Extending OpenFastTrace With Plugins

Version 4.1.0 adds support for extending OFT with third-party plugins.

## Installing Plugins

You install a plugin by copying its JAR files to `$HOME/.oft/plugins/<plugin-name>/*.jar`. OFT will automatically load plugins from this location. To check which plugins are available, start OFT with command line argument `--log-level INFO`. This will log all available plugins and their location.

## Available Plugins

Currently no third-party plugins are available. If you want to add a new plugin, please create a [GitHub issue](https://github.com/itsallcode/openfasttrace/issues/new?assignees=&labels=&projects=&template=New_plugin.md).

| Plugin Name | Plugin Type | Description |
|-------------|-------------|-------------|
| N/A         | N/A         | Currently, no third-party plugins are available for OpenFastTrace. |
