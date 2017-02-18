<head><link href="oft_spec.css" rel="stylesheet"></link></head>

# Introduction
This document is derived from the "[arc42][bib.arc42]" architectural template.

## Terminology
The terminology from the [system requirement specification][bib.srs] applies.

## Conventions

### Syntax Definitions
Syntax definitions in this document use the [Augmented Backus-Naur Form][bib.abnf].

The following definitions are used frequently throughout the document:

   * ANY - any valid character
   * LINEBREAK = the line break character of the platform

# Constraints

# Solution Ideas and Strategy

## Requirement tracing

The algorithm that checks the requirement links has to follow each link between requester and provider at least once.

Given:

  r<sub>n</sub>, n ∊ {1..N}

  p<sub>m</sub>, m ∊ {1..M}
  
  a<sub>n,l</sub>, l ∊ {1..L<sub>n</sub>}
  
  c<sub>m,k</sub>, k ∊ {1..M<sub>m</sub>}

Where _r_ are the requesters, _p_ the providers, _a_ artifacts required by providers and _c_ the coverage provided.

The number of forward links results to:

  f = Σ<sup>N</sup><sub>n = 1</sub> ( L<sub>n</sub> )

The number of backward links is:

  b = Σ<sup>M</sup><sub>n = 1</sub> ( K<sub>n</sub> )

### Lookups in the Naive Approach

The naive approach is to iterate over all required artifacts in all requesters and for each iterate over the complete set of providers to see if coverage exists.

If we assume a scenario where the coverage is 100% and a provider is found in average after iterating over half the providers this gives us the following number of Lookups _l_:

  l = f p / 2

Assuming that each requester needs to be covered by an average of _A_ artifacts we get:

  f = r A

  l = ( r  A ) p / 2

Assuming further that each provider has an average of _C_ back links to provide coverage we further get:

  p = r C

  l = ( r A ) ( r C ) / 2 = r² A C / 2

Note that _A_ and _C_ can be considered to have a small range in a typical project, depending only on the working style and not on the number of requirements the project need to handle.

In effect the lookup using the naive approach results in a complexity of O(n²). Not good.

### Lookups With an Index

If instead of relying on a linear search of all providers, we use an index. We saw earlier that iterating over the requesters results in a complexity of O(n). A proper index lookup should give O(log n) in the worst case. That leaves us with a complexity of O(n log(n)). Better.

### Choosing the Index

Since the specification item IDs inherently look similar, load tests need to show which kind of index is best balanced and has the optimal access time.

# Context

## Technical Constraints

## Conventions

# Building Block View

## Importers
For each specification artifact type OFT uses an importer. The importer uses the specification artifact as data source and reads specification items from it.

