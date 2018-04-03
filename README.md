# <img src="src/main/resources/openfasttrace_logo.svg" alt="OFT logo" width="150"/> OpenFastTrace

[![Build Status](https://travis-ci.org/itsallcode/openfasttrace.svg)](https://travis-ci.org/itsallcode/openfasttrace)
[![Circle CI](https://circleci.com/gh/itsallcode/openfasttrace.svg?style=svg)](https://circleci.com/gh/itsallcode/openfasttrace)
[![Sonarcloud Quality Gate](https://sonarcloud.io/api/badges/gate?key=org.itsallcode%3Aopenfasttrace%3Adevelop)](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace%3Adevelop)
[![codecov](https://codecov.io/gh/itsallcode/openfasttrace/branch/develop/graph/badge.svg)](https://codecov.io/gh/itsallcode/openfasttrace)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/14936/badge.svg)](https://scan.coverity.com/projects/itsallcode-openfasttrace)
[![Download](https://api.bintray.com/packages/itsallcode/itsallcode/openfasttrace/images/download.svg) ](https://bintray.com/itsallcode/itsallcode/openfasttrace/_latestVersion)

## Installation

### Dependencies

#### Runtime Dependencies

OpenFastTrace only needs a Java 8 (or later) runtime environment to run.

#### Build time Dependencies

At build time the following dependencies are additionally required.

| Dependency                                                                   | Purpose                                                | License                       |
-------------------------------------------------------------------------------|--------------------------------------------------------|--------------------------------
| [Apache Maven](https://maven.apache.org/)                                    | Build tool                                             | Apache License 2.0            |
| [Equals Verifier](https://github.com/jqno/equalsverifier)                    | Automatic contract checker for `equals()` and `hash()` | Apache License 2.0            |
| [Hamcrest Auto Matcher](https://github.com/itsallcode/hamcrest-auto-matcher) | Speed-up for building Hamcrest matchers                | GNU General Public License V3 |
| [JUnit](https://junit.org/junit4/index.html)                                 | Unit testing framework                                 | Eclipse Public License 1.0    |
| [Mockito](http://site.mockito.org/)                                          | Mocking framework                                      | MIT License                   |
| [System rules](https://stefanbirkner.github.io/system-rules/)                | JUnit rules for testing `STDOUT`, `STDIN` and `STDERR` | Common Public License 1.0     |

### On Linux

#### Ubuntu or Debian

If you just want to run OFT:

    apt-get install openjdk-8-jre

If you want to build OFT:

    apt-get install openjdk-8-jdk maven

All other build time dependencies are automatically downloaded by Maven.

## Development

* Run `mvn test` to run unit tests.
* Run `mvn exec:java@trace` to run requirements tracing.

### Configure logging

We use [`java.util.logging`](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html) for logging. To configure log level and formatting, add the following system property:
```
-Djava.util.logging.config.file=src/test/resources/logging.properties
```

### License file header

* We use [license-maven-plugin](http://www.mojohaus.org/license-maven-plugin) to check in `verify` phase that all files have the correct license header. The build will fail if there are any files with missing/outdated headers.
* To update files with correct license headers and generate file `LICENSE.txt`, run command
```bash
mvn license:update-project-license license:update-file-header
```

### Publishing to jcenter

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
	$ mvn deploy
	```

4. Create a [release](https://github.com/itsallcode/openfasttrace/releases) on GitHub.
5. Sign in at https://bintray.com/ and publish the uploaded artifacts.
