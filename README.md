# openfasttrack

[![Build Status](https://travis-ci.org/hamstercommunity/openfasttrack.svg)](https://travis-ci.org/hamstercommunity/openfasttrack)
[![Circle CI](https://circleci.com/gh/hamstercommunity/openfasttrack.svg?style=svg)](https://circleci.com/gh/hamstercommunity/openfasttrack)
[![codecov.io](https://codecov.io/github/hamstercommunity/openfasttrack/coverage.svg?branch=master)](https://codecov.io/github/hamstercommunity/openfasttrack?branch=master)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/7509/badge.svg)](https://scan.coverity.com/projects/hamstercommunity-openfasttrack)

Requirement tracking suite

# Installation

## Dependencies

OpenFastTrack needs a Java JDK 8 (or later) implementation and Maven as build tool.

## On Linux

### Ubuntu or Debian

    apt-get install openjdk-8-jre

## Development

Run `mvn test` to run unit tests.

## Publishing to jcenter

To publish to jcenter

1. Add the following to your `~/.m2/settings.xml`
```xml
	<servers>
		<server>
			<id>bintray-openfasttrack-maven-repo</id>
			<username>[bintray-username]‚</username>
			<password>[bintray-api-key]</password>
		</server>
	</servers>
```
2. Run `mvn deploy`
