# Extending OpenFastTrace With Plugins

OpenFastTrace (OFT) is designed to be extensible. You can add new importers, exporters, and reporters by using plugins.

## Installation

OFT automatically loads plugins from JAR files located in a predefined location at startup.

To install a plugin, copy the plugin's JAR file to the following directory depending on your operating system:

* **Linux**: `$HOME/.oft/plugins/`
* **Windows**: `%APPDATA%/oft/plugins/`
* **macOS**: `$HOME/Library/Application Support/oft/plugins/`

After copying the JAR file, the plugin will be available the next time you run OFT.

## Developing Plugins

If you want to develop your own plugins for OFT, please refer to the [Plugin Developer Guide](developer_guide/plugin_developer_guide.md).
