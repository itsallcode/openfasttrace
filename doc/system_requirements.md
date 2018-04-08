<head><link href="oft_spec.css" rel="stylesheet"></link></head>

# System Requirement Specification OpenFastTrace

## Introduction

OpenFastTrace (OFT) is a requirement tracing suite written in Java.

### Goals

The OFT's goals are

* Fast
* Automation friendly
* Useful
* Platform independent

A requirement tracing suite must be fast in order to be used. Development time is precious and requirement tracing does not have a high priority compared to building and testing the software regularly. This means that tracing will only be accepted by the users if it does not eat away their development time.

Requirement tracing is a recurring and frankly quite boring task best performed by a machine instead of a person. Again the chances of getting accepted depend on getting this job "out of the way" and this means automating it. OFT therefore aims to offer easy integration into existing build processes, independently of the tool chains used.

It looks strange first that "useful" needs to be spelled out as a goal. The reason is that the authors felt that existing tracing tools are mostly optimized for producing proof that you worked according to process - i.e. reports that you can shelve until an quality auditor wants to see them. While OFT can do this too, it is not a main goal. Instead OFT wants to help developers and technical writers to find gaps and mismatches in their requirement coverage fast and with as little effort as possible.

OFT aims to help developers independently of their platform. I should not matter if you are developing on Linux, a Mac, BSD, Windows. Also it should not matter which programming language or development environment you are using. OFT aims to be portable and provide interfaces that allow integration into your existing toolchain.

### Terms and Abbreviations

The following list gives you an overview of terms and abbreviations commonly used in OFT documents.

* Artifact: a container for specification items
* Artifact type: the role of an artifact in a specification hierarchy
* Coverage: Specification items covering other specification items
* Coverage provider: a specification item that provides coverage
* Coverage requester: a specification item that needs coverage
* OFT: OpenFastTrace (this project)
* ReqM2: A requirement tracing suite
* Specification item: holds either a requirement or coverage
* Specification artifact: a data source containing specification items (e.g. file, ticket system,
    database)

In the following subsections central terms are explained in more detail.

#### Specification Items

