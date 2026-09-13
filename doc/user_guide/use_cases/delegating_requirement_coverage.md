---
layout: default
title: Delegating Requirement Coverage
parent: Use Cases
grand_parent: User Guide
nav_order: 3
---

### Delegating Requirement Coverage

Consider a situation where you are responsible for the high-level software architecture of your project. You define the component breakdown, the interfaces and the interworking of the components. You get your requirements from a system requirement specification, but it turns out many of those incoming requirements are at a detail level that does not require design decisions on inter-component-level but rather affects the internals of a single component.

In those cases it would be a waste of time to repeat the original requirement in your architecture just to hand them down to the detailed design of a component. Instead, what you need is a fast way to express "yes, I read that requirement, and I am sure it does not need design decisions in the high-level architecture."

To achieve this, OFT features a shorthand notation for delegating the job of covering a specification item to one or more different artifact types.

In the following example, a requirement in the system requirement specification (artifact type `req`) stated that the web user interface of your product should use the corporate design. This clearly does not require an architectural decision (`arch`), so you forward it directly to the detailed design (`dsn`) level.  

    arch --> dsn : req~web-ui-uses-corporate-design~1

Please note that the arrow is intentionally done with two dashes (`-->`) in order to reduce the chance for parsing collisions since the arrow with one dash often appears in documents. 

This notation can appear after:

* A title
* "Needs" section
* "Depends" section
* "Covers" section
* "Tags" section

If it appears in a multi-line text section of a requirement (description, comment or rationale) the forward is ignored.

Note that a forward terminates the previous specification item, so the following notation does not work:

    `dsn~foo~1`
    …
    Covers: req~foo~1
    
    dsn-->impl:req~bar~1              <-- this terminates the previous specification item
    
    Needs: impl,utest                 <-- this is now lost

To avoid confusion, it is best to have all forwards in a separate section with their own title:

    # Forwarded Requirements

    * `dsn-->impl:req~bar~1`
    * `dsn-->impl:req~zoo~2`
    * `…`

---

← [Excluding Parts of a Specification Document for OFT Parsing](excluding_parts_of_a_specification_document_for_oft_parsing.md) | ↑ [Use Cases](use_cases.md) | [Distributing the Detailing Work](distributing_the_detailing_work.md) →
