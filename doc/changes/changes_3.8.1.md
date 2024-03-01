# OpenFastTrace 3.8.1, released 2024-03-??

Code name: Bugfixes for Markdown Importer

## Summary

This release fixes unexpected behaviour in the Markdown importer. Before it detected `Needs` tags in unexpected location.

In the following example, the importer detected `image` as `Needs` coverage:

```md
Test case for assigning a specific number of SPIs to allocate for dom0less domain
=================================================================================
`XenValTestCase~arm64_assign_domain_SPIs~1`

Covers:
 - `XenSSR~arm64_assign_domain_SPIs~1`

Needs:
 - XenValTestCode

Objectives
----------

The script is responsible for:
 - preparing kernel image (AArch64 linux image)
```

We changed this behaviour. Now `Needs` coverage is only recognized until the next empty line. So the list must not contain empty lines.

The Markdown importer now also accepts heading underlines with trailing whitespace. This avoids hard-to-find issues with heading detection.

## Bugfixes

* #396: Fixed unexpected result in Markdown importer
