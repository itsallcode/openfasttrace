# Introduction

## Terminology

The terminology from the [system requirement specification][bib.srs] applies.

## Conventions

### Syntax Definitions
Syntax definitions in this document use the [Augmented Backus-Naur Form][bib.abnf].

The following definitions are used frequently throughout the document:

   * ANY - any valid character
   * LINEBREAK = the line break character of the platform

# Constraints

## Technical Constraints

## Conventions

# Functional view

## Markdown Import

### Specification Item ID Format
`req~md.specification_item_id_format~1` <a id="req~md.specification_item_id_format~1"></a>

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

  * req~markdown_import~1

Needs: impl, utest, uman

### Specification Item Title
`req~md.specification_item_title~1` <a id="req~md.specification_item_title">

If a Markdown title directly precedes a specification item ID, then the Markdown title is used a title for the specification item.

Rationale:

Markdown titles show up in the outline an are a natural way of defining a requirment title.

Covers:

  * req~markdown_import~1

Needs: impl, utest, uman 

### Requirement references
`req~md.requirement_references~1` <a id="req~md.requirement_references~1"></a>

In Markdown specification item references have the following format:

    reference = (plain-reference | url-style-link )
    
    plain-reference = requirement-id
    
    url-style-link = "[" link-text "]" "(" "#" requirement-id ")"
    
Covers:

  * feat~requirement_import~1

Needs: impl, utest, uman

### Traced reference relations
`req~md.traced_reference_relations~1` <a id="req~md.traced_reference_relations~1"></a>

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

  * [Requirement Import](#feat~requirement_import~1)

Needs: impl, utest, uman

# Composition

# Modules

## Importers
For each specification artifact type OFT uses an importer. The importer uses the specification artifact as data source and reads specification items from it.

## Import Event Listeners
Importers emit events if they find parts of a specification item in the artifact they are importing. An Event

# Bibliography

[bib.srs]: system_requirements.md "OpenFastTrack System Requirement Specification"
[bib.abnf]: ftp://ftp.rfc-editor.org/in-notes/std/std68.txt "Augmented BNF for Syntax Specifications: ABNF"

* [System Requirement Specification OpenFastTrack][bib.srs], Sebastian Bär
* [Augmented BNF for Syntax Specifications: ABNF][bib.abnf] , D. Crocker, P. Overell, January 2008