In OFT requirements and artifacts covering them are represented by [specification items](#specification-item). Each item is a container for attributes of requirements and covering artifacts like the name, artifact type and the location where OFT found them.

A specification item can also contain information about its relationships to other specification items. For more details about those relationships check [section "tracing"](#tracing).

#### Coverage

Coverage is a measure of how well the tracing result matches the required relations between specification items.

Full coverage is what a project aims to achieve: all required relations between specification items exist.

An item is undercovered if at least one of the required relations is missing. It is overcovered if at least one relation exists that is not required.

## Features

### Requirement Tracing
`feat~requirement-tracing~1`

OFT traces requirements from specification to any kind of coverage (document, implementation, test, etc.).

Needs: req

### Markdown Import
`feat~markdown-import~1`

OFT imports specification items from Markdown files.

Rationale:

Markdown is a clean an simple format that:

* is viewable with any text editor
* focuses on content instead of layout
* is portable across platforms
* easy to process with text manipulation tools

For those reasons Markdown is a suitable candidate for writing specification that can be read and
maintained over a long time.

Needs: req

### ReqM2 Import
`feat~reqm2-import~1`

OFT imports specification items from ReqM2 files.

Rationale:

One of the OpenFastTrace design goals is to provide a Java-based drop-in replacement for ReqM2, so file format compatibility is essential.

Needs: req

### Coverage Tag Import
`feat~coverage-tag-import~1`

OFT imports coverage tags from source code files.

Rationale:

Coverage tags indicate parts of the source code that implements a certain requirement.

Needs: req

### ReqM2 Export
`feat~reqm2-export~1`

OFT exports specification items to ReqM2 files.

Rationale:

One of the OpenFastTrace design goals is to provide a Java-based drop-in replacement for ReqM2, so file format compatibility is essential.

Needs: req

### Tracing Reports

A tracing report is a representation of the results of the requirement tracing OFT performs. Depending on their use, reports can be designed to be human readable, machine readable or both.

#### Plain Text Report
`feat~plain-text-report~1`

OFT produces a tracing report in plain text.

Rationale:

This can be read directly as console output or with any text editor.

Needs: req

### User Interface

#### Command Line Interface
`feat~command-line-interface~1`

OFT offers a command line interface.

Rationale:

Running traces automatically in a scripted environment is the most important use case.

Needs: req

## Functional Requirements

### Anatomy of Specification Items

#### Specification Item
`req~specification-item~2`

A specification item consists of the following parts:

* ID
* Title (optional)
* Status (optional)
* Description (optional)
* Rationale (optional)
* Comment (optional)
* Covers (optional)
* Depends (optional)
* Needs (optional)
* Tags (optional)

The ID is a unique key through which the specification item can be referenced. It also contains the specification item type and revision number.

The title is a short summary of the specification item, mostly intended to appear in overview lists.

The status of the item is one of "approved", "proposed", "draft" and "rejected".

The description contains the normative part of the specification.

The rationale explains the reasoning behind a requirement or decision.

The "Covers" section contains a list of all specification item IDs that are covered by this item.

The "Depends" section contains a list of all specification item IDs that must be implemented in order for this item to be complete.

The "Needs" section list all artifact item types in which coverage for this item must be provided.

Tags are a way to label an artifact intended for grouping.

Needs: dsn

### Supported Formats

#### Markdown

Markdown is a simple ASCII-based markup format that is designed to be human readable in the source. While it can be rendered into HTML, it is perfectly eye-friendly even before rendering.

Markdown focuses on content over formatting by giving the document structure like headlines, paragraphs and lists. The combination of being lightweight, human-readable and structure-oriented makes it a good fit for writing specifications as code.

OFT defines a Markdown format that we call "Requirement-Enhanced Markdown" which is a superset of the regular Markdown. Any Markdown renderer can render this format without understanding it. The additional structural definitions tell OFT which part of the text is a specification item.

For backward compatibility OFT supports a variant of this format that was introduced at Elektrobit. This format is a little bit closer to ReqM2, the predecessor that sparked the OFT idea. We recommend using standard OFT Markdown format in new documents though since this format is cleaner.

##### Markdown Standard Syntax
`req~markdown-standard-syntax~1`

The OFT Markdown specification artifact format uses the standard markdown syntax without proprietary extensions.

Rationale:

The specification documents that the OFT Markdown importer reads must be viewable with any regular Markdown reader and as plain text.

Covers:

* [feat~markdown-import~1](#markdown-import)

Needs: dsn

##### Markdown Outline Readable
The Markdown outline -- a table of contents created from the heading structure by various Markdown editors -- must be human readable.

Rationale:

In long specification document the outline is the primary means of navigating the document. Only if the outline can be read easily, it is useful for authoring specification documents.

Covers:

* [feat~markdown-import~1](#markdown-import)

Needs: dsn

##### Support for EB Markdown Requirements
`req~eb-markdown~1`

In addition to OFT's requirement-enhanced markdown syntax OFT also supports Elektrobit's variant.

Rationale:

This allows stepwise migration to the OFT standard format. The Elektrobit format is a little bit closer to ReqM2.

Covers:

* [feat~markdown-import~1](#markdown-import)

Needs: dsn

#### Coverage Tags

Developers add coverage tags as comments to the source code to indicate where certain specification items are covered.

##### Import Full Coverage Tag Format
`req~import.full-coverage-tag-format~1`

OFT imports coverage tags from source files in a full format that contains all necessary information for tracing.

Covers:

* [feat~coverage-tag-import~1](#coverage-tag-import)

Needs: dsn

##### Import Short Coverage Tag Format
`req~import.short-coverage-tag-format~1`

OFT imports coverage tags from source files in a short format that requires additional configuration during import.

Covers:

* [feat~coverage-tag-import~1](#coverage-tag-import)

Needs: dsn

### Tracing

#### Outgoing Coverage Link Status
`req~tracing.outgoing-coverage-link-status~1`

_Outgoing coverage link_ means links that originate from a specification item and end at another specification item.
OFT determines the status of an outgoing coverage link of a specification item.

The possible results are:

  1. Covers:    link points to a specification item which wants this coverage
  2. Outdated:  link points to a specification item which has a higher revision number
  3. Predated:  link points to a specification item which has a lower revision number
  4. Ambiguous: link points to a specification item that has duplicates
  5. Unwanted:  coverage provider has an artifact type the provider does not want
  6. Orphaned:  link is broken - there is no matching coverage requester

Covers:

* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

#### Incoming Coverage Link Status
`req~tracing.incoming-coverage-link-status~1`

_Incoming coverage link_ means links that end at a specification item and originate at another specification item
OFT determines the incoming coverage link status of a requirement.

The possible results are:

  1. Covered shallow:  coverage provider for a required coverage exists
  2. Covered unwanted: coverage provider covers an artifact type the requester does not want
  3. Covered predated: coverage provider covers a higher revision number than the requester has
  4. Covered outdated: coverage provider covers a lower revision number than the requester has

Covers:

* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

#### Deep Coverage
`req~tracing.deep-coverage~1`

OFT marks a specification item as _covered deeply_ if this item - and all items it needs coverage from - are covered recursively.

Covers:

* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

#### Duplicate Items
`req~tracing.duplicate-items~1`

OFT marks a specification item as _duplicate_ if other items with the same ID exist.

Covers:

* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

#### Defect Items
`req~tracing.defect-items~2`

OFT marks a specification item as _defect_ if the following criteria apply

* The specification item has duplicates (i.e. another specification item with the same ID exists) _or_
* The item has any other status than "rejected" _and any of_
  * At least one outgoing coverage link has a different status than "Covers"
  * The item is not covered deeply

Covers:

* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

#### Link Cycle
`req~tracing.link-cycle~1`

OFT detects if specification items are linked in a cycle.

Rationale:
Link cycles are never intended and hard to find by hand in large collections of requirements.

Covers:

* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

### Strict and Relaxed Coverage

Users can choose between two modes of coverage determination: strict and relaxed.

Strict coverage means that the covering item must have a status of `Approved` in addition to the other criteria discussed in [section "Tracing"](#tracing). This is the mode users need for a final assessment of coverage ahead of a project release.

In Relaxed coverage mode OFT accepts any status but `Rejected` of the covering item. The reason why this mode is necessary is that if the team is using requirement states, then they will often have the situation that not all requirements in the document that needs to be covered are already approved. On the other hand the document that is supposed to provide the coverage can usually not wait to start covering until the input document is finalized. This would cause too much project delay. Relaxed mode allows the covering document to check whether all requirements are covered _before_ the input document is finally approved.

#### Strict and Relaxed Coverage Mode
`req~strict_and_relaxed_coverage_mode~1`

OFT allows users to choose between the following coverage evaluation modes:

1. Strict mode: covering items must be in status `Approved`
2. Relaxed mode: covering items must be in any other status than `Rejected`

Covers:

* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

### Partial Tracing
Usually the responsibility of document authors or coders when it comes to tracing is to make sure that they cover the input documents above. Only integrators or quality engineers are concerned with full chain coverage.

If the users try to run a regular trace without feeding in the artifacts all the way to the bottom level of the tracing chain, the coverage check will always report errors because of missing lower level coverage.

To mitigate the situation OFT allows users to ignore required coverage for selected artifact types.

Example:

Kim is a software architect and it is her job to cover the system requirements coming from Steve in her software architecture. Kim wants to make sure she did not forget to cover a system requirement and uses OFT to trace the two documents. The system requirement specification uses the artifact types `feat` and `req` where `req` covers the `feat` artifacts in the same document. Kim's architecture uses the artifact type `sysarch` which covers `req` and requires a detailed design `dsn`.

Obviously the detailed design is missing at the point when Kim runs the trace. To mitigate this situation Kim configures OFT to ignore all artifacts of type `dsn`, including the needed coverage. This allows Kim to validate coverage towards the system requirement without needing the detailed design document.

#### Include Only Artifact Types
`req~include-only-artifact-types~1`

OFT gives users the option to include only a configurable set of artifact types during processing.

Covers:

* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

#### Include Only Tags
`req~include-only-tags~1`

OFT gives users the option to include only specification items contained in configurable set of tags during processing.

Covers:

* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

### Reports
Reports are the main way to find out if a projects requirements are covered properly.

#### Common Report Functions

#### Plain Text Report
The plain text report is the most basic report variant. It serves two main purposes:

1. Input in build chains
2. Minimal requirement coverage view with the least dependencies. Any text terminal can display the plain text report.

##### Plain Text Report Summary
`req~reporting.plain-text.summary~2`

The summary in the plain text report includes:

* Result status
* Total number of specification items
* Total number of defect specification items (if any)

Covers:

* [feat~plain-text-report~1](#plain-text-report)

Needs: dsn

##### Plain Text Report Specification Item Overview
`req~reporting.plain-text.specification-item-overview~2`

An item summary consist in the plain text report includes

  1. Status
  2. Number of broken incoming links
  3. Total number of incoming links
  4. Number of broken outgoing links
  5. Total number of outgoing links
  6. Number of duplicates (not including this item)
  7. ID
  8. Status (unless "approved")
  9. Artifact types indicating coverage

Covers:

* [feat~plain-text-report~1](#plain-text-report)

Needs: dsn

##### Plain Text Report Link Details
`req~reporting.plain-text.link-details~1`

The link detail section shows for all links of a specification item:

  1. Incoming / Outgoing
  2. Link status
  3. ID of the specification item on the other end of the link

Covers:

* [feat~plain-text-report~1](#plain-text-report)

Needs: dsn

### Requirement Format Conversion
OFT supports conversion from one requirement format into another for example from Markdown to ReqM2.

Requirement conversion allows using the input formats OFT support in combination with external tracing tool chains by converting the inputs into the format the toolchain expects.

#### ReqM2 Export
`req~conversion.reqm2-export~1`

OFT exports to ReqM2's "SpecObject" format.

Covers:

* [feat~reqm2-export~1](#reqm2-export)

Needs: dsn

### User Interface

#### Requirement Tracing

##### Requirement Tracing Command
`req~cli.tracing.command~1`

OFT allows requirement tracing via the command line.

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

##### Tracing Output Format
`req~cli.tracing.output-format~1`

Users can select one of the following requirement tracing output formats (aka. "report formats"):

* Plain text

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

##### Default Tracing Output Format
`req~cli.tracing.default-output-format~1`

OFT uses plain text as requirement tracing output format if the user does not select one.

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~requirement-tracing~1](#requirement-tracing)

Needs: dsn

##### Tracing Exit Status

`req~cli.tracing.exit-status~1`

The return value of the OFT executable reflects the overall tracing result.

Covers:

* [feat~requirement-tracing~1](#requirement-tracing~1

Needs: dsn

#### Requirement Format Conversion

##### Requirement Conversion Command
`req~cli.conversion.command~1`

OFT allows requirement tracing via the command line.

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~reqm2-export~1](#reqm2-export)

Needs: dsn

##### Conversion Output Format
`req~cli.conversion.output-format~1`

Users can select one of the following conversion output formats (aka. "export formats"):

* ReqM2

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~reqm2-export~1](#reqm2-export)

Needs: dsn

##### Default Conversion Output Format
`req~cli.conversion.default-output-format~1`

OFT uses ReqM2 as default conversion format if the user does not select one.

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~reqm2-export~1](#reqm2-export)

Needs: dsn

#### Common

##### Input Selection
`req~cli.input-selection~1`

Users can select requirement sources as combinations of files and directories.

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~requirement-tracing](#requirement-tracing)

Needs: dsn

##### Input Directory Selection
`req~cli.input-directory-selection~1`

Users can select zero or more directories below which files and sub-directories are imported recursively.

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~markdown-import~1](#markdown-import)
* [feat~reqm2-import~1](#reqm2-import)

Needs: dsn

##### Default Input
`req~cli.default-input~1`

If users select neither files nor directories for input, OFT imports from the current working directory recursively.

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~markdown-import~1](#markdown-import)
* [feat~reqm2-import~1](#reqm2-import)

Needs: dsn

##### Newline Format
`req~cli.newline-format~1`

Users can choose between Unix-style and Windows-style newline encoding for outputs.

Rationale:

When users work together in teams where the team members use different platforms, configuring the newline helps the team to set a common standard.

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~reqm2-export~1](#reqm2-export)
* [feat~plain-text-report](#plain-text-report)

Needs: dsn

##### Default Newline Format
`req~cli.default-newline-format~1`

Unless selected by the user, OFT uses the native newline format of the platform it runs on for outputs.

Covers:

* [feat~command-line-interface~1](#command-line-interface)
* [feat~reqm2-export~1](#reqm2-export)
* [feat~plain-text-report](#plain-text-report)

Needs: dsn
