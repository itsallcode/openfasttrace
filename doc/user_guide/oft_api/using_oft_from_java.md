---
layout: default
title: Using OFT From Java
parent: OFT API
grand_parent: User Guide
nav_order: 1
---

### Using OFT From Java

The Java interface uses the "fluent programming" paradigm to make the code more compact and easy to read.

The steps that you need to program using the OFT API depend on whether you want to covert between requirement formats

    import -> export

or run a report.

     import -> link -> trace -> report

#### Converting File from Java

The following example code use OFT as a converter that scans the current working directory recursively (default import setting) and exports the found artifacts with the standard settings to a ReqM2 file. 

```java
import org.itsallcode.openfasttrace.Oft;
import org.itsallcode.openfasttrace.core.SpecificationItem;
```

Select input paths and import specification items from there:

```java
final Oft oft = Oft.create();
final List<SpecificationItem> items = oft.importItems(settings);
```

Export the items:

```java
oft.exportToPath(items, Paths.get("/output/path/export.oreqm"));
```

#### Tracing and Reporting From Java

The example below shows how to use OFT as a reporter.  

```java
import org.itsallcode.openfasttrace.Oft;
import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.Trace;
```

The import is similar to the converter case, except this time we add an input path explicitly for the sake of demonstration: 

```java
final ImportSettings settings = ImportSettings //
        .builder() //
        .addInputs("/input/path") //
        .build;
final Oft oft = Oft.create();
final List<SpecificationItem> items = oft.importItems(settings);
```

Now link the items together (i.e. make them navigable):

```java
final List<LinkedSpecificationItem> linkedItems = oft.link(items);
```

Run the tracer on the linked items:

```java
final Trace trace = oft.trace(linkedItems);
```

Create a report from the trace:

```java
oft.reportToStdOut(trace);
```

You can also use the trace results in your own code:

```java
if (trace.hasNoDefects())
{
    // ... do something
}
```

#### Reporting Formats

There are various reporting formats for OFT and one can set it using the ReportSettings object.

```java
ReportSettings reportSettings = ReportSettings.builder().outputFormat("html").build();
```

The `ReportSettings` builder has other functions as well that allow you to set verbosity etc.

OFT allows you to report directly to the standard output or to a file

```java
// Reporting to a file
oft.reportToPath(trace, reportPath, reportSettings);
```

```java
// Reporting to stdout
oft.reportToStdOut(trace);
```

#### Configuring the Steps

Import, export and report each have an overloaded variant that can be configured using the following classes

* [org.itsallcode.openfasttrace.api.importer.ImportSettings](../../../api/src/main/java/org/itsallcode/openfasttrace/api/importer/ImportSettings.java)
* [org.itsallcode.openfasttrace.core.ExportSettings](../../../core/src/main/java/org/itsallcode/openfasttrace/core/ExportSettings.java)
* [org.itsallcode.openfasttrace.api.ReportSettings](../../../api/src/main/java/org/itsallcode/openfasttrace/api/ReportSettings.java)

Each of those classes comes with a builder which is called like this:

```java
ReportSettings settings = ReportSettings.builder().newline(Newline.UNIX).build();
```

---

← [OFT API](oft_api.md) | ↑ [OFT API](oft_api.md) | [Exit Codes](exit_codes.md) →
