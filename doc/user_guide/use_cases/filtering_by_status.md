---
layout: default
title: Filtering by Status
parent: Use Cases
grand_parent: User Guide
nav_order: 5
---

### Filtering by Status

Sometimes you only want to see specification items that have reached a certain maturity level. For example, you might want to create a report that only includes approved requirements.

To achieve this, you can filter by status using the `-w` or `--wanted-statuses` option:

    oft trace -w approved doc/

This tells OFT to only import specification items that have the status `approved`. You can also provide a comma-separated list of statuses:

    oft trace -w approved,proposed doc/

---

← [Distributing the Detailing Work](distributing_the_detailing_work.md) | ↑ [Use Cases](use_cases.md) | [Tracing the Whole Chain](tracing_the_whole_chain.md) →
