# <img src="src/main/resources/openfasttrace_logo.svg" alt="OFT logo" width="150"/> OpenFastTrace

[![Build Status](https://travis-ci.org/itsallcode/openfasttrace.svg)](https://travis-ci.org/itsallcode/openfasttrace)
[![Circle CI](https://circleci.com/gh/itsallcode/openfasttrace.svg?style=svg)](https://circleci.com/gh/itsallcode/openfasttrace)
[![codecov](https://codecov.io/gh/itsallcode/openfasttrace/branch/develop/graph/badge.svg)](https://codecov.io/gh/itsallcode/openfasttrace)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/14936/badge.svg)](https://scan.coverity.com/projects/itsallcode-openfasttrace)
[![Download](https://api.bintray.com/packages/itsallcode/itsallcode/openfasttrace/images/download.svg)](https://bintray.com/itsallcode/itsallcode/openfasttrace/_latestVersion)

Sonarcloud status:

[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=bugs)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![Code smells](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=code_smells)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=coverage)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![Duplicated Lines](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=ncloc)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=security_rating)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![Technical Dept](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=sqale_index)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace%3Adevelop&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)

## Project Information

* ![Contributing guide](CONTRIBUTING.md)
* ![OpenFastTrace stories](https://github.com/itsallcode/openfasttrace/wiki/OFT-Stories)
* ![Command Line Usage](doc/usage.txt)
* ![Design](doc/design.md)
* ![System Requirements](doc/system_requirements.md)

## Installation

### Runtime Dependencies

OpenFastTrace only needs a Java 8 (or later) runtime environment to run.

### Installation on Linux

#### Ubuntu or Debian

If you just want to run OFT:

    apt-get install openjdk-8-jre

If you want to build OFT:

    apt-get install openjdk-8-jdk maven

## Development

### Build Time Dependencies

The list below show all build time dependencies in alphabetical order. Note that except the Maven build tool all required modules are downloaded automatically by Maven.

| Dependency                                                                   | Purpose                                                | License                       |
-------------------------------------------------------------------------------|--------------------------------------------------------|--------------------------------
| [Apache Maven](https://maven.apache.org/)                                    | Build tool                                             | Apache License 2.0            |
| [Equals Verifier](https://github.com/jqno/equalsverifier)                    | Automatic contract checker for `equals()` and `hash()` | Apache License 2.0            |
| [Hamcrest Auto Matcher](https://github.com/itsallcode/hamcrest-auto-matcher) | Speed-up for building Hamcrest matchers                | GNU General Public License V3 |
| [JUnit](https://junit.org/junit4/index.html)                                 | Unit testing framework                                 | Eclipse Public License 1.0    |
| [Mockito](http://site.mockito.org/)                                          | Mocking framework                                      | MIT License                   |
| [System rules](https://stefanbirkner.github.io/system-rules/)                | JUnit rules for testing `STDOUT`, `STDIN` and `STDERR` | Common Public License 1.0     |

### Essential Build Steps

* Run `mvn test` to run unit tests.
* Run `mvn exec:java@trace` to run requirements tracing.

### Configure Logging

We use [`java.util.logging`](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html) for logging. To configure log level and formatting, add the following system property:

```bash
-Djava.util.logging.config.file=src/test/resources/logging.properties
```

### License File Header

* We use [license-maven-plugin](http://www.mojohaus.org/license-maven-plugin) to check in `verify` phase that all files have the correct license header. The build will fail if there are any files with missing/outdated headers.
* To update files with correct license headers and generate file `LICENSE.txt`, run command

```bash
mvn license:update-project-license license:update-file-header
```

### Publishing to JCenter

1. Add the following to your `~/.m2/settings.xml`:

    ```xml
    <servers>
        <server>
            <id>bintray-openfasttrack-maven-repo</id>
            <username>[bintray-username]</username>
            <password>[bintray-api-key]</password>
        </server>
    </servers>
    ```

2. Update version in `pom.xml`, commit and push.
3. Run command

    ```bash
    mvn deploy
    ```

4. Create a [release](https://github.com/itsallcode/openfasttrace/releases) on GitHub.
5. Sign in at [bintray.com](https://bintray.com)
6. Go to the [bintray project page](https://bintray.com/itsallcode/itsallcode/openfasttrace)
7. There should be a notice saying "You have 6 unpublished item(s) for this package". Click the "Publish" link.
