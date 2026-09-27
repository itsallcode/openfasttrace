# <img src="core/src/main/resources/openfasttrace_logo.svg" alt="OFT logo" width="150"/> OpenFastTrace

## What is OpenFastTrace?

OpenFastTrace (short OFT) is a requirement management and tracing suite. Requirement tracing keeps track of whether you actually implemented everything you planned to in your specifications. It also identifies obsolete parts of your product and helps you to get rid of them.

You can learn more about requirement tracing and how to use OpenFastTrace in the [user guide](doc/user_guide/user_guide.md).

Below you see a screenshot of an HTML tracing report where OFT traces itself. You see a summary followed by a detail view of the traced requirements. 

<img src="doc/assets/images/oft_screenshot_tracing_report.png" style="box-shadow: 5px 10px 18px #888888;" alt="OFT HTML tracing report">

## Project Information

[![Build](https://github.com/itsallcode/openfasttrace/actions/workflows/build.yml/badge.svg)](https://github.com/itsallcode/openfasttrace/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/org.itsallcode.openfasttrace/openfasttrace.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.itsallcode.openfasttrace%22%20a%3A%22openfasttrace%22)

Sonarcloud status:

[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=bugs)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)
[![Code smells](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=code_smells)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=coverage)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)
[![Duplicated Lines](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=ncloc)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=security_rating)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)
[![Technical Dept](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=sqale_index)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.openfasttrace%3Aopenfasttrace-root&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace-root)

**User Guides and Tools**

* [📖 User Guide](https://openfasttrace.itsallcode.org/user_guide/user_guide.html)
* [📚 Terminology](https://openfasttrace.itsallcode.org/terminology.html)
* [🔌 Extending OpenFastTrace With Plugins](https://openfasttrace.itsallcode.org/plugins.html)
* [💲 Command Line Usage](core/src/main/resources/usage.txt)
* [🛠 IntelliJ Plugin (PyCharm, Clion, etc.)](https://github.com/itsallcode/openfasttrace-intellij-plugin)
* [🤖 Agent Skills](.agents/skills)
* [🛡️ Security Policy](SECURITY.md)
* [♻️ Project Lifecycle and Deprecations](doc/user_guide/product_lifecycle/product_lifecycle.md)

**News and Discussions**

* [🌐 Homepage](https://openfasttrace.itsallcode.org)
* [📢 Blog](https://blog.itsallcode.org/)
* [➕ Changelog](doc/changes/changes.md)
* [📅 Upcoming Milestones](https://github.com/orgs/itsallcode/projects/3/views/3)
* [🗨️ Discussion Board](https://github.com/itsallcode/openfasttrace/discussions)
* [🐘 OpenFastTrace@mastodon.social](https://mastodon.social/@OpenFastTrace)
* [✨ OpenFastTrace Stories](https://github.com/itsallcode/openfasttrace/wiki/OFT-Stories)
* [ℹ️ About us](https://openfasttrace.itsallcode.org/about_us.html)

**Information for Contributors**

* [🎟️ Project Board](https://github.com/orgs/itsallcode/projects/3/views/1)
* [🦮 Developer Guide](https://openfasttrace.itsallcode.org/developer_guide/developer_guide.html)
* [🔌 Plugin Developer Guide](doc/developer_guide/plugin_developer_guide.md)
* [🎁 Contributing Guide](CONTRIBUTING.md)
* [🤝 Code of Conduct](CODE_OF_CONDUCT.md)
* [🤖 AI Agent Guide](AGENTS.md)
* [💡 System Requirements](doc/spec/system_requirements.md)
* [👜 Design](doc/spec/design.md)

**Demos and Presentations**

* [📹 Introduction Video](https://www.youtube.com/watch?v=tlzMT6RaVWA) (YouTube, 3:30 minutes)
* [🛗 Elevator pitch](https://github.com/itsallcode/openfasttrace-demo/tree/main?tab=readme-ov-file#elevator-pitch)
* [📽️ OpenFastTrace Presentation](https://github.com/itsallcode/openfasttrace-demo/blob/main/OpenFastTrace_in_20_minutes.odp) (LibreOffice, 20 minutes)
* [🎬 OpenFastTrace Live Demo Script](https://github.com/itsallcode/openfasttrace-demo/blob/main/oft-live-demo-medium.md) (Markdown, 1 hour)

## Using OpenFastTrace

If you want to use OFT, you have the choice between using it as part of your build process — typically with Maven or Gradle. Or you can run OFT from the command line.

Check the [user guide](https://openfasttrace.itsallcode.org/user_guide/user_guide.md) for detailed information on how to use OpenFastTrace.

## Installation

### Runtime Dependencies

OpenFastTrace 4.0.0 and above only needs a Java 17 (or later) runtime environment to run. OpenFastTrace until version 3.x.x supported Java 11. Versions prior to that ran with Java 8.
Note that only the latest version of OFT is actively supported.

### Installation

You can get OFT via our [Debian APT repository](https://itsallcode/itsallcode-apt-repository), through [Homebrew](https://formulae.brew.sh/formula/openfasttrace), as [Maven plugin](https://central.sonatype.com/artifact/org.itsallcode/openfasttrace-maven-plugin) or [Gradle plugin](https://plugins.gradle.org/plugin/org.itsallcode.openfasttrace). Or you can get the [jar archive]((https://docs.oracle.com/javase/8/docs/technotes/guides/jar/jar.html#JAR%20Manifest)) via [releases on GitHub](releases).

Check the chapter about [Installation](https://openfasttrace.itsallcode.org/user_guide/installation/installation.html) in the user guide for details.

## Running OpenFastTrace

### Run JAR File

The most basic variant to run OpenFastTrace is directly from the JAR file via the command line:

```sh
java -jar product/target/openfasttrace-4.10.0.jar trace /path/to/directory/being/traced
```

If you want to run OFT automatically as part of a continuous build, we recommend using our plugins for [Gradle](https://github.com/itsallcode/openfasttrace-gradle) and [Maven](https://github.com/itsallcode/openfasttrace-maven-plugin).

For more details about how to run OFT please consult the [user guide](https://openfasttrace.itsallcode.org/user_guide/user_guide.md).

### Download and Execute in Continuous Integration

If you want to run OFT in a CI build, you can use the OFT wrapper script [oftw.sh](product/oftw.sh). The script only requires Java 17 and Maven and downloads the OFT JAR from Maven Central if it is not yet available in the local Maven repository `$HOME/.m2/repository`.

## Development

If you want to learn how to build OpenFastTrace, please check our [Developer Guide](https://openfasttrace.itsallcode.org/developer_guide/developer_guide.md).

You would like to contribute to OFT? Please check out our [Contributor Guide](CONTRIBUTING.md) to get started. 
