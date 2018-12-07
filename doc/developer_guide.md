# OpenFastTrace Developer Guide

This document contains technical information for developers contributing to OpenFastTrace (short OFT).

If you want to know more about how to contribute to OFT, please check out our [Contributor Guide](../CONTRIBUTION.md).

## Build Time Dependencies

The list below shows all build time dependencies in alphabetical order. Note that except the Maven build tool all required modules are downloaded automatically by Maven.

| Dependency                                                                   | Purpose                                                | License                       |
-------------------------------------------------------------------------------|--------------------------------------------------------|--------------------------------
| [Apache Maven](https://maven.apache.org/)                                    | Build tool                                             | Apache License 2.0            |
| [Equals Verifier](https://github.com/jqno/equalsverifier)                    | Automatic contract checker for `equals()` and `hash()` | Apache License 2.0            |
| [Hamcrest Auto Matcher](https://github.com/itsallcode/hamcrest-auto-matcher) | Speed-up for building Hamcrest matchers                | GNU General Public License V3 |
| [JUnit](https://junit.org/junit5)                                            | Unit testing framework                                 | Eclipse Public License 1.0    |
| [Mockito](http://site.mockito.org/)                                          | Mocking framework                                      | MIT License                   |
| [JUnit5 System Extensions](https://github.com/itsallcode/junit5-system-extensions) | JUnit extension for testing `System.x` calls    | Eclipse Public License 2.0     |
| [Pitest](http://pitest.org/)                                                 | Mutation testing                                       | Apache License 2.0            |

## Preparations

OpenFastTrace uses [Apache Maven](https://maven.apache.org) as technical project management tool that resolves and downloads the build-dependencies before building the packages.

### Installation of Build Initial Dependencies on Linux

#### Ubuntu or Debian

If you want to build OFT:

    apt-get install openjdk-8-jdk maven

## Essential Build Steps

* `git clone https://github.com/itsallcode/openfasttrace.git`
* Run `mvn test` to run unit tests.
* Run `mvn exec:java@trace` to run requirements tracing.

## Using Eclipse

Import as a Maven project using *"File" &rarr; "Import..." &rarr; "Maven" &rarr; "Existing Maven Projects"*

## Configure Logging

We use [`java.util.logging`](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html) for logging. To configure log level and formatting, add the following system property:

```bash
-Djava.util.logging.config.file=src/test/resources/logging.properties

## License File Header

* We use [license-maven-plugin](http://www.mojohaus.org/license-maven-plugin) to check in `verify` phase that all files have the correct license header. The build will fail if there are any files with missing/outdated headers.
* To update files with correct license headers and generate file `LICENSE.txt`, run command

```bash
mvn license:update-project-license license:update-file-header
```

## Run local sonar analysis

```bash
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar \
    -Dsonar.host.url=https://sonarcloud.io \
    -Dsonar.organization=itsallcode \
    -Dsonar.login=[token]
```

See analysis results at https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace

## Run [mutation testing](http://pitest.org)

```bash
mvn org.pitest:pitest-maven:mutationCoverage
# speed up repeated analysis with history
mvn -DwithHistory org.pitest:pitest-maven:mutationCoverage
```

## Publishing to JCenter

1. Add the following to your `~/.m2/settings.xml`:

    ```xml
    <servers>
        <server>
            <id>itsallcode-maven-repo</id>
            <username>[bintray-username]</username>
            <password>[bintray-api-key]</password>
        </server>
    </servers>
    ```

1. Checkout the `develop` branch.
1. Update version in `pom.xml` and `README.md`, commit and push.
1. Run command

    ```bash
    mvn deploy
    ```
1. Merge to `master` branch
1. Create a [release](https://github.com/itsallcode/openfasttrace/releases) of the `master` branch on GitHub.
1. Sign in at [bintray.com](https://bintray.com)
1. Go to the [Bintray project page](https://bintray.com/itsallcode/itsallcode/openfasttrace)
1. There should be a notice saying "You have 6 unpublished item(s) for this package". Click the "Publish" link. Binaries will be available for download at [JCenter](https://jcenter.bintray.com/org/itsallcode/openfasttrace/)
1. Publish to Maven Central by clicking the "Sync" button at https://bintray.com/itsallcode/itsallcode/openfasttrace#central. After some time the new version will appear at https://repo1.maven.org/maven2/org/itsallcode/openfasttrace/.
