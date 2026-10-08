---
layout: default
title: Partial Tracing by Responsibility
parent: Use Cases
grand_parent: User Guide
nav_order: 4
---

### Partial Tracing by Responsibility

In a software project, different roles are responsible for different layers of the traceability chain. Product owners work with features (`feat`) and requirements (`req`), architects with architecture and detailed design (`arch`, `dsn`), developers with implementation and unit tests (`impl`, `utest`), and test engineers with integration and system tests (`itest`, `stest`).

When Soeren, the product owner, adds a feature and its system requirements, a full trace could report missing design or implementation that is not part of his review. Andrea, the architect, can likewise check design coverage before development is complete. The artifact type filter lets each role trace the layers relevant to their work.

For example, the layers may be organized like this:

```text
[Product owner]     feat  ──>  req
[Architect]                      └──>  arch  ──>  dsn
[Developer]                                        └──>  impl, utest
[Test engineers]                                   └──>  itest, stest
```

Each level declares the artifact types needed to cover its specification items (`Needs: ...`).

Use the `-a` or `--wanted-artifact-types` option with a comma-separated list of [artifact types](../../terminology.md#artifact-type) to limit a trace. Items, coverage needs, and links for types outside the list are left out of the trace; the filter narrows the trace results, not the files parsed.

Soeren, the product owner, checks that each user feature (`feat`) is specified by system requirements (`req`):

    oft trace -a feat,req doc/

This checks feature-to-requirement coverage without reporting missing downstream design or implementation.

After the requirements are in place, Andrea, the architect, checks their coverage by architecture and detailed design:

    oft trace -a feat,req,arch,dsn doc/

Including both feature and requirement items keeps the earlier layer in the trace; the filter does not require implementation or tests yet.

Wan and Wu, the developers, check implementation and unit tests along with the preceding specifications:

    oft trace -a feat,req,arch,dsn,impl,utest doc/ src/

When integration and system tests are ready, test engineers include their types in the filter:

    oft trace -a feat,req,arch,dsn,impl,utest,itest,stest doc/ src/

#### Artificial Termination of Specification Items

In a full trace, a chain ends at [terminating specification items](../../terminology.md#terminating-specification-item)—items that do not require further coverage (such as source code or test markers without a `Needs:` declaration).

When you trace with an artifact type filter, OFT strips all coverage requirements for artifact types that are not in the filter list. For instance, when Soeren runs `oft trace -a feat,req doc/`, a system requirement that normally declares `Needs: arch` has its `arch` requirement removed during the trace. This *artificially terminates* the requirement items at the boundary of the selected artifact types, allowing the trace to pass without missing-coverage errors for downstream artifacts that have not yet been written.

See also:
* [Distributing the Detailing Work](distributing_the_detailing_work.md) for filtering by tags across multiple teams
* [Import Options](../reference/oft_command_line.md#import-options) for command-line syntax details
