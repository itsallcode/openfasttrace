---
layout: default
title: Tools for Authoring OFT Documents
parent: Tool Support
grand_parent: User Guide
nav_order: 1
---

### Tools for Authoring OFT Documents

The following editors and integrated development environments are well suited for authoring OFT documents. The list is not exhaustive, any editor with Markdown capabilities can be used.

| Editor / IDE                                         | Syntax<br/>highlighting | Preview | Outline | HTML<br/>export | OFT<br/>Plugin |
|------------------------------------------------------|:-----------------------:|:-------:|:-------:|:---------------:|:--------------:|
| [CLion](https://www.jetbrains.com/clion/)            |            y            |    y    |    y    |        y        |       y        |
| [Gedit](https://wiki.gnome.org/Apps/Gedit)           |            y            |         |         |                 |                |
| [Eclipse](https://eclipse.org)                       |            y            |    y    |    y    |        y        |       y¹⁾      |
| [IntelliJ](https://www.jetbrains.com/idea/)          |            y            |    y    |    y    |        y        |       y        |
| [PyCharm](https://www.jetbrains.com/pycharm/)        |            y            |         |         |                 |       y        |
| [Vim](https://www.vim.org/)                          |            y            |         |         |                 |                |
| [Visual Studio Code](https://code.visualstudio.com/) |            y            |    y    |    y    |        y        |       y¹⁾      |

Please note that some IDEs may require additional plugins to support Markdown features.

¹⁾ Support via language server

#### JetBrains IDEs (CLion, PyCharm, IntelliJ, etc.)

We offer a plugin for the [JetBrains IDEs (CLion, PyCharm, IntelliJ, etc.)](https://github.com/itsallcode/openfasttrace-intellij-plugin)

Features include:

* Syntax highlighting for OFT specification item IDs
* Symbol search for OFT specification items
* Navigation between OFT specification items
* Templates for OFT specification items
* Run configurations for OFT traces
* In-IDE trace report

#### Third-Party Plugins

Check out the [OpenFastTrace Language Server](https://github.com/fgorke/openfasttrace-language-server) written by [@fgorke](https://github.com/fgorke).

A big thank-you to [@fgorke](https://github.com/fgorke) for developing the OpenFastTrace Language Server!

Unlike a specific plugin for a single IDE, language servers provide a standardized way to integrate language features across different IDEs (e.g., VS Code or the JetBrains IDEs).

* [User Guide](https://github.com/fgorke/openfasttrace-language-server/blob/main/doc/user-guide/README.md)
* [Demo](https://github.com/fgorke/openfasttrace-language-server/blob/main/doc/demo/README.md)
* [Releases](https://github.com/fgorke/openfasttrace-language-server/releases)

---

← [Tool Support](tool_support.md) &nbsp;&nbsp;•&nbsp;&nbsp; ↑ [Tool Support](tool_support.md) &nbsp;&nbsp;•&nbsp;&nbsp; [Templates for IDEs](templates_for_ides.md) →
