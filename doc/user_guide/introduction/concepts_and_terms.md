---
layout: default
title: Concepts and Terms
parent: Introduction
grand_parent: User Guide
nav_order: 4
---

### Concepts and Terms

OpenFastTrace uses unified terminology for all documents. Please refer to the [central terminology document](../../terminology.md) for core definitions of terms like [specification item](../../terminology.md#specification-item), [coverage](../../terminology.md#coverage), and [artifact](../../terminology.md#artifact).

The following sections provide detailed information on the parts of a [specification item ID](../../terminology.md#specification-item-id).

#### Specification Item Artifact Type

The artifact type serves two purposes:

1. identifying the source document type
2. identifying the position in the tracing hierarchy

Artifact types are represented by character strings consisting out of ASCII letters. No other characters are allowed.

While not enforced by OFT, the following strings are well-established:

* `feat` - high-level feature
* `req` - user requirement
* `arch` - architectural requirement
* `dsn` - design requirement
* `impl` - implementation
* `utest` - unit test
* `itest` - integration test
* `stest` - system test
* `uman` - user manual
* `oman` - operation manual

If you don't distinguish between architectural and detailed design, we recommend using `dsn` for both. The OFT specification, for example, does it that way.

How many types you introduce, how you name and stack them is up to you. When we designed OFT, we were clear about the fact that we would not be able to cover all possible artifact types one could imagine, so we did not hardcode them into OFT.

#### Specification Item Name

The name part of the ID must be a character string consisting of Unicode letters and/or numbers separated by underscore (`_`), hyphen (`-`) or dot (`.`). Whitespaces are not allowed.

* Names must start with a unicode letter
* Consecutive dots `.` are not allowed

We recommend using a dot `.` to create a hierarchy of items:

    exporter.html5.folding
    exporter.html5.colors
    exporter.csv.column_names

#### Specification Item Revision

The revision number of a specification item is a positive integer number that can be started at zero but by convention usually is started at one.

The revision is intended to obsolete existing coverage links in case the content of a specification item semantically changed. Incrementing the revision voids all existing links to this item so that authors linking to the item know they have to check for changes and adapt the covering items.

Examples:

If you change a requirement that lists all browsers that an HTML export needs to be compatible with, you made a semantic change and should raise the revision number.

If, on the other hand, you only added a missing period at the end of a sentence, the requirement content did not really change and there is no need to invalidate existing coverage.

---

← [Why do I Need Requirement Tracing?](why_do_i_need_requirement_tracing.md) | ↑ [Introduction](introduction.md) | [Installation](../installation/installation.md) →
