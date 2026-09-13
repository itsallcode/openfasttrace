---
layout: default
title: Excluding Parts of a Specification Document for OFT Parsing
parent: Use Cases
grand_parent: User Guide
nav_order: 2
---

### Excluding Parts of a Specification Document for OFT Parsing

Sometimes you want specific sections or a whole document to be excluded from OFT parsing. One reason could be that it is a document that contains an OFT example, that should not contribute to the trace. Or, you could have data in a document and don't want to risk that something accidentally looks like an OFT artifact.

To switch of scanning use the token `oft:on|off` in your document at the appropriate location.

Markdown example:

    <!-- oft:off -->
    This part is ignored by OFT.
    <!-- oft:on -->
    Here OFT scans again.

ReStructured text example:

    .. oft:off
    This part is ignored by OFT.
    .. oft:on
    Here OFT scans again.

---

← [Writing a Specification](writing_a_specification.md) | ↑ [Use Cases](use_cases.md) | [Delegating Requirement Coverage](delegating_requirement_coverage.md) →
