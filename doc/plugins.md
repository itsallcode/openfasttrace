---
layout: default
title: Plugins
nav_order: 6
---

# Extending OpenFastTrace With Plugins

OpenFastTrace (OFT) is designed to be extensible. You can add new importers, exporters, and reporters by using plugins.

## Installation

OFT automatically loads plugins from JAR files located in a predefined location at startup.

To install a plugin, copy the plugin's JAR file to the following directory depending on your operating system:

* **Linux**: `$HOME/.oft/plugins/`
* **Windows**: `%APPDATA%/oft/plugins/`
* **macOS**: `$HOME/Library/Application Support/oft/plugins/`

After copying the JAR file, the plugin will be available the next time you run OFT.

## Configuring Plugins From Java

When you embed OFT in your own application and resolve plugins through your own dependency
mechanism, you can pass plugin JARs to OFT at runtime instead of installing them in the
predefined directory:

```java
final Oft oft = Oft.builder()
        .addPlugin("my-plugin", Path.of("my-plugin.jar"))
        .build();
```

Each `addPlugin(name, jars...)` call defines one named plugin. All JARs passed in one call are
loaded through the same separate ClassLoader, so a plugin can be passed together with its
dependencies. A plugin's JARs may contain multiple service providers; OFT loads all of them.
Configured plugins are loaded in addition to the plugins from the predefined directory.

## Developing Plugins

If you want to develop your own plugins for OFT, please refer to the [Plugin Developer Guide](developer_guide/plugin_developer_guide.md).
