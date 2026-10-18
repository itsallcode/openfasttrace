---
layout: default
title: macOS Installation
parent: Installation
grand_parent: User Guide
nav_order: 2
---

## macOS Installation

OpenFastTrace can be installed on macOS using Homebrew, JBang or manually via a JAR file.

### Homebrew

If you use Homebrew, you can install OpenFastTrace directly:

```bash
brew install openfasttrace
```

### JBang

[JBang](https://www.jbang.dev/) runs OpenFastTrace straight from Maven Central and downloads a suitable Java runtime if none is installed.

1.  Install JBang (see the [JBang installation guide](https://www.jbang.dev/documentation/jbang/latest/installation.html) for other options):
    ```bash
    curl -Ls https://sh.jbang.dev | bash -s - app setup
    ```
2.  Install OpenFastTrace as `oft` command:
    ```bash
    jbang app install oft@itsallcode/openfasttrace
    ```
3.  Run OFT:
    ```bash
    oft --help
    ```

To run OFT once without installing it, use `jbang oft@itsallcode/openfasttrace --help`.

### Manual Installation

If you prefer a manual setup, you can use the executable JAR file directly.

#### 1. Install Java

OpenFastTrace requires **Java 17 or newer**. On macOS, you can install it using Homebrew or download it from a provider like Adoptium.

*   **Homebrew**: `brew install openjdk@17`
*   **Manual Download**: Visit [Adoptium (Temurin)](https://adoptium.net/) and download the macOS installer (.pkg or .dmg) for Java 17 or newer.

#### 2. Download OFT

Download the `openfasttrace-<version>.jar` from the [GitHub Releases](https://github.com/itsallcode/openfasttrace/releases) page. Please check for the latest available version there.

#### 3. Run OFT

You can now run OFT using:
```bash
java -jar path/to/openfasttrace-<version>.jar --help
```

#### 4. Creating a command alias (Optional)

To make it easier to run, add an alias to your `.zshrc` (or `.bash_profile`):
```bash
alias oft='java -jar /path/to/openfasttrace-<version>.jar'
```
