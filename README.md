# openfasttrack

[![Build Status](https://travis-ci.org/hamstercommunity/openfasttrack.svg)](https://travis-ci.org/hamstercommunity/openfasttrack)
[![Circle CI](https://circleci.com/gh/hamstercommunity/openfasttrack.svg?style=svg)](https://circleci.com/gh/hamstercommunity/openfasttrack)
[![codecov.io](https://codecov.io/github/hamstercommunity/openfasttrack/coverage.svg?branch=master)](https://codecov.io/github/hamstercommunity/openfasttrack?branch=master)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/7509/badge.svg)](https://scan.coverity.com/projects/hamstercommunity-openfasttrack)
[![Download](https://api.bintray.com/packages/kaklakariada/maven/openfasttrack/images/download.svg)](https://bintray.com/kaklakariada/maven/openfasttrack/_latestVersion)

Requirement tracking suite

# Installation

## Dependencies

OpenFastTrack needs a Java JDK 8 (or later) implementation and Maven as build tool.

## On Linux

### Ubuntu or Debian

    apt-get install openjdk-8-jre

## Development

Run `mvn test` to run unit tests.

### Configure logging

We use [`java.util.logging`](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html) for logging. To configure log level and formatting, add the following system property:
```
-Djava.util.logging.config.file=src/test/resources/logging.properties
```

### License file header

* We use [license-maven-plugin](http://www.mojohaus.org/license-maven-plugin) to check in `verify` phase, that all files have the correct license header. The build will fail if there are any files with missing/outdated headers.
* To update files with correct license headers and generate file `LICENSE.txt`, run command
```bash
mvn license:update-project-license license:update-file-header
```

### Publishing to jcenter

To publish to jcenter

* Add the following to your `~/.m2/settings.xml`:
```xml
	<servers>
		<server>
			<id>bintray-openfasttrack-maven-repo</id>
			<username>[bintray-username]</username>
			<password>[bintray-api-key]</password>
		</server>
	</servers>
```
* If necessary update version in `pom.xml`, commit and push it.
* Run `mvn deploy`
* Create a [release](https://github.com/hamstercommunity/openfasttrack/releases) in github
