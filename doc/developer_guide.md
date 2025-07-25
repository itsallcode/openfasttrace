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
        <version>4.2.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

### Getting OFT via Gradle

To use OpenFastTrace as a dependency in your [Gradle](https://gradle.org/) project:

```groovy
dependencies {
    compile "org.itsallcode.openfasttrace:openfasttrace:4.2.0"
}
```

## Build Time Dependencies

The list below shows all build time dependencies in alphabetical order. Note that except the Maven build tool, Maven downloads all required modules automatically.

| Dependency                                                                         | Purpose                                                | License                       |
|------------------------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
| [Apache Maven](https://maven.apache.org/)                                          | Build tool                                             | Apache License 2.0            |
| [Equals Verifier](https://github.com/jqno/equalsverifier)                          | Automatic contract checker for `equals()` and `hash()` | Apache License 2.0            |
| [Hamcrest Auto Matcher](https://github.com/itsallcode/hamcrest-auto-matcher)       | Speed-up for building Hamcrest matchers                | GNU General Public License V3 |
| [JUnit](https://junit.org)                                                         | Unit testing framework                                 | Eclipse Public License 1.0    |
| [Mockito](https://github.com/mockito/mockito)                                      | Mocking framework                                      | MIT License                   |
| [JUnit5 System Extensions](https://github.com/itsallcode/junit5-system-extensions) | JUnit extension for testing `System.x` calls           | Eclipse Public License 2.0    |

## Preparations

OpenFastTrace uses [Apache Maven](https://maven.apache.org) as the technical project management tool that resolves and downloads the build-dependencies before building the packages.

### Installation of Initial Build Dependencies on Linux

#### Ubuntu or Debian

If you want to build OFT:

    apt-get install openjdk-17-jdk maven

## Configure Maven Toolchains

OFT uses Maven Toolchains to configure the correct JDK version (see the [documentation](https://maven.apache.org/guides/mini/guide-using-toolchains.html) for details). To configure the Toolchains plugin create file ` ~/.m2/toolchains.xml` with the following content. Adapt the paths to your JDKs.

```xml
<toolchains xmlns="http://maven.apache.org/TOOLCHAINS/1.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/TOOLCHAINS/1.1.0 http://maven.apache.org/xsd/toolchains-1.1.0.xsd">
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>17</version>
        </provides>
        <configuration>
            <jdkHome>/usr/lib/jvm/java-17-openjdk-amd64/</jdkHome>
        </configuration>
    </toolchain>
        <toolchain>
        <type>jdk</type>
        <provides>
            <version>21</version>
        </provides>
        <configuration>
            <jdkHome>/usr/lib/jvm/java-21-openjdk-amd64/</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
```

## Essential Build Steps

### Clone Git Repository

```sh
git clone https://github.com/itsallcode/openfasttrace.git
```

### Test and Build

Run unit tests:

```sh
mvn -T 1C test
```

Run unit and integration tests and additional checks:

```sh
mvn -T 1C verify
```

Build OFT:

```sh
mvn -T 1C package -DskipTests
```

This will build the executable JAR including all modules at `product/target/openfasttrace-$VERSION.jar`.

#### Specify Java Version

By default, OFT is built with Java 17.

To build and test with a later version, add argument `-Djava.version=17` to the Maven command.


#### Speedup Build

By default, Maven builds the OFT modules sequentially.

To speed up the build and build modules in parallel, add argument `-T 1C` to the Maven command.

### Run Requirements Tracing

```sh
./oft-self-trace.sh
```

## Using Eclipse

Import as a Maven project using *"File" &rarr; "Import..." &rarr; "Maven" &rarr; "Existing Maven Projects"*

## Configure the `itsallcode style` formatter

All subprojects come with formatter and save actions configuration for Eclipse.

If you use a different IDE like IntelliJ, please import the formatter configuration [itsallcode_formatter.xml](itsallcode_formatter.xml).

## Configure Logging

We use [`java.util.logging`](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html) for logging. To configure log level and formatting, add the following system property:

```bash
-Djava.util.logging.config.file=src/test/resources/logging.properties
```

## Check for updated dependencies / plugins

Display dependencies and plugins with newer versions:

```bash
mvn --update-snapshots versions:display-dependency-updates versions:display-plugin-updates
```

Automatically upgrade dependencies:

```bash
mvn -T 1C --update-snapshots versions:use-latest-releases versions:update-properties
```

## Run local sonar analysis

```bash
mvn -T 1C clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar \
    -Dsonar.token=[token]
```

See analysis results at [sonarcloud.io](https://sonarcloud.io/dashboard?id=org.itsallcode.openfasttrace%3Aopenfasttrace).

## Reproducible Build

This project is configured to produce exactly the same artifacts each time when building from the same Git commit. See the [Maven Guide to Configuring for Reproducible Builds](https://maven.apache.org/guides/mini/guide-reproducible-builds.html).

* Verify correct configuration of the reproducible build (also included in phase `verify`):
  ```bash
  mvn initialize artifact:check-buildplan
  ```
* Verify that the build produces exactly the same artifacts:
  ```bash
  mvn -T 1C clean install -DskipTests
  mvn -T 1C clean verify artifact:compare -DskipTests
  ```

The build will use the last Git commit timestamp as timestamp for files in `.jar` archives.

## Creating a Release

**NOTE**: This currently only works for release version numbers, not SNAPSHOT versions.

### Prepare the Release

1. Checkout the `main` branch.
2. Create a new "prepare-release" branch.
3. Update the version in
    * `openfasttrace-parent/pom.xml` (`revision` property)
    * `README.md`
    * `doc/developer_guide.md`
4. Add changes in a new version to `doc/changes/changes.md` and `doc/changes/changes_$VERSION.md` and update the release date.
5. Commit and push changes.
6. Create a new pull request, have it reviewed and merged to `main`.

### Perform the Release

1. Start the release workflow
   * Run command `gh workflow run release.yml --repo itsallcode/openfasttrace --ref main`
   * or go to [GitHub Actions](https://github.com/itsallcode/openfasttrace/actions/workflows/release.yml) and start the `release.yml` workflow on branch `main`.
2. Update the title and description of the newly created [GitHub release](https://github.com/itsallcode/openfasttrace/releases).
3. After some time the release will be available at [Maven Central](https://repo1.maven.org/maven2/org/itsallcode/openfasttrace/openfasttrace/).

## Module Overview

This section explains the module structure of OpenFastTrace.

| Module                                     | Deployed to MC | Content                                                  |
|--------------------------------------------|----------------|----------------------------------------------------------|
| **Core Modules**                           |                |                                                          |
| `openfasttrace-api`                        | ✅              | Core API interfaces and contracts for the tracing system |
| `openfasttrace-core`                       | ✅              | Main implementation of tracing logic and algorithms      |
| `openfasttrace-testutil`                   | ❌              | Test utilities and helpers (internal development only)   |
| **Exporters**                              |                |                                                          |
| `openfasttrace-exporter-common`            | ✅              | Common utilities and base classes for exporters          |
| `openfasttrace-exporter-specobject`        | ✅              | Exporter for SpecObject format                           |
| **Importers**                              |                |                                                          |
| `openfasttrace-importer-lightweightmarkup` | ✅              | Base classes for lightweight markup importers            |
| `openfasttrace-importer-markdown`          | ✅              | Importer for Markdown documents with requirement tags    |
| `openfasttrace-importer-restructuredtext`  | ✅              | Importer for reStructuredText documents                  |
| `openfasttrace-importer-specobject`        | ✅              | Importer for SpecObject XML format                       |
| `openfasttrace-importer-tag`               | ✅              | Importer for tagged source code comments                 |
| `openfasttrace-importer-xmlparser`         | ✅              | Common XML parsing utilities for importers               |
| `openfasttrace-importer-zip`               | ✅              | Importer for ZIP archives containing requirements        |
| **Reporters**                              |                |                                                          |
| `openfasttrace-reporter-plaintext`         | ✅              | Plain text report generator                              |
| `openfasttrace-reporter-html`              | ✅              | HTML report generator with interactive features          |
| `openfasttrace-reporter-aspec`             | ✅              | ASpec format reporter for augmented specifications       |
| **Product**                                |                |                                                          |
| `openfasttrace` (product)                  | ✅              | Complete application bundle with all modules (uber-JAR)  |
| **Infrastructure**                         |                |                                                          |
| `openfasttrace-parent`                     | ❌              | Base parent POM (build infrastructure)                   |
| `openfasttrace-mc-deployable-parent`       | ❌              | Deployment parent POM (build infrastructure)             |
| `openfasttrace` (root)                     | ❌              | Root aggregator POM (build infrastructure)               |

Legend:

✅ Deployed to Maven Central: Public API modules that external users can depend on

❌ Not Deployed: Internal infrastructure, test utilities, and build configuration

### Why Two Parent POMs?

As you can see in the table above, not all modules are intended to be deployed on Maven Central. The distinction is made by using two parent POMs. 

The base POM is `openfasttrace-parent`, it mainly contains common dependencies and build configuration. On top of that, `openfasttrace-mc-deployable-parent` adds signing and Maven central deployment. 

This structure ensures that only user-facing components are published to Maven Central while keeping internal development tools and build infrastructure private.

## Debugging Maven Central Deployment

### Build Deployment Bundle

Build deployment bundle to check if the expected modules are included:

```sh
mvn -T1C  deploy -Pcentral-publishing -DskipPublishing=true -DskipTests
```

This will build `central-bundle.zip` in one of the modules. Find it with `find . -name "central-bundle.zip"`, then check it's content with `unzip -l api/target/central-publishing/central-bundle.zip`.

Ensure that neither `testutil` nor any parent module is included.

### Verify Deployment Preconditions

Configure Maven Central credentials in `~/.m2/settings.xml`: 

```xml
<settings>
    <servers>
        <server>
            <id>central</id>
            <username>user</username>
            <password>password</password>
        </server>
    </servers>
</settings>

```

The following command will upload the bundle to Maven Central without publishing:

```sh
mvn -T1C clean deploy -Pcentral-publishing -DskipPublishing=true -DautoPublish=false -DskipTests
```

Then go to https://central.sonatype.com/publishing/deployments and check that the deployment is marked as "validated" and that the expected components are included. Don't forget to click the "Drop" button to avoid accidentally publishing the release.
