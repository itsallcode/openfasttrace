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

# Context

## Technical Constraints

## Conventions

# Building Block View

## Importers
For each specification artifact type OFT uses an importer. The importer uses the specification artifact as data source and reads specification items from it.

## Import Event Listeners
Importers emit events if they find parts of a specification item in the artifact they are importing. An Event

## Command Line Interpreter
The command line interpreter (CLI) takes parameters given to OFT and parses them. It is responsible for making sense of the parameter contents and issuing help and error messages about the command line syntax.

# Runtime View

# Deployment View

# Concepts

## Data Structures

## Markdown-style Structures

### Specification Item ID Format
`dsn~md.specification_item_id_format~1` <a id="dsn~md.specification_item_id_format~1"></a>

A requirement ID in has the following format

    requirement-id = type "" id "~" revision
    
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

If a Markdown title directly precedes a specification item ID, then the Markdown title is used a title for the specification item.

Rationale:

Markdown titles show up in the outline an are a natural way of defining a requirment title.

Covers:

  * `req~markdown_import~1`

Needs: impl, utest, uman 

### Requirement references
`dsn~md.requirement_references~1` <a id="dsn~md.requirement_references~1"></a>

In Markdown specification item references have the following format:

    reference = (plain-reference | url-style-link )
    
    plain-reference = requirement-id
    
    url-style-link = "[" link-text "]" "(" "#" requirement-id ")"
    
Covers:

  * `req~markdown_import~1`
  * `req~markdown_standard_syntax~1`

Needs: impl, utest, uman

### Traced reference relations
`dsn~md.traced_reference_relations~1` <a id="dsn~md.traced_reference_relations~1"></a>

The Markdown importer interprets specification item reference relations a follows:

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

### Why is this architecture relevant?

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