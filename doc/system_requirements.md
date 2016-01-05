System Requirement Specification OpenFastTrack

# Introduction

## Terminology

  * Specification item: holds either a requirement or coverage
  * Specification artifact: a data source containing specification items (e.g. file, ticket system,
    database)

# Features
## Requirement Tracing <a id="feat.requirement_tracing~1"/>
OFT traces requirements from specification to any kind of coverage (document, implementation, test, etc.)

Needs: req

## Requirement Import <a id="feat.requirement_import~1"/>
OFT imports requirements from different text formats. The default is Markdown.

Needs: req

# High Level Requirements

## Coverage Status of a Requirement <a id="req.coverage_status~1"/>
OFT determines the coverage status of a requirement. The possible options are:

  1. Uncovered: an specification item requires coverage but is not covered
  2. Covered: a specification item requires coverage and is covered 
  3. Over covered: coverage for a specification item was found that does not exist

Covers:

  * [Requirement Tracing](#feat.requirement_tracing~1)

Needs: dsn, uman

##  Specification Item <a id="req.specification_item~1"/>
A specification item consists of the following parts:

  * ID
  * Title (optional)
  * Description
  * Rationale (optional)
  * Comment (optional)
  * Covers (optional)
  * Depends (optional)
  * Needs

The ID is a unique key through which the specification item can be referenced. It also contains the
specification item type and revision number.

The title is a short summary of the specification item, mostly intended to appear in overview lists.

The description contains the normative part of the specification.

The rationale explains the reasoning behind a requirement or decision.

The "Covers" section contains a list of all specification items that are covered by this item.

The "Depends" section contains a list of all specification items that must be implemented in order
for this item to be complete.

The "Needs" section list all specification item types in which coverage for this item is needed.

Needs: dsn

## Requirement ID format <a id="req.requirement_format~1"/>
A requirement ID in has the following format

    requirement-id = type "." id "~" revision
    
    type = 1*ALPHA
    
    id = ALPHA *(ALPHA / DIGIT / "." / "_")
    
    revision = *DIGIT

Rationale:

The ID must only contain characters that can be used in URIs without quoting. This makes linking in formats like Markdown or HTML clean and easy. 
Requirement type and revision must be immediately recognizable from the requirement ID. The built-in revision number makes links break if a requirement is updated - a desired behavior.

## Markdown import <a id="req.markdown_import~1"/>
OFT imports specification items from Markdown

Covers:

  * [Requirement Import](#feat.requirement_import~1)

Needs: dsn, uman

### Requirement links <a id="req.md.requirement_links~1"/>
In Markdown requirement links have the following format:

    link = "[" link-text "]" "(" "#" requirement-id ")"
    
Covers:

  * [Requirement Import](#feat.requirement_import~1)

Needs: dsn, uman

### Link relations <a id="req.md.link_relations~1"/>
The Markdown importer interprets link relations a follows:

  1. Covers
  2. Details
  3. Depends
  4. Untraced

To define a link, a line needs to start with the following syntax:

    covers-link = *WSP "*" *WSP link
    
    details-link = *WSP "+" *WSP link
    
    depends-link = *WSP "-" *WSP link

Only one traced link per line is supported. Any optional text after the link is ignored if it is separated by at least one whitespace character

Rationale:

Defining a link should be as natural and simple as possible in Markdown. It must also be rendered correctly by a regular Markdown renderer without modifications. Embedding links in lists to define the relationship looks nice and is language independent.

Covers:

  * [Requirement Import](#feat.requirement_import~1)

Needs: dsn, uman