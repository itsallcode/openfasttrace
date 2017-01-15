<head><link href="oft_spec.css" rel="stylesheet"></link></head>

System Requirement Specification OpenFastTrack

# Introduction

## Terminology

  * OFT: OpenFastTrack (this project)
  * Specification item: holds either a requirement or coverage
  * Specification artifact: a data source containing specification items (e.g. file, ticket system,
    database)
  * Coverage requester: a specification item that need coverage
  * Coverage provider: a specification item that provides coverage

# Features
## Requirement Tracing
`feat~requirement_tracing~1` <a id="feat~requirement_tracing~1"></a>

OFT traces requirements from specification to any kind of coverage (document, implementation, test, etc.)

Needs: req

## Requirement Import
`feat~requirement_import~1` <a id="feat~requirement_import~1"></a>

OFT imports requirements from different text formats. The default is Markdown.

Needs: req

## Reporting

### Plain Text Report
`feat~plain_text_report~1`<a id="feat~plain_text_report~1"></a>

OFT produces a report in plain text that can be read directly as console output or with any text editor.

Needs: req

# High Level Requirements

## Anatomy of Specification Items

### Specification Item
`req~specification_item~1` <a id="req~specification_item~1"></a>

A specification item consists of the following parts:

  * ID
  * Title (optional)
  * Description
  * Rationale (optional)
  * Comment (optional)
  * Covers (optional)
  * Depends (optional)
  * Needs

The ID is a unique key through which the specification item can be referenced. It also contains the specification item type and revision number.

The title is a short summary of the specification item, mostly intended to appear in overview lists.

The description contains the normative part of the specification.

The rationale explains the reasoning behind a requirement or decision.

The "Covers" section contains a list of all specification item IDs that are covered by this item.

The "Depends" section contains a list of all specification item IDs that must be implemented in order
for this item to be complete.

The "Needs" section list all artifact item types in which coverage for this item is needed.

Needs: dsn

### Outgoing Coverage Link Status
`req~outgoing_coverage_link_status~1` <a id="req~outgoing_coverage_link_status~1"></a>

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

  * [feat~requirement_tracing~1](#feat~requirement_tracing~1)

Needs: dsn

### Incoming Coverage Link Status
`req~incoming_coverage_link_status~1` <a id="req~incoming_coverage_link_status~1"></a>

_Incoming coverage link_ means links that end at a specification item and originate at another specification item
OFT determines the incoming coverage link status of a requirement.

The possible results are:

  1. Covered shallow:  coverage provider for a required coverage exists
  2. Covered unwanted: coverage provider covers an artifact type the requester does not want
  3. Covered predated: coverage provider covers a higher revision number than the requester has
  4. Covered outdated: coverage provider covers a lower revision number than the requester has

Covers:

  * [feat~requirement_tracing~1](#feat~requirement_tracing~1)

Needs: dsn

### Deep Coverage
`req~deep_coverage~1` <a id="req~deep_coverage~1"></a>

OFT marks a specification item as _covered deeply_ if this item and all items it needs coverage from are covered recursively.

Covers:

  * [feat~requirement_tracing~1](#feat~requirement_tracing~1)

### Duplicate Items
`req~duplicate_items~1` <a id="req~duplicate_items~1></a>

OFT marks a specification item as a _duplicate_ if other items with the same ID exist.

Covers:

  * [feat~requirement_tracing~1](#feat~requirement_tracing~1)


### Defect Items
`req~defect_items~1` <a id="req~defect_items~1"></a>

OFT marks a specification item as _defect_ if any of the following criteria apply

  1. The specification item has duplicates (i.e. another specification item with the same ID exists)
  2. At least one outgoing coverage link has a different status than "Covers"
  3. The item is not covered deeply
  
Covers:

  * [feat~requirement_tracing~1](#feat~requirement_tracing~1) 

## Import
### Input File Selection
`req~input_file_selection~1` <a id="req~input_file_selection~1"></a>

Users select the input files either directly or indirectly via directories. In case users give directories, all files below each directory and sub-directories are used as input.

Rationale:

The 90% case will be scanning a single project directory and using the contents for tracing. There are scenarios though, where file lists generated by external tools need to be processed.

Covers:

  * [feat~requirement_import~1](#feat~requirement_import~1)

Needs: dsn

### Markdown Import
`req~markdown_import~1` <a id="req~markdown_import~1"></a>

OFT imports specification items from Markdown.

Rationale:

Markdown is a clean an simple format that:

  * is viewable with any text editor
  * focuses on content instead of layout
  * is portable across platforms
  * easy to process with text manipulation tools
  
For those reasons Markdown is a suitable candidate for writing specification that can be read and
maintained over a long time.

Covers:

  * [feat~requirement_import~1](#feat~requirement_import~1)

Needs: dsn

#### Markdown Standard Syntax
`req~markdown_standard_syntax~1` <a id="req~markdown_standard_syntax~1"></a>

The OFT Markdown specification artifact format uses the standard markdown syntax without proprietary extensions.

Rationale:

The specification documents that the OFT Markdown importer reads must be viewable with any regular Markdown reader and as plain text.

Covers:

  * [feat~requirement_import~1](#feat~requirement_import~1)

Needs: dsn

#### Markdown Outline Readable
The Markdown outline -- a table of contents created from the heading structure by various Markdown editors -- must be human readable.

Rationale:

In long specification document the outline is the primary means of navigating the document. Only if the outline can be read easily, it is useful for authoring specification documents.

Covers:

  * [feat~requirement_import~1](#feat~requirement_import~1)

Needs: dsn

## Tracing

### Exit Status According to Tracing Result

`req~exit_status~1` <a id="req~exit_status~1"/></a>

The return value of the OFT executable reflects the overall tracing result.
 
Covers:

  * [feat~plain_text_report~1](#feat~plain_text_report~1)

Needs: dsn

## Reports
Reports are the main way to find out if a projects requirements are covered properly.

### Report Verbosity Levels
`req~report_verbosity_levels~1` <a id="req~report_verbosity_levels~1"></a>

When running a report, users can choose between the following report levels:

  * Quiet - no output
  * Minimal - output states if all items are properly covered or not
  * Summary - summary with aggregated coverage statistics
  * Failures - List of all failed item IDs
  * Failure details - List of all failed items and an overall summary
  * All - List of all items and a summary

Covers:

  * [feat~plain_text_report~1](#feat~plain_text_report~1)

Needs: dsn

### Plain Text Report
The plain text report is the most basic report variant. It serves two main purposes:

1. Input in build chains
2. Minimal requirement coverage view with the least dependencies. Any text terminal can display the plain text report.

#### Item Links Status
`req~ptr.level_failure_details~1` <a id="req~ptr.item_link_summary~1"></a>

On failure detail verbosity level the plain text report displays:

Per specification item
  1. Status
  2. Number of broken incoming links 
  3. Total number of incoming links
  4. Number of broken outgoing links
  5. Total number of outgoing links
  6. Number of duplicates (not including this item)
  7. ID
  8. Description

A summary
  1. Overall status
  2. Total number of links per status
  3. Total number of duplicates

Covers:

  * [feat~plain_text_report~1](#feat~plain_text_report~1)

Needs: dsn