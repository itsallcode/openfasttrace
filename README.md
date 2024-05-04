# <img src="core/src/main/resources/openfasttrace_logo.svg" alt="OFT logo" width="150"/> OpenFastTrace

## What is OpenFastTrace?

OpenFastTrace (short OFT) is a requirement tracing suite. Requirement tracing helps you keeping track of whether you actually implemented everything you planned to in your specifications. It also identifies obsolete parts of your product and helps you getting rid of them.

You can learn more about requirement tracing and how to use OpenFastTrace in the [user guide](doc/user_guide.md).

Below you see a screenshot of of a HTML tracing report where OFT traces itself. You see a summary followed by a detail view of the traced requirements. 

<img src="doc/images/oft_screenshot_tracing_report.png" style="box-shadow: 5px 10px 18px #888888;" alt="OFT HTML tracing report">

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

**User Guides**

* [📖 About us](doc/about_us.md)
* [📖 User Guide](doc/user_guide.md)
* [💲 Command Line Usage](core/src/main/resources/usage.txt)

**News and Discussions**

* [📢 Blog](https://blog.itsallcode.org/)
* [➕ Changelog](doc/changes/changes.md)
* [📅 Upcoming Milestones](https://github.com/orgs/itsallcode/projects/3/views/3)
* [🗨️ Discussion Board](https://github.com/itsallcode/openfasttrace/discussions)
* [✨ OpenFastTrace Stories](https://github.com/itsallcode/openfasttrace/wiki/OFT-Stories)

**Information for Contributors**

* [🎟️ Project Board](https://github.com/orgs/itsallcode/projects/3/views/1)
* [🦮 Developer Guide](doc/developer_guide.md)
* [🎁 Contributing Guide](CONTRIBUTING.md)
* [💡 System Requirements](doc/spec/system_requirements.md)
* [👜 Design](doc/spec/design.md)

**Demos and Presentations**

* [📹 Introduction Video](https://www.youtube.com/watch?v=tlzMT6RaVWA) (YouTube, 3:30 minutes)
* [🛗 Elevator pitch](https://github.com/itsallcode/openfasttrace-demo/tree/main?tab=readme-ov-file#elevator-pitch)
* [📽️ OpenFastTrace Presentation](https://github.com/itsallcode/openfasttrace-demo/blob/main/OpenFastTrace_in_20_minutes.odp) (LibreOffice, 20 minutes)
* [🎬 OpenFastTrace Live Demo Script](https://github.com/itsallcode/openfasttrace-demo/blob/main/oft-live-demo-medium.md) (Markdown, 1 hour)

## Using OpenFastTrace

If you want to use OFT, you have the choice between using it as part of your build process &mdash; typically with Maven or Gradle. Or you can run OFT from the command line.

Check the [user guide](doc/user_guide.md) for detailed information on how to use OpenFastTrack.

## Getting OpenFastTrace

OpenFastTrace at it's core is a Java Archive (short "[JAR](https://docs.oracle.com/javase/8/docs/technotes/guides/jar/jar.html#JAR%20Manifest)"). This file contains the OpenFastTrace Library and an entry point for [running OFT from the command line](#running-openfasttrace).

### Getting Pre-Built Packages

Pre-Built JAR files (called `openfasttrace-3.8.0.jar`) are available from the following places:

* [Maven Central](https://repo1.maven.org/maven2/org/itsallcode/openfasttrace/openfasttrace/3.8.0/openfasttrace-3.8.0.jar)
* [GitHub](https://github.com/itsallcode/openfasttrace/releases/download/3.8.0/openfasttrace-3.8.0.jar)
 
Check our [developer guide](doc/developer_guide.md#getting-the-openfasttrace-library) to learn how to use the OFT JAR as dependency in your own code with popular build tools.

## Installation

### Runtime Dependencies

OpenFastTrace 3.0.0 and above only needs a Java 11 (or later) runtime environment to run. Older versions of OpenFastTrace can run with Java 8.

#### Installation of Runtime Dependencies on Linux

##### Ubuntu or Debian

If you just want to run OFT:

    apt-get install openjdk-11-jre

## Running OpenFastTrace

The most basic variant to run OpenFastTrace is directly from the JAR file via the command line:

```bash
java -jar product/target/openfasttrace-3.8.0.jar trace /path/to/directory/being/traced
```

If you want to run OFT automatically as part of a continuous build, we recommend using our plugins for [Gradle](https://github.com/itsallcode/openfasttrace-gradle) and [Maven](https://github.com/itsallcode/openfasttrace-maven-plugin).

For more details about how to run OFT please consult the [user guide](doc/user_guide.md).

## Development

If you want to learn how to build OpenFastTrace, please check our [Developer Guide](doc/developer_guide.md).

You would like to contribute to OFT? Please check out our [Contributor Guide](CONTRIBUTING.md) to get started. 
