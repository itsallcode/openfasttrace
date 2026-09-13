---
layout: default
title: OFT Command Line
parent: Reference
grand_parent: User Guide
nav_order: 1
---

### OFT Command Line

The OFT command line looks like this:

    oft command [option ...] [<input file or directory> ...]

or

    oft --help

Where `command` is one of

* `trace` - create a requirement trace document
* `convert` - convert to a different requirements format
* `help` - display a help message showing the command line usage and version of OFT

and `option` is one or more of the options listed below.

#### Display a Short Help Message

The following commands are equivalent and all display the command line usage and the version of OFT.

    oft -h
    oft --help
    oft help

#### Import options

    -a, --wanted-artifact-types <artifact type>[,...]

Import only specification items where the artifact type matches one of the listed types.

    -w, --wanted-statuses <status>[,...]

Import only specification items that have a status contained in the comma-separated list of statuses.

    -t, --wanted-tags [_,]<tag>[,...]

Import only specification items that have at least one of the listed tags. If you add a single underscore "_" as the first entry in the list, specification items that have no tags at all are also imported.

#### Tracing options

    -o, --output-format <format>

The format of the report.

One of:
* `plain`
* `html`
* `aspec`

Defaults to `plain`.

    --v, --report-verbosity <level>

The verbosity of the tracing report.

* `quiet` - no output (in case only the return code is used)
* `minimal` - display `ok` or `not ok`
* `summary` - display only the summary, not individual specification items
* `failures` - list of defect specification items
* `direct_failures` - list of specification items with non-transitive defects
* `failure_summaries` - list of summaries for defect specification items
* `direct_failure_summaries` - list of summaries for specification items with non-transitive defects
* `failure_details` - summaries and details for defect specification items
* `direct_failure_details` - summaries and details for specification items with non-transitive defects
* `overview` - summaries, link details and tags for all specification items, without the description
* `all` - summaries and details for all specification items

Defaults to `failure_details`.

    --details-section-display <status>

Initial display status of the details section in the HTML report

* `collapse` - hide details (default)
* `expand` - show details

#### Converting Options

    -o, --output-format <format>

Format into which requirements are converted.

One of
* `specobject`

Defaults to `specobject`.

#### Common Options

    -f, --output-file <path>

The output file or in case the output consists of more than one file, the output path. Defaults to STDOUT if not given.

    -i, --ignore-artifact-types <type<[,<type> ...]

Choose one or more artifact types which are going to be ignored during import. Affects specification items of that type, needed coverage and links to specification items of that type.

    -n, --newline <format>

Newline format, one of
* `unix`
* `windows`
* `oldmac`

Defaults to the platform standard if not given.

You can change the output color scheme.

    -c, --color=<color scheme>

The available color schemes are

`black-and-white`
:: Plain black and white. On the console this also means no font styles used.

`monochrome`
:: Black, white and shades of grey. Also enables font style on the console.

`color`
:: Color output. Also enables font style on the console.


    -l, --log-level <log level>
    
Log level for console logging. One of `OFF`, `SEVERE`, `WARNING`, `INFO`, `CONFIG`, `FINE`, `FINER`, `FINEST`, `ALL`. Defaults to `WARNING`.

---

← [Reference](reference.md) | ↑ [Reference](reference.md) | [Build Integration](build_integration.md) →
