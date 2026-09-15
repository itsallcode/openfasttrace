---
layout: default
title: Tracing the Whole Chain in the Same File System
parent: Use Cases
grand_parent: User Guide
nav_order: 7
---

### Tracing the Whole Chain in the Same File System

Preconditions:

* All artifacts are readable for the user executing OFT

Description:

In a small project you probably have all artifacts in the same file system - most likely under a common root directory.
In this case the easiest way to get a full trace is to list all the directories that OFT should search for artifacts to import.

Let's assume a typical Java project with the following directory layout:

    /home/git/my-project
      |-- doc                      manuals, requirement specification and design
      |-- src
      |    |-- main
      |    |     '-- java          implementation
      |    '-- test
     ...         '-- java          unit and integration tests
     

In this case the minimal OFT command line looks like this:

```sh
PROJECT_ROOT='/home/git/my-project/'
oft trace "$PROJECT_ROOT"/doc "$PROJECT_ROOT"/src/main/java "$PROJECT_ROOT"/src/test/java
```

Or if you prefer it shorter:

```sh
cd /home/git/my-project/
oft trace doc src/main/java src/test/java
```

The first variant is better suited for integration into scripts where you usually want to avoid changing the directory.

By default, this will produce a plain text trace that displays details of all defect specification items and a summary.

See also:
* [Tracing Options](../reference/oft_command_line.md#tracing-options) for controlling the tracing output

---

← [Tracing the Whole Chain](tracing_the_whole_chain.md) &nbsp;&nbsp;•&nbsp;&nbsp; ↑ [Use Cases](use_cases.md) &nbsp;&nbsp;•&nbsp;&nbsp; [HTML Tracing Reports](html_tracing_reports.md) →
