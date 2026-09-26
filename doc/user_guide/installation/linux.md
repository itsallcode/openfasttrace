---
layout: default
title: Linux Installation
parent: Installation
grand_parent: User Guide
nav_order: 1
---

## Linux Installation

There are several ways to install OpenFastTrace on Linux, depending on your distribution and preference.

### Package Manager (APT - Debian, Ubuntu, etc.)

For Debian-based systems, we provide an APT repository. This is the recommended way to keep OFT up to date.

1.  Follow the instructions at the [itsallcode-apt-repository](https://github.com/itsallcode/itsallcode-apt-repository) to add the repository to your system.
2.  Install OpenFastTrace using:
    ```bash
    sudo apt update
    sudo apt install openfasttrace
    ```

### Homebrew (Linuxbrew)

If you use Homebrew on Linux, you can install OpenFastTrace directly:

```bash
brew install openfasttrace
```

### Manual Installation

If you prefer a manual setup, you can use the executable JAR file directly.

#### 1. Install Java

OpenFastTrace requires **Java 17 or newer**. On Linux, you can usually install it via your package manager:

*   **Debian/Ubuntu**: `sudo apt install default-jre` or `sudo apt install openjdk-17-jre`
*   **Fedora/RHEL**: `sudo dnf install java-17-openjdk`
*   **Arch Linux**: `sudo pacman -S jre17-openjdk`

#### 2. Download OFT

Download the `openfasttrace-<version>.jar` from the [GitHub Releases](https://github.com/itsallcode/openfasttrace/releases) page. Please check for the latest available version there.

#### 3. Run OFT

You can now run OFT using:
```bash
java -jar path/to/openfasttrace-<version>.jar --help
```

#### 4. Creating a command alias (Optional)

To make it easier to run, add an alias to your `.bashrc` or `.zshrc`:
```bash
alias oft='java -jar /path/to/openfasttrace-<version>.jar'
```
