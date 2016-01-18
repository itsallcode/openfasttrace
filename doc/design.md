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

If instead of relying on a linear search of all provider we use an index, the number of forward lookups is reduced to. We saw earlier that iterating over the requesters gives a a complexity of O(n). A proper index lookup should give O(log n) in the worst case. That leaves us with a complexity of O(n log(n)). Better.

### Choosing the Index

Since the specification item IDs inherently look similar, load tests need to show which kind of index is best balanced and has the optimal access time.

# Context

## Technical Constraints

## Conventions

# Building Block View

## Importers
For each specification artifact type OFT uses an importer. The importer uses the specification artifact as data source and reads specification items from it.

## Import Event Listeners
Importers emit events if they find parts of a specification item in the artifact they are importing.

## Command Line Interpreter
The Command Line Interpreter (CLI) takes parameters given to OFT and parses them. It is responsible for making sense of the parameter contents and issuing help and error messages about the command line syntax.

## Tracer
The Tracer consumes the list of requirements collected by the importers and evaluates the link status for each link.

## Reporter
The Reporter consumes the link status list and the specification item list and generates a report in the chosen output format. 

# Runtime View

## Tracing

### Backward tracing
`dsn~backward_coverage_status~1`

The Tracer component iterates over all covered IDs of all specification items and determines the backward coverage status of the link between the provider item and the requester item.

Covers:

  * `req~backward_coverage_status~1`

Needs: utest, impl

# Deployment View

# Concepts

## Data Structures

## Markdown-style Structures

### Specification Item ID Format
`dsn~md.specification_item_id_format~1` <a id="dsn~md.specification_item_id_format~1"></a>

A requirement ID in has the following format

    requirement-id = type "~" id "~" revision
    
    type = 1*ALPHA
    
    id = id-fragment *("." id-fragment)
    
    id-fragment = ALPHA *(ALPHA / DIGIT / "_")

    revision = 1*DIGIT

Rationale:

The ID must only contain characters that can be used in URIs without quoting. This makes linking in formats like Markdown or HTML clean and easy. 
Requirement type and revision must be immediately recognizable from the requirement ID. The built-in revision number makes links break if a requirement is updated - a desired behavior.

Covers:

  * `req~markdown_import~1`

Needs: impl, utest, uman

### Specification Item Title
`dsn~md.specification_item_title~1` <a id="dsn~md.specification_item_title"></a>

If a Markdown title directly precedes a specification item ID, then the Markdown title is used as title for the specification item.

Rationale:

Markdown titles show up in the outline and are a natural way of defining a requirment title.

Covers:

  * `req~markdown_import~1`

Needs: impl, utest, uman 

### Requirement references
`dsn~md.requirement_references~1` <a id="dsn~md.requirement_references~1"></a>

In Markdown specification item references have the following format:

    reference = (plain-reference | url-style-link)
    
    plain-reference = requirement-id
    
    url-style-link = "[" link-text "]" "(" "#" requirement-id ")"
    
Covers:

  * `req~markdown_import~1`
  * `req~markdown_standard_syntax~1`

Needs: impl, utest, uman

### Traced reference relations
`dsn~md.traced_reference_relations~1` <a id="dsn~md.traced_reference_relations~1"></a>

The Markdown importer interprets specification item reference relations as follows:

  1. Covers
  2. Depends

References of type "Covers" and "Depends" must be in a paragraph preceded by the relation name.

    covers-link = "Covers:" *SP 1*LINEBREAK *WSP "*" *WSP reference
    
    depends-link = "Depends:" *SP 1*LINEBREAK *WSP "*" *WSP reference

Only one traced reference per line is supported. Any optional text after the reference is ignored if it is separated by at least one whitespace character

Rationale:

Defining a link should be as natural and simple as possible in Markdown. It must also be rendered correctly by a regular Markdown renderer without modifications. Embedding links in lists to define the relationship looks nice and is language independent.

Covers:

  * `req~markdown_import~1`
  * `req~markdown_standard_syntax~1`

Needs: impl, utest, uman

## User Interface

### Command Line

#### Input File Selection
`dsn~input_file_selection~1` <a id="dsn~input_file_selection~1"></a>

The CLI accepts the following two variants for defining input files:

  * A list of files
  * A list of directories

In both cases relative and absolute paths are accepted. "Relative" means in relation to the current working directory.

Covers:

  * `req~input_file_selection~1`

#### Input File De-Duplication
`dsn~input_file_deduplication~1` <a id="dsn~input_file_deduplication~1~1></a>

The CLI generates a duplicate-free list of input files calculated form the inputs given via the command line.

Covers:

  * `req~input_file_selection~1`

# Design Decisions

## How do we Implement the Command Line Interpreter
`dsn~reflection_based_cli` <a id="dsn~reflection_based_cli"></a>

OFT got its own simple command line interpreter that uses reflection to feed the command line arguments to a receiver object.

Rationale:

One of the design goal of OFT is that it works without external runtime dependencies except for the Java Standard API. So taking an existing CLI was no option.
Using reflection allows the CLI user to implement the receiver as a POJO. No annotations are necessary.

Covers:

  * `req~input_file_selection~1`

### Why is This Architecture Relevant?

Exchanging the CLI later takes considerable effort.

### Alternatives considered

  * No CLI (plain argument list) - not flexible enough
  * External CLI - breaks design goal

# Bibliography

[bib.srs]: system_requirements.md "OpenFastTrack System Requirement Specification"
[bib.abnf]: ftp://ftp.rfc-editor.org/in-notes/std/std68.txt "Augmented BNF for Syntax Specifications: ABNF"
[bib.arc42]: http://arc42.org

## Specifications

  * [System Requirement Specification OpenFastTrack][bib.srs], Sebastian Bär
  * [Augmented BNF for Syntax Specifications: ABNF][bib.abnf] , D. Crocker, P. Overell, January 2008

## Web Sites

  * [arc42 - Ressources for software architects][bib.arc42], Dr. Gernot Starke, Dr. Peter Hruschka