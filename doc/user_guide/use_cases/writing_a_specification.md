---
layout: default
title: Writing a Specification
parent: Use Cases
grand_parent: User Guide
nav_order: 1
---

### Writing a Specification

Preconditions:
* Text editor (preferably with syntax highlighting for [Markdown](https://daringfireball.net/projects/markdown/))

OFT's native format for writing specifications is [Markdown](https://daringfireball.net/projects/markdown/). Markdown is an easy to learn, easy to read markup format that can be written with any text editor and is typically rendered to HTML. For your convenience we recommend using an editor that provides at least syntax highlighting. A preview function is also helpful. In the best case it features an outline view too. Check ["Tools for Authoring OFT Documents"](../tool_support/tools_for_authoring_oft_documents.md#tools-for-authoring-oft-documents) for some suggestions.

While OFT introduces additional syntax rules so that it can distinguish between [informative](../../terminology.md#informative-passage) and [normative passages](../../terminology.md#normative-passage), all elements are valid Markdown.

Let's start with a minimal requirement:

    `req~this-is-the-id~1`
    
    This is the description of the requirement.

Simple as this. This is already a valid and complete OFT requirement. Of course, you can enrich the requirement with other information, but at the heart of it every requirement is an ID and a description.

It is mostly a matter of taste whether you prefer your specification items to have a title or not. The same requirement above with a title looks like this:

    ### The Requirement Title
    `req~this-is-the-id~1`
    
    This is the description of the requirement.

Since version 3.8.0 OFT also supports titles with underlines. Since Markdown only allows first level (H1) and second level (H2) titles to be underlined with '=' and '-' respectively and requirements are usually nested deeper into a document, we recommend sticking to the hash mark style of titles though. Underlined titles are mainly supported for compatibility with ReStructured Text (RST). 

    A Requirement Title With an Underline
    -------------------------------------
    `req~this-is-the-id~1`
    
    This is the description of the requirement.

The upside of giving requirements a title is that they appear in Markdown outline views. The downside is that they introduce redundancy in your specification and therefore have the tendency to become inconsistent with the content of the specification item. If you think in software design terms, the titles violate the ["Don't Repeat Yourself" principle (DRY)](https://en.wikipedia.org/wiki/Don't_repeat_yourself).

The number of hash marks in front of the title must adhere to the rules of Markdown, meaning that if you want to put a [specification item](../../terminology.md#specification-item) inside a section with a level two header, the item title must start with three hash marks.

At the moment the specification item above is a [terminating item](../../terminology.md#terminating-specification-item) because it does not require coverage by any [artifact type](../../terminology.md#artifact-type). Since a user level requirement always needs coverage in other artifact types, we are going to add this next.

    ### The Requirement Title
    `req~this-is-the-id~1`
    
    This is the description of the requirement.
    
    Needs: dsn, uman

Now the item must be covered in the design ("dsn") and user manual ("uman"). Remember you can introduce your own artifact types depending on the needs of your project.

Of course, you can embed specification items into normal Markdown text. This adds the necessary [informative](../../terminology.md#informative-passage) context that is required to understand the [normative passages](../../terminology.md#normative-passage).

    # ACME portable hole
       
    ## Introduction
   
    This document describes the user requirements for the ACME portable hole
    ...
    
    ## Functional Requirements
    
    This section lists the functional requirements of the ACME portable hole.
    Non-functional requirements are described in the section
    [quality scenarios](#quality-scenarios).
    
    ### The Requirement Title
    `req~this-is-the-id~1`
    
    This is the description of the requirement.
    
    Needs: dsn, uman

Requirements should be accompanied by a rationale in all cases where the reason for the requirement is not immediately obvious. A comment can be used for explanatory parts, warnings or other information that is neither normative nor fits into the rationale.

    `arch~acme-client-uses-exponential-back-off-strategy~1`
    
    If the ACME client cannot reach the ACME server, it uses a back-off strategy
    with exponentially growing retry interval.
    
    Rationale:
    If the ACME server comes up again after a failure, it would be under heavy
    load immediately if all clients tried to reestablish their connections at
    the same time. ...
    
    Comment:
    Since the implementation depends on the hardware capabilities of the client,
    the details are up to the detailed design.
    
    Needs: dsn 

`Needs`, `Rationale` and `Comment` are OpenFastTrace keywords that tell OpenFastTrace how to process the following content. There are other keywords in the context of specification items written in Markdown described in the following sections.

#### Keywords

Keywords are followed by a colon that separates the keyword from the content. Depending on the keyword, the content may start on the next line.

##### `Status`

The `Status` keyword takes a single value from `draft`, `proposed`, `approved`, `rejected` to set the status of the item. The status can be used to filter specification items during import (see [Import options](../reference/oft_command_line.md#import-options)). Has to occur before the `Description`, `Rationale` or `Comment`. 

    ### A draft spec items
    `req~draft-item~1`
    Status: draft
    
    This spec item is in the draft state and thus not considered final.
   

##### `Covers`

The `Covers` keyword states which items are covered by the current specification item. It is followed by a list of items that are covered, each one written on a new line starting with a bullet character (`+`, `*`, or `-`) followed by the referenced specification item id. 

Given the Feature `feat~rubber-ducky~1` exists and needs a `req`. A requirement that covers that feature could be written as

    ### Rubber ducky is made from latex
    `req~rubber-ducky-made-from-latex~1`
    
    The rubber ducky should be made from latex.
    
    Rationale:
    We'd like to avoid using materials made from crude oil and therefor use latex instead, because it is made from sustainable, regrowable resources.
    
    Covers:
    - feat~rubber-ducky~1

##### `Needs`

The `Needs` keyword states which artifact types are needed to cover the current specification item. It is followed by a list of artifact types that are needed, each one written on a new line starting with a bullet character (`+`, `*`, or `-`) followed by the artifact type abbreviation. `Needs` comes in two flavors: as one-liner or as list. 

**Variant a) one-line `needs`**

    Needs: impl, utest, itest

**Variant b) as List**

    Needs:
    - dsn
    - uman

Please note that you cannot mix the two styles in one specification item.

##### `Depends`

The `Depends` keyword defines dependencies between specification items. It is followed by a list of items the current specification item depends on, each one written on a new line starting with a bullet character (`+`, `*`, or `-`) followed by the referenced specification item id. At the moment this has no effect on the HTML or plaintext output, but only if the `-o aspec` option is used. This has no effect on the coverage of specification items.

    ### Depending specification item
    `req~depending-item~1`
    
    This item depends on two others. 
    
    Depends:
    - req~dependency-1~1
    - req~dependency-2~1

##### `Description`
 
This keyword is *optional*. Starts the text passage that describes a specification item. The description is automatically started with any non-empty text that does not start with another keyword. Has to occur before `Comment` or `Rationale`. The specification item 

    ### Specification item
    `feat~specification-item~1`
    
    Description:
    This is the description.
    
is functionally equivalent to

    ### Specification item
    `feat~specification-item~1`
    
    This is the description.

##### `Tags`

Tags are described in detail later in this document, see section [Distributing the Detailing Work](distributing_the_detailing_work.md#distributing-the-detailing-work).

---

← [Use Cases](use_cases.md) &nbsp;&nbsp;•&nbsp;&nbsp; ↑ [Use Cases](use_cases.md) &nbsp;&nbsp;•&nbsp;&nbsp; [Excluding Parts of a Specification Document for OFT Parsing](excluding_parts_of_a_specification_document_for_oft_parsing.md) →

### Quality Scenarios

*Note: This section is a placeholder referenced in the example above.*
