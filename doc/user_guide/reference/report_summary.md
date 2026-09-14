---
layout: default
title: Report Summary
parent: Reference
grand_parent: User Guide
nav_order: 5
---

### Report Summary

At the end of the report, a summary is displayed that informs you about the overall state of the trace.

> **ok** - 123 total

If there are defects, the summary provides more details:

> **not ok** - 123 total, 5 direct, 2 transitive defects

Here, "direct" means that the item itself has a coverage defect, and "transitive" means that the item is correctly covered, but one of the items it covers (or its descendants) has a defect.

---

← [Console Tracing Report](console_tracing_report.md) &nbsp;&nbsp;•&nbsp;&nbsp; ↑ [Reference](reference.md) &nbsp;&nbsp;•&nbsp;&nbsp; [XML Tracing Report](xml_tracing_report.md) →
