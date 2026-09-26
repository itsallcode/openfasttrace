---
layout: docs
title: Windows Installation
parent: Installation
grand_parent: User Guide
nav_order: 3
---

## Windows Installation

On Windows, OpenFastTrace is typically run manually via the JAR file.

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
