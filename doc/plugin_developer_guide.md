# Plugin Developer Guide

This guide describes how to develop [plugins](plugins.md) for OpenFastTrace (OFT).

## Initial Setup

1. Create a new Java project and add dependency [`org.itsallcode.openfasttrace:openfasttrace-api`](https://search.maven.org/artifact/org.itsallcode.openfasttrace/openfasttrace-api):
   ```xml
   <dependency>
       <groupId>org.itsallcode.openfasttrace</groupId>
       <artifactId>openfasttrace-api</artifactId>
       <version>[latest version]</version>
   </dependency>
   ```

2. Create a new class (e.g. `com.example.oft.import.MyImporter`) implementing one of the following interfaces:
   * [`org.itsallcode.openfasttrace.api.report.ReporterFactory`](https://github.com/itsallcode/openfasttrace/blob/main/api/src/main/java/org/itsallcode/openfasttrace/api/report/ReporterFactory.java): Generate tracing report
   * [`org.itsallcode.openfasttrace.api.importer.ImporterFactory`](https://github.com/itsallcode/openfasttrace/blob/main/api/src/main/java/org/itsallcode/openfasttrace/api/importer/ImporterFactory.java): Import requirements from a new file format
   * [`org.itsallcode.openfasttrace.api.exporter.ExporterFactory`](https://github.com/itsallcode/openfasttrace/blob/main/api/src/main/java/org/itsallcode/openfasttrace/api/exporter/ExporterFactory.java): Export requirements in a new file format

3. Create a file in `src/main/resources/$INTERFACE_FQN`, using the fully qualified class name of the interface as file name.

4. Add the fully qualified class name of your new plugin class to the new file, e.g. `com.example.oft.import.MyImporter`

## Runtime Dependencies

OpenFastTrace does not use any third-party runtime dependencies by design. You can add any dependencies to your plugin if required (see [note about packaging](#adding-third-party-dependencies)).

**Warning** The plugin must not use any classes other than those included in the API `openfasttrace-api`. All other classes in OFT are internal and may change in incompatible ways even in patch releases.

## Packaging

Build your plugin as a normal Java JAR file, e.g. using `maven-jar-plugin`. The JAR must not contain `openfasttrace-api`.

### Adding Third-Party Dependencies

If your plugin uses third party dependencies, you have two options:
* Publish and install the plugin and its dependencies as separate JARs.
* Build a fat JAR, e.g. using `maven-shade-plugin` and include the plugin's dependencies.

   **Important:** do not include `openfasttrace-api` in the fat JAR to avoid having duplicate classes on the classpath at runtime.
