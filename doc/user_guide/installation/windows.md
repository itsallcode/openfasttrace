---
layout: default
title: Windows Installation
parent: Installation
grand_parent: User Guide
nav_order: 3
---

## Windows Installation

On Windows, OpenFastTrace can be run via JBang or manually via the JAR file.

### JBang

[JBang](https://www.jbang.dev/) runs OpenFastTrace straight from Maven Central and downloads a suitable Java runtime if none is installed.

1.  Install JBang (see the [JBang installation guide](https://www.jbang.dev/documentation/jbang/latest/installation.html) for other options):
    ```powershell
    iex "& { $(iwr -useb https://ps.jbang.dev) } app setup"
    ```
2.  Install OpenFastTrace as `oft` command:
    ```powershell
    jbang app install oft@itsallcode/openfasttrace
    ```
3.  Run OFT:
    ```powershell
    oft --help
    ```

To run OFT once without installing it, use `jbang oft@itsallcode/openfasttrace --help`.

### Manual Installation

#### 1. Install Java

OpenFastTrace requires **Java 17 or newer**. On Windows, you can install it using `winget` or download an installer.

*   **winget**: `winget install eclipse.temurin.17.jre`
*   **Manual Download**: Visit [Adoptium (Temurin)](https://adoptium.net/) and download the Windows MSI installer for Java 17 or newer.

#### 2. Download OFT

Download the `openfasttrace-<version>.jar` from the [GitHub Releases](https://github.com/itsallcode/openfasttrace/releases) page. Please check for the latest available version there.

#### 3. Run OFT

You can now run OFT via PowerShell or Command Prompt:
```powershell
java -jar C:\path\to\openfasttrace-<version>.jar --help
```

#### 4. Creating a command alias (Optional)

To make it easier to run, create a batch file named `oft.bat` in a directory that is in your system `PATH` (e.g., `C:\Windows\System32` or a custom tools folder):

```batch
@echo off
java -jar "C:\path\to\openfasttrace-<version>.jar" %*
```
