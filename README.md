# OpenFastTrace

[![Build Status](https://travis-ci.org/itsallcode/openfasttrace.svg)](https://travis-ci.org/itsallcode/openfasttrace)
[![Circle CI](https://circleci.com/gh/itsallcode/openfasttrace.svg?style=svg)](https://circleci.com/gh/itsallcode/openfasttrace)
[![Sonarcloud Quality Gate](https://sonarcloud.io/api/badges/gate?key=com.github.kaklakariada%3Aopenfasttrack)](https://sonarcloud.io/dashboard?id=com.github.kaklakariada%3Aopenfasttrack-gradle%3Adevelop)
[![codecov.io](https://codecov.io/github/itsallcode/openfasttrace/coverage.svg?branch=develop)](https://codecov.io/github/itsallcode/openfasttrace?branch=develop)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/14936/badge.svg)](https://scan.coverity.com/projects/itsallcode-openfasttrace)
[![Download](https://api.bintray.com/packages/itsallcode/itsallcode/openfasttrace/images/download.svg) ](https://bintray.com/itsallcode/itsallcode/openfasttrace/_latestVersion)

Requirement tracking suite

# Installation

## Dependencies

OpenFastTrace needs a Java JDK 8 (or later) implementation and Maven as build tool.

## On Linux

### Ubuntu or Debian

    apt-get install openjdk-8-jre

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