## Import Event Listeners
Importers emit events if they find parts of a [specification item](#specification-item) in the artifact they are importing.

## Command Line Interpreter
The command line interpreter (CLI) takes parameters given to OFT and parses them. It is responsible for making sense of the parameter contents and issuing help and error messages about the command line syntax.

## Linker
The linker is responsible for turning the imported [specification items](#specification-item) collected by the [importers](#importers) into [linked specification items](#linked-specification-item). 

## Tracer
The tracer consumes the list of [linked specification items](#linked-specification-item) and evaluates the link status for each link.


## Reporter
The Reporter consumes the link status list and the specification item list and generates a report in the chosen output format. 

# Runtime View

## Tracing

### Tracing Needed Coverage
`dsn~tracing.needed-coverage-status~1`

The [tracer](#tracer) component iterates over all needed artifacts of all specification items and determines if and which coverage exists for each.

Covers:

  * `req~tracing.needed-coverage-status~1`
  
Needs: utest, impl

### Outgoing Coverage Link Status
`dsn~tracing.outgoing-coverage-link-status~1`

The [linker](#linker) component determines the coverage status of the outgoing link between the provider item and the requester item.

The possible results are:

  1. Covers:    link points to a specification item which wants this coverage
  2. Outdated:  link points to a specification item which has a higher revision number
  3. Predated:  link points to a specification item which has a lower revision number
  4. Ambiguous: link points to a specification item that has duplicates
  5. Unwanted:  coverage provider has an artifact type the provider does not want
  6. Orphaned:  link is broken - there is no matching coverage requester

Covers:

  * `req~tracing.outgoing-coverage-link-status~1`

Needs: utest, impl

### Incoming Coverage Link Status
`dsn~tracing.incoming-coverage-link-status~1`

The [linker](#linker) component determines the coverage status of the incoming link between the requester item and the provider item.

The possible results are:

  1. Covered shallow:  coverage provider for a required coverage exists
  2. Covered unwanted: coverage provider covers an artifact type the requester does not want
  3. Covered predated: coverage provider covers a higher revision number than the requester has
  4. Covered outdated: coverage provider covers a lower revision number than the requester has

Covers:

  * `req~tracing.incoming-coverage-link-status~1`

Needs: impl, utest, uman

### Deep Coverage
`dsn~tracing.deep-coverage~1`

The [tracer](#tracer) marks a [specification item](#specification-item) as _covered deeply_ if this item - and all items it needs coverage from - are covered recursively.

Covers:

  * `req~tracing.deep-coverage~1`

Needs: impl, utest, uman

### Duplicate Items
`dsn~tracing.tracing.duplicate-items~1`

The [tracer](#tracer) marks a [specification item](#specification-item) as a _duplicate_ if other items with an identical [specification item ID](#specification-item-id) exists

Covers:

  * `req~tracing.duplicate-items~1`

Needs: impl, utest, uman


### Defect Items
`dsn~tracing.defect-items~1`

The [tracer](#tracer) marks a [specification item](#specification-item) as _defect_ if any of the following criteria apply

  1. The specification item has duplicates
  2. At least one outgoing coverage link has a different status than "Covers"
  3. The item is not covered deeply
  
Covers:

  * `req~tracing.defect-items~1`

Needs: dsn

## Tracing Reports

### Plain Text Report

#### Plain Text Report Summary
`dsn~reporting.plain-text-summary~1`

The summary in the plain text report includes:

  * Result status
  * Total number of specification items
  * Total number of specification items that are not covered (if any)
  * Total number of duplicate specification items (if any)

Covers:

  * `req~reporting.plain-text-summary~1`

Needs: dsn

#### Plain Text Report Specification Item Overview
`dsn~reporting.plain-text.specification-item-overview~1`

An item summary consist in the plain text report includes

  1. Status
  2. Number of broken incoming links 
  3. Total number of incoming links
  4. Number of broken outgoing links
  5. Total number of outgoing links
  6. Number of duplicates (not including this item)
  7. ID
  8. Artifact types indicating coverage
  9. Source file name and line (optional)

Covers:

  * `req~reporting.plain-text.specification-item-overview~1`

Needs: dsn

#### Plain Text Report Options
`dsn~reporting.plain-text.report-options~1`

The following parts of the plain text reports can be switched on and off:

  * Summary
  * Overview per defect specification item
  * Overview per clean specification item
  * Source file name and line
  * Specification item description

Covers:

  * `req~reporting.plain-text.report-options~1`

Needs: dsn

#### Default Plain Text Report
`dsn~reporting.plain-text.default-report-options~1`

The following options are set by default in the plain text report unless options are specified by the user:

  * Summary
  * Overview per defect specification item
  * Source file name and line

Covers:

  * `req~reporting.plain-text.default-report-options~1`

Needs: dsn

## Requirement Format Conversion

#### ReqM2 Export
`dsn~conversion.reqm2-export~1`

OFT exports to ReqM2's "SpecObject" format.

Comment:
The ReqM2 format is specified in the ReqM2 handbook by Elektrobit.

Covers:

  * `req~conversion.reqm2-export~1`

Needs: impl, utest, uman

# Deployment View

# Concepts

## Data Structures

### Internal Data Structures

#### Specification Item
`dsn~specification-item~1`

A `SpecificationItem` consists of the following parts:

  * ID (`SpecificationItemId`)
  * Title (`String`, optional)
  * Description (`String`, optional)
  * Rationale (`String`, optional)
  * Comment (`String`, optional)
  * Source file + line (`String`, `int`, optional)
  * Covers (List of `SpecificationItemId`, optional)
  * Depends (List of `SpecificationItemId`, optional)
  * Needs (List of `String`, optional)

Covers:

  * `req~specification-item~1`

Needs: impl, utest, uman

#### Linked Specification Item
`dsn~linked-specification-item~1`

A `LinkedSpecificationItem` is a subclass of the [SpecificationItem](#specification-item) that is enriched with references to other LinkedSpecificationItems.

Rationale:
This allows navigating between specification items.

Covers:

  * `req~specification-item~1`

Needs: impl, utest, uman

#### Specification Item Id
`dsn~specification-item-id~1`

A `SpecificationItemId` consists of:

  * Artifact type (String)
  * name (String)
  * revision (number)

Covers:

  * `req~specification-item~1`

Needs: impl, utest, uman

### Markdown-style Structures

#### Markdown Specification Item ID Format
`dsn~md.specification-item-id-format~2`

A requirement ID has the following format

    requirement-id = type "~" id "~" revision
    
    type = 1*ALPHA
    
    id = id-fragment *("." id-fragment)
    
    id-fragment = ALPHA *(ALPHA / DIGIT / "_" / "-")

    revision = 1*DIGIT

Rationale:

The ID must only contain characters that can be used in URIs without quoting. This makes linking in formats like Markdown or HTML clean and easy. 
Requirement type and revision must be immediately recognizable from the requirement ID. The built-in revision number makes links break if a requirement is updated - a desired behavior.

Comment:

Note that the artifact type is integral part of the ID. That means that `dsn~my-requirement~1` is something completely different then `utest~my-requirement~1`. One of the benefits of making the artifact type mandatory part of the ID is that this allows for typical coverage chains like.

    req~my-requirement~2 -> dsn~my-requirement~4 -> impl~my-requirement~4

Otherwise users would be forced to invent different names for each link in the chain.

Covers:

  * `req~markdown-import~1`

Needs: impl, utest, uman

#### Markdown Specification Item Title
`dsn~md.specification-item-title~1`

If a Markdown title directly precedes a specification item ID, then the Markdown title is used as title for the specification item.

Rationale:

Markdown titles show up in the outline and are a natural way of defining a requirement title.

Covers:

  * `req~markdown-import~1`

Needs: impl, utest, uman 

#### Markdown Requirement References
`dsn~md.requirement-references~1`

In Markdown specification item references have the following format:

    reference = (plain-reference | url-style-link)
    
    plain-reference = requirement-id
    
    url-style-link = "[" link-text "]" "(" "#" requirement-id ")"
    
Covers:

  * `req~markdown-import~1`
  * `req~markdown-standard-syntax~1`

Needs: impl, utest, uman

#### Markdown "Covers" list
`dsn~md.covers_list~1`

The Markdown Importer supports the following format for links that cover a different specification item.

    covers-list = covers-header 1*(LINEBREAK covers-line)
    
    covers-header = "Covers:" *WSP
    
    covers-line = *WSP "*" *WSP reference

Only one traced reference per line is supported. Any optional text after the reference is ignored if it is separated by at least one whitespace character

Rationale:

Defining a link should be as natural and simple as possible in Markdown. It must also be rendered correctly by a regular Markdown renderer without modifications. Embedding links in lists to define the relationship looks nice and is language independent.

Covers:

  * `req~markdown-import~1`
  * `req~markdown-standard-syntax~1`

Needs: impl, utest, uman

#### Markdown "Depends" List
`dsn~md.depends_list~1`

The Markdown Importer supports the following format for links to a different specification item which the current depends on.

    depends-list = depends-header 1*(LINEBREAK depends-line)
    
    depends-header = "Depends:" *WSP
    
    depends-line = *WSP "*" *WSP reference

Only one traced reference per line is supported. Any optional text after the reference is ignored if it is separated by at least one whitespace character

Rationale:

Defining a link should be as natural and simple as possible in Markdown. It must also be rendered correctly by a regular Markdown renderer without modifications. Embedding links in lists to define the relationship looks nice and is language independent.

Covers:

  * `req~markdown-import~1`
  * `req~markdown-standard-syntax~1`

Needs: impl, utest, uman

#### Markdown "Needs" List
`dsn~md.needs_coverage_list~1`

The Markdown Importer supports the following format for defining the list of artifact types that are needed to fully cover the current specification item.

    needs-list = "Needs:" *WSP reference *("," *WSP reference)

Covers:

  * `req~markdown-import~1`
  * `req~markdown-standard-syntax~1`

## User Interface

### CLI Command Selection
`dsn~cli.command-selection~1`

The CLI expects one of the following commands as first unnamed command line parameter:

    command = "trace" / "convert"

Covers:

  * `req~cli.tracing.command~1`
  * `req~cli.conversion.command~1`

Needs: impl, utest, uman

### Common

#### Input File Selection
`dsn~cli.input-file-selection~1`

The CLI accepts the following two variants for defining input files:

  * A list of files
  * A list of directories

In both cases relative and absolute paths are accepted. "Relative" means in relation to the current working directory.

Covers:

  * `req~cli.input-selection~1`
  
Needs: impl, utest, uman

#### Input Directory Recursive Traversal
`dsn~input-directory-recursive-traversal~1`

The Importer reads all requirement input files from all input directories recursively.

Covers:

  * `req~cli.input-directory-selection~1`

Needs: impl, utest, uman

#### Default Input
`dsn~cli.default-input~1`

If the user does not specify any inputs as CLI parameters, the CLI uses the current working directory as default input.

Covers:

  * `req~cli.default-input~1`

Needs: impl, utest, uman

#### Input File Deduplication
`dsn~cli.input-file-deduplication~1`

The CLI generates a duplicate-free list of input files calculated form the inputs given via the command line.

Covers:

  * `req~cli.input-deduplication~1`

Needs: impl, utest, uman

#### Newline Format
`dsn~newline-format~1`

The CLI accepts one of the following newline formats:

    new-line-format = "unix" / "windows"

Rationale:

When users work together in teams where the team members use different platforms, configuring the newline helps the team to set a common standard.

Covers:

  * `req~cli.newline-format~1`

Needs: impl, utest, itest, uman

#### Default Newline Format
`dsn~cli.default-newline-format~1`

If the user does not specify the newline format as parameter, the importer uses the native newline format of the platform OFT is executed on.

Covers:

  * `req~cli.default-newline-format~1`

Needs: impl, itest, uman

### Requirement Tracing

#### Tracing Output Format
`dsn~cli.tracing.output-format~1`

The CLI accepts one of the following requirement tracing report formats as parameter:

    report-formats = "plain"

Covers:

  * `req~cli.tracing.output-format~1`

Needs: impl, utest, uman

#### Default Tracing Output Format
`dsn~cli.tracing.default-format~1`

The CLI uses plain text as requirement tracing report format if none is given as a parameter.

Covers:

  * `req~cli.tracing.default-output-format~1`

Needs: impl, utest, uman

#### Tracing Exit Status
`dsn~cli.tracing.exit-status~1`

The return value of the OFT executable is:

    * `0` tracing was successful
    * `1` tracing ran successfully, but the tracing result is negative

Covers:

  * `req~cli.tracing.exit-status~1`

Needs: impl, utest, uman

### Requirement Format Conversion

#### Conversion Output Format
`dsn~cli.conversion.output-format~1`

The CLI accepts one of the following export formats as parameter:

    export-formats = "reqm2"

Covers:

  * `req~cli.conversion.output-format~1`

Needs: impl, utest, uman

#### Default Conversion Output Format
`dsn~cli.conversion.default-format~1`

The CLI uses ReqM2 as export format if none is given as a parameter.

Covers:

  * `req~cli.conversion.default-output-format~1`

Needs: impl, utest, uman

# Design Decisions

## How do we Implement the Command Line Interpreter
`dsn~reflection-based-cli~1`

OFT got its own simple command line interpreter that uses reflection to feed the command line arguments to a receiver object.

Rationale:

One of the design goal of OFT is that it works without external runtime dependencies except for the Java Standard API. So taking an existing CLI was no option.
Using reflection allows the CLI user to implement the receiver as a POJO. No annotations are necessary.

Covers:

  * `req~cli.tracing.command~1`
  * `req~cli.conversion.command~1`

### Why is This Architecture Relevant?

Exchanging the CLI later takes considerable effort.

### Alternatives considered

  * No CLI (plain argument list) - not flexible enough
  * External CLI - breaks design goal

# Bibliography

The following documents or are referenced in this specification.

[bib.srs]: system_requirements.md "OpenFastTrack System Requirement Specification"
[bib.abnf]: ftp://ftp.rfc-editor.org/in-notes/std/std68.txt "Augmented BNF for Syntax Specifications: ABNF"
[bib.arc42]: http://arc42.org

## Specifications

  * [System Requirement Specification OpenFastTrack][bib.srs], Sebastian Bär
  * [Augmented BNF for Syntax Specifications: ABNF][bib.abnf] , D. Crocker, P. Overell, January 2008

## Web Sites

  * [arc42 - Ressources for software architects][bib.arc42], Dr. Gernot Starke, Dr. Peter Hruschka