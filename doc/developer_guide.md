# OpenFastTrace Developer Guide

This document contains technical information for developers contributing to OpenFastTrace (short OFT).

If you want to know more about how to contribute to OFT, please check out our [Contributor Guide](../CONTRIBUTING.md).

## Getting the OpenFastTrace Library

### Getting OFT via Maven

To use OpenFastTrace as a dependency in your [Maven](https://maven.apache.org) project add this to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.itsallcode.openfasttrace</groupId>
        <artifactId>openfasttrace</artifactId>
        <version>3.8.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

### Getting OFT via Gradle

To use OpenFastTrace as a dependency in your [Gradle](https://gradle.org/) project:

```groovy
dependencies {
    compile "org.itsallcode.openfasttrace:openfasttrace:3.8.0"
}
```

## Build Time Dependencies

The list below shows all build time dependencies in alphabetical order. Note that except the Maven build tool all required modules are downloaded automatically by Maven.

| Dependency                                                                         | Purpose                                                | License                       |
|------------------------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
| [Apache Maven](https://maven.apache.org/)                                          | Build tool                                             | Apache License 2.0            |
| [Equals Verifier](https://github.com/jqno/equalsverifier)                          | Automatic contract checker for `equals()` and `hash()` | Apache License 2.0            |
| [Hamcrest Auto Matcher](https://github.com/itsallcode/hamcrest-auto-matcher)       | Speed-up for building Hamcrest matchers                | GNU General Public License V3 |
| [JUnit](https://junit.org/junit5)                                                  | Unit testing framework                                 | Eclipse Public License 1.0    |
| [Mockito](https://github.com/mockito/mockito)                                      | Mocking framework                                      | MIT License                   |
| [JUnit5 System Extensions](https://github.com/itsallcode/junit5-system-extensions) | JUnit extension for testing `System.x` calls    | Eclipse Public License 2.0     |

## Preparations

OpenFastTrace uses [Apache Maven](https://maven.apache.org) as technical project management tool that resolves and downloads the build-dependencies before building the packages.

### Installation of Initial Build Dependencies on Linux

#### Ubuntu or Debian

If you want to build OFT:

    apt-get install openjdk-11-jdk maven

## Essential Build Steps

* `git clone https://github.com/itsallcode/openfasttrace.git`
* Run `mvn test` to run unit tests.
* Run `mvn verify` to run integration tests.
* Run `./oft-self-trace.sh` to run requirements tracing.

## Using Eclipse

Import as a Maven project using *"File" &rarr; "Import..." &rarr; "Maven" &rarr; "Existing Maven Projects"*

## Configure the `itsallcode style` formatter

All sub-projects come with formatter and save actions configuration for Eclipse.

If you use a different IDE like IntelliJ, please import the formatter configuration [itsallcode_formatter.xml](itsallcode_formatter.xml).

## Configure Logging

We use [`java.util.logging`](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html) for logging. To configure log level and formatting, add the following system property:

```bash
-Djava.util.logging.config.file=src/test/resources/logging.properties
```

## Check for updated dependencies / plugins

```bash
mvn --update-snapshots versions:display-dependency-updates versions:display-plugin-updates
```

## Run local sonar analysis

```bash
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar \
    -Dsonar.host.url=https://sonarcloud.io \
    -Dsonar.organization=itsallcode \
    -Dsonar.login=[token]
```

See analysis results at [sonarcloud.io](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace).

## Creating a Release

**NOTE**: This currently only works for release version numbers, not SNAPSHOT versions.

### One-time Setup

Add the following to your `~/.m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>your-jira-id</username>
            <password>your-jira-pwd</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>ossrh</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.keyname>key_id</gpg.keyname>
                <gpg.passphrase>the_pass_phrase</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

### Prepare the Release

1. Checkout the `main` branch.
2. Create a new "prepare-release" branch.
3. Update version in
    * `openfasttrace-parent/pom.xml` (`revision` property)
    * `README.md`
    * `doc/developer_guide.md`
4. Add changes in new version to `doc/changes/changes.md` and `doc/changes/changes_$VERSION.md` and update the release date.
5. Verify that build runs successfully:

    ```bash
    mvn clean verify
    ```
6. Commit and push changes.
7. Create a new Pull Request, have it reviewed and merged.

### Perform the Release

1. Checkout `main` and get the latest changes.
1. Run this command

    ```bash
    mvn clean deploy -Possrh
    ```
1. Create a [release](https://github.com/itsallcode/openfasttrace/releases) of the `main` branch on GitHub.
1. Copy the changelog entries for the new version to the GitHub release.
1. Upload `product/target/openfasttrace-<version>.jar` and attach it to the new GitHub release.
1. After some time the release will be available at [Maven Central](https://repo1.maven.org/maven2/org/itsallcode/openfasttrace/openfasttrace/).
