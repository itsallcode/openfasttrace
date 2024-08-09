![oft-logo](../core/src/main/resources/openfasttrace_logo.svg)

# OpenFastTrace (OFT) User Guide

## In a Nutshell

OFT is a requirement tracing tool. It helps you make sure that all defined requirements are covered in your code. It
also helps you find outdated code passages.

1. Create requirement and specification documents in Markdown including OFT-readable specification items
2. Put tags into your source code that mark the coverage of items from the specification
3. Use OFT to trace the requirements from the source to the final implementation

## Introduction

### Who Should Read This Document?

This document is mainly targeted at users wanting to learn how to use OFT for authoring requirement specifications, tracing requirement coverage and converting between requirement formats.

In software projects those users are typically:

* Technical writers
* Requirement engineers
* Software developers
* Quality engineers

For this class of users all sections are of interest.

The document is also helpful for people who just want to get an insight on what OFT is good for and the ideas behind it like:

* Project managers
* IT Support personnel
* People responsible for picking tools (who we recommend should be users too)

Of course requirement engineering and tracing are useful outside the software domains too.

### What is Requirement Tracing?

OpenFastTrace is a requirement tracing suite. Requirement tracing helps you to keep track of whether you actually implemented everything you planned to in your specifications. It also identifies obsolete parts of your product and helps you to get rid of them.

The foundation of all requirement tracing are links between documents, implementation, test, reports and whatever other artifacts your product consists of.

Let's assume you compiled a list of five main features your users asked for. They are very coarse but provide a nice overview of what your project is expected to achieve. Next you decide to write a few dozen user stories to flesh out the details of what your users want.

In order not to forget anything important, you create a link from each user story to the corresponding feature.

After you are done you want to make sure everything is in order. Instead of checking all the links by hand, you let OFT check if every feature is covered in at least one user story.

OFT comes back with a result that one of your features is not covered. You realize that you indeed forgot that one, write two new user stories and link them to the so far uncovered feature. This time OFT comes back with an assuring "OK".

Step-by-step you repeat this pattern for your design document and all resulting artifacts.

### Why do I Need Requirement Tracing?

Requirement tracing is a safety net for non-trivial software projects:

* protects you from forgetting planned parts of your product
* finds orphaned code, documents and resources
* helps you track progress towards milestones
* allows you to prove due diligence during quality audits and customer reviews

### Concepts and Terms

There are some often used terms in the OFT documentation that stand for concepts you should be familiar with when using OFT.

#### Specification Item

"Specification Item" is the general term we use to denominate all normative pieces of specifications and markers to their coverage in the implementation.

Examples:
* Feature definitions
* Requirements in a system requirement specification
* Markers in implementation and tests that signal [coverage](#coverage)

We use this term to better distinguish between the accepted use of the word "requirement" which most people only use for specification items found in requirement documents and the broader use that includes coverage markers.

#### Specification Item ID

The identifier (ID) of a [specification item](#specification-item) is a project-globally unique key which is used to refer to a specification item.

The specification item ID consists of the following parts:
* [Artifact type](#specification-item-artifact-type)
* [Name](#specification-item-name)
* [Revision](#specification-item-revision)

All parts are integral to the ID. The name alone is neither unique nor complete. In OFT's native document formats the ID is represented as a character string where the three parts are separated by the tilde (`~`) symbol.

Examples:

    feat~html-export~1
    req~html5-exporter~1
    dsn~html5-exporter~1
    utest~html5-exporter~4

The following sections explain the each of the three parts in detail.

##### Specification Item Artifact Type

The artifact type serves two purposes:

1. identifying the source document type
2. identifying the position in the tracing hierarchy

Artifact types are represented by character strings consisting out of ASCII letters. No other characters are allowed.

While not enforced by OFT the following strings are well established:

* `feat` - high-level feature
* `req` - user requirement
* `arch` - architectural requirement
* `dsn` - design requirement
* `impl` - implementation
* `utest` - unit test
* `itest` - integration test
* `stest` - system test
* `uman` - user manual
* `oman` - operation manual

If you don't distinguish between architectural and detailed design we recommend using `dsn` for both. The OFT specification for example does it that way.

How many types you introduce, how you name and stack them is up to you. When we designed OFT, we were clear about the fact that we would not be able to cover all possible artifact types one could imagine, so we did not hardcode them into OFT.

##### Specification Item Name

The name part of the ID must be a character string consisting of Unicode letters and/or numbers separated by underscore (`_`), hyphen (`-`) or dot (`.`). Whitespaces are not allowed.

* Names must start with a unicode letter
* Consecutive dots `.` are not allowed

We recommend using a dot `.` to create a hierarchy of items:

    exporter.html5.folding
    exporter.html5.colors
    exporter.csv.column_names

##### Specification Item Revision

The revision number of a specification item is a positive integer number that can be started at zero but by convention usually is started at one.

The revision is intended to obsolete existing coverage links in case the content of a specification item semantically changed. Incrementing the revision voids all existing links to this item so that authors linking to the item know they have to check for changes and adapt the covering items.

Examples:

If you change a requirement that lists all browsers that an HTML export needs to be compatible with, you made a semantic change and should raise the revision number.

If on the other hand you only added a missing period at the end of a sentence, the requirement content did not really change and there is no need to invalidate existing coverage. 

#### Informative Passages

Informative passages of a specification provide explanations and context that is necessary for understanding the subject. They do not require coverage though.

#### Normative Passages

Normative passages contain requirements (or in OFT terms ["specification items"](#specification-item)). Unlike [informative passages](#informative-passages) they require that someone details, implements or verifies the contained specification items.

#### Coverage

The term "coverage" describes the relation between [specification items](#specification-item) that require detailing, implementation or verification and the items providing just that. This is done by listing all [artifact types](#specification-item-artifact-type) where the author of a specification item expects to see coverage for that item.

A specification item is covered when for each of the required artifact types at least one item exists that covers the original item.

#### Deep Coverage

Deep coverage is a special form of coverage. Achieving deep coverage means that not only is a [specification item](#specification-item) covered by all required [artifact types](#specification-item-artifact-type), but also the covering items are all covered.

#### Terminating Specification Item

A [specification item](#specification-item) terminates a chain of items if it does not require coverage in any [artifact type](#specification-item-artifact-type).

Example:

    "feat" --needs--> "req" --needs--> "dsn" --needs--> "impl" (terminates chain)
                                                 |----> "utest" (terminates chain)
                                                 '----> "itest" (terminates chain)

## Use Cases

### Writing a Specification

Preconditions:
* Text editor (preferably with syntax highlighting for [Markdown](https://daringfireball.net/projects/markdown/))

OFT's native format for writing specifications is [Markdown](https://daringfireball.net/projects/markdown/). Markdown is an easy to learn, easy to read markup format that can be written with any text editor and is typically rendered to HTML. For your convenience we recommend using an editor that provides at least syntax highlighting. A preview function is also helpful. In the best case it features an outline view too. Check ["Tools for Authoring OFT Documents"](#tools-for-authoring-oft-documents) for some suggestions.

While OFT introduces additional syntax rules so that it can distinguish between [informative](#informative-passages) and [normative passages](#normative-passages), all elements are valid Markdown.

Let's start with a minimal requirement:

    `req~this-is-the-id~1`
    
    This is the description of the requirement.

Simple as this. This is already a valid and complete OFT requirement. Of course, you can enrich the requirement with other information but at the heart of it every requirement is an ID and a description.

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

The number of hash marks in front of the title must adhere to the rules of Markdown, meaning that if you want to put a [specification item](#specification-item) inside a section with a level two header, the item title must start with three hash marks.

At the moment the specification item above is a [terminating item](#terminating-specification-item) because it does not require coverage by any [artifact type](#specification-item-artifact-type). Since a user level requirement always needs coverage in other artifact types, we are going to add this next.

    ### The Requirement Title
    `req~this-is-the-id~1`
    
    This is the description of the requirement.
    
    Needs: dsn, uman

Now the item must be covered in the design ("dsn") and user manual ("uman"). Remember you can introduce your own artifact types depending on the needs of your project.

Of course, you can embed specification items into normal Markdown text. This adds the necessary [informative](#informative-passages) context that is required to understand the [normative passages](#normative-passages).

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

The `Status` keyword takes a single value from `draft`, `proposed`, `approved` to set the status of the item. At the moment this has no effect on the HTML or plaintext output, but only if the `-o aspec` option is used (see [XML Tracing Report](#xml-tracing-report)). Has to occur before the `Description`, `Rationale` or `Comment`. 

    ### A draft spec items
    `req~draft-item~1`
    Status: draft
    
    This spec item is in the draft state and thus not considered final.
   

##### `Covers`

The `Covers` keyword states which items are covered by the current specification item. It is followed by a list of items that are covered, each one written on a new line starting with a bullet character (`+`, `*`, or `-`) followed by the referenced specification item id. 

Given the Feature `feat~rubber-ducky~1` exists and needs a `req`. A requirement that covers that feature, could be written as

    ### Rubber ducky is made from latex
    `req~rubber-ducky-made-from-latex~1`
    
    The rubber ducky should be made from latex.
    
    Rationale:
    We'd like to avoid using materials made from crude oil and therefor use latex instead, because it is made from sustainable, regrowable resources.
    
    Covers:
    - feat~rubber-ducky~1

##### `Depends`

The `Depends` keyword defines dependencies between specification items. It is followed by a list of items the current specification item depends on, each one written on a new line starting witch a bullet character (`+`, `*`, or `-`) followed by the referenced specification item id. At the moment this has no effect on the HTML or plaintext output, but only if the `-o aspec` option is used. This has no effect on the coverage of specification items.

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

Tags are described in detail later in this document, see section [Distributing the Detailing Work](#distributing-the-detailing-work).

### Delegating Requirement Coverage

Consider a situation where you are responsible for the high-level software architecture of your project. You define the component breakdown, the interfaces and the interworking of the components. You get your requirements from a system requirement specification, but it turns out many of those incoming requirements are at a detail level that does not require design decisions on inter-component-level but rather affects the internals of a single component.

In those cases it would be a waste of time to repeat the original requirement in your architecture just to hand them down to the detailed design of a component. Instead, what you need is a fast way to express "yes, I read that requirement, and I am sure it does not need design decisions in the high-level architecture."

To achieve this OFT features a shorthand notation for delegating the job of covering a specification item to one or more different artifact types.

In the following example a requirement in the system requirement specification (artifact type `req`) stated that the web user interface of your product should use the corporate design. This clearly does not require an architectural decision (`arch`), so you forward it directly to the detailed design (`dsn`) level.  

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

### Distributing the Detailing Work

In projects of a certain size you always reach the point where a single team is not enough to process the workload. As a consequence the teams must find a way to distribute the work. A popular approach is splitting the architecture into components that are as independent as possible. Each team is then responsible for one or more distinct components. While the act of assigning the work should never be done inside the specification, at least the specification can prepare criteria on which to split the work.

One proven way to do this is to use tags. The teams then decide for which specification items with which tags they are responsible.

![Covering selected tags](images/uml/object/obj_multiple_detailed_designs.svg)

In our example it is the job of Andrea the architect to create a system architecture for the system specification coming from Soeren. Andrea defines a set of components which communicate with each other through well-defined, minimal interfaces. Each component is designed so that it can be independently developed and tested. Only an integration test is later necessary to prove that the components work together as designed. You tag each architectural requirement with the names of the affected components.

A typical requirement would then look like this (shortened to emphasize the "needs" and "tags" part): 

    `arch~authentication-provider-requires-valid-client-certificate~1`
    
    The authentication provider accepts only connections from clients offering a client certificate ...
    
    Needs: dsn
    
    Tags: AuthenticationProvider

The development teams distribute the components among themselves and use the tags to filter for only the [specification items](#specification-item) they are responsible for. The teams then cover all of these in the detailed design and deliver everything to an integrator. The sum of all detailed designs must then cover the architectural design.

Wan and Wu from the web service team in our example run an OFT convert job like this to pick the parts of the architecture they are affected by:

    oft convert -t AuthenticationProvider,ServiceDiscovery,MapProvider import/arch/ > arch_filtered_by_web_services.xml

This tells OFT to read all known specification files from the directory "import/arch" and filter by a list of tags. The result is a list of requirements that match the tag filter.

If you want to also import specification items that do not have any tags, add a single underscore "_" as the first entry in the comma-separated list of tags:

    oft convert -t _,AuthenticationProvider,ServiceDiscovery,MapProvider import/arch/ > arch_filtered_by_web_services.xml
     

### Tracing the Whole Chain

If you plan to assess the coverage state of your product as a whole, you need to trace the full chain including all artifacts.

### Tracing the Whole Chain in the Same File System

Preconditions:

* All artifacts are readable for the user executing OFT

Description:

In a small project you probably have all artifacts in the same file system - most likely under a common root directory.
In this case the easiest way to get a full trace is to list all the directories that OFT should search for artifacts to import.

Let's assume a typical Java project with the following directory layout:

    /home/git/my-project
      |-- doc                      manuals, requirement specification and design
      |-- src
      |    |-- main
      |    |     '-- java          implementation
      |    '-- test
     ...         '-- java          unit and integration tests
     

In this case the minimal OFT command line looks like this:

```sh
PROJECT_ROOT='/home/git/my-project/'
oft trace "$PROJECT_ROOT"/doc "$PROJECT_ROOT"/src/main/java "$PROJECT_ROOT"/src/test/java
```

Or if you prefer it shorter:

```sh
cd /home/git/my-project/
oft trace doc src/main/java src/test/java
```

The first variant is better suited for integration into scripts where you usually want to avoid changing the directory.

By default, this will produce a plain text trace that displays details of all defect specification items and a summary.

See also:
* [Tracing Options](#tracing-options) for controlling the tracing output

### HTML Tracing Reports

While plain text reports are perfect for debugging your tracing chain, sometimes you need reports that are more optically appealing. This is usually true if you have to show reports to management or to quality assessors who usually focus on summaries and statistics rather than detail results. In this case you can tell OFT to create reports in HTML format by adding the `-o html` switch.

```sh
oft trace -o html
```

## Reference

### OFT Command Line

The OFT command line looks like this:

    oft command [option ...] [<input file or directory> ...]

Where `command` is one of

* `trace` - create a requirement trace document
* `convert` - convert to a different requirements format

and `option` is one or more of the options listed below.

#### Import options

    -a, --wanted-artifact-types <artifact type>[,...]

Import only specification items where the artifact type matches one of the listed types.

    -t, --wanted-tags [_,]<tag>[,...]

Import only specification items that have at least one of the listed tags. If you add a single underscore "_" as first entry in the list, specification items that have no tags at all are also imported.

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
* `failure_summaries` - list of summaries for defect specification items
* `failure_details` - summaries and details for defect specification items
* `all` -  summaries and details for all specification items

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
: Plain black and white. On the console this also means no font styles used.

`monochrome`
: Black, white and shades of grey. Also enables font style on the console.

`color`
: Color output. Also enables font style on the console.

### Build Integration

In order to integrate requirement tracing with OFT into your CI build, we recommend using the OFT plugins for Maven and Gradle:

* [openfasttrace-maven-plugin](https://github.com/itsallcode/openfasttrace-maven-plugin)
* [openfasttrace-gradle](https://github.com/itsallcode/openfasttrace-gradle)

### Input Format Support

#### Tags in Programming Language or Markup Files

The Tag Importer is the most basic importer OFT offers. While it supports a wide variety of source formats, it does not
really understand the surrounding format, but instead looks for certain patterns that define specification items.

To avoid conflict with the formats actual contents, you embed these definitions in comments usually.

Tags have the following format:

```
[ <covered-artifact-type> -> <specification-object-id> ]
```

Spaces above were only added for readability. They are optional. In fact usually people prefer a more compact form.
Here is an example of a tag embedded into a Java comment:

```java
// [impl->dsn~validate-authentication-request~1]
private validate(final AuthenticationRequest request){
    // ...
}
```

##### Optional Elements

Tags can optionally specify a revision number or name and revision number:

```
[ <covered-artifact-type> ~~ <revision> -> <specification-object-id> ]
[ <covered-artifact-type> ~ <name> ~ <revision> -> <specification-object-id> ]
```

Examples:

```java
// [impl~~2->dsn~validate-authentication-request~1]
// [impl~validate-password~2->dsn~validate-authentication-request~1]
```

##### Forwarding Requirements

When using UML models as design document files like UML models it is useful to add needed coverage as well. To do this, you can use the following format:

```
[ <covered-artifact-type> -> <specification-object-id> >> <list-of-needed-artifcat-types> ]
```

Example:

```
' [dsn->req~1password-login~1>>impl,test]
user -> system : login(token: OAuthToken)
```

##### Supported File Extensions

The Tag Importer recognizes the supported format by the file extension. The following list shows the standard set of
recognized file types:

**Programming languages**

* C (`.c`, `.h`)
* C++ (`.C`, `.cpp`, `.c++`, `.cc`, `.H`, `.hpp`, `.h++`, `.hh`)
* C# (`.c#`, `cs`)
* Database related (`.sql`, `.pls`)
* Configuration files (`.cfg`, `.conf`, `.ini`)
* [Go](https://golang.org/) (`.go`)
* Groovy (`.groovy`)
* Java (`.java`)
* JavaScript (`.js`)
* TypeScript (`.ts`)
* JSON (`.json`)
* Lua (`.lua`)
* Objective C (`.m`, `.mm`)
* Perl (`.pl`, `.pm`)
* PHP (`.php`)
* Python (`.py`)
* R (`.r`)
* Rust (`.rs`)
* Shell programming (`.sh`, `.bash`, `.zsh`)
* Swift (`.swift`)
* Terraform (`.tf`, `.tfvars`)
* Windows batch files (`.bat`)

**Markup languages**

* HTML (`.html`, `.htm`, `.xhtml`)
* YAML (`.yaml`, `.yml`)

**Modeling languages**

* [PlantUML](https://plantuml.com) (`.pu`, `.puml`, `.plantuml`)

Note that XML is at the moment not yet supported by the Tag Importer, because it would collide with the SpecObj Importer. Once import fallback is implemented, XML will be supported too.

**Test Specification languages**

* [Gherkin](https://cucumber.io/docs/gherkin/) (`.feature`)
 
#### Markdown

The main importer of OFT accepts markdown files with the extensions `.md` and `.markdown`.

#### SpecObject

Elektrobit's SpecObject format is read from SpecObject files with the `.xml` extension.

The SpecObject format is extended with support of namespaces to allow adding custom XML elements, e.g.:

```xml
<specdocument xmlns:ext="http://extension">
    <specobjects doctype="req">
    </specobjects>
    <ext:extension>
    </ext:extension>
</specdocument>
```

## Console Tracing Report

The Console Tracing Report is the standard report format of OFT. Its main purpose is to quickly debug broken tracing links. In this section you learn how to read this report.

Below you see a typical example of a requirement from a design document.

    ok [ in:  2 /  2 ✔ | out:  1 /  1 ✔ ] dsn~cli.tracing.default-format~1 (impl, utest)
    
      The CLI uses plain text as requirement tracing report format if none is given as a parameter.
    
      [covered shallow  ] ← impl~cli.tracing.default-format-2215031703~0
      [covers           ] → req~cli.tracing.default-output-format~1
      [covered shallow  ] ← utest~cli.tracing.default-format-3750270139~0

Let's go through its elements one by one.

The first line is the summary.

It starts with the status of the requirement &mdash; OK in this case.

> **ok** [ in:  2 /  2 ✔ | out:  1 /  1 ✔ ] dsn~cli.tracing.default-format~1 (impl, utest)

Next we have a couple of numbers.

The first pair shows how many of the incoming good links this requirement has (two), and how many in total (two).

> ok [ **in:  2 /  2 ✔** | out:  1 /  1 ✔ ] dsn~cli.tracing.default-format~1 (impl, utest)

Consequently, the next pair informs you how many (one) of the overall (one) outgoing links are good.

Please note that OFT cannot predict the exact number of required incoming links, because often we are talking about one-to-many relations. So OFT does not try to. The checkmark and crossmark in the square brackets are only a quick indicator of if the existing links are okay. This goes so far that in case of zero links, no mark is displayed at all.

>  ok [ in:  2 /  2 ✔ | **out:  1 /  1 ✔** ] dsn~cli.tracing.default-format~1 (impl, utest)

The [Specification Item ID](#specification-item-id) in the middle is the unique technical ID of this requirement.

>  ok [ in:  2 /  2 ✔ | out:  1 /  1 ✔ ] **dsn~cli.tracing.default-format~1** (impl, utest)

In the brackets you find, which artifact types this item expects as coverage. If the type is covered correctly, you see just the name there. 

>  ok [ in:  2 /  2 ✔ | out:  1 /  1 ✔ ] dsn~cli.tracing.default-format~1 (**impl, utest**)

If it is not covered, the name is lead in by a minus:

>  **not ok** &hellip;  (**-impl**, utest)
 
If an artifact type provides coverage that is not requested, you find this indicated with a plus in front.

> **not ok** &hellip; (impl, **+itest**, utest)

If there were any other specification objects defined with the same ID, you would see the following at the end of the summary line:

> [has 3 duplicates]

Everything after that line are details of the requirement. Indented text indicates this. The first part of the details is the description.

    The CLI uses plain text as requirement tracing report format if none is given as a parameter.

The section with the arrows provides details about incoming and outgoing links. Arrows pointing to the left are incoming links, arrows pointing to the right are outgoing. You can easily remember this, since the arrows either point towards the ID of the connected specification item or away from it.

The following line means that this design requirement is covered in the implementation.

> [covered shallow  ] ← impl~cli.tracing.default-format-2215031703~0

The ID of the implementation comes from the Tag Importer and is for its most part auto-generated. The artifact type `dsn` is simply replaced by `impl` here and a number is attached for disambiguation.

> [covered shallow  ] ← **impl**~cli.tracing.default-format-**2215031703**~0

In the square brackets you find the status of the link.

Just in case you are wondering about the extra spaces in some places of the report, those exist as padding to align multiple similar items in lists. 

## XML Tracing Report

Often users want to further process the results generated by OpenFastTrace to create statistics about requirements, 
to add the results to a database or further analyze the results. OpenFastTrace therefore provides a reporter that
exports all relevant collected information into a single XML file that can further be processed by other tools.

The XML exporter is called `aspec` reporter. `aspec` in this case means augmented specobject. An `aspec` report 
can be generated  by calling OpenFastTrace in the following way:

```bash
java -jar openfasttrace.jar trace -o aspec -f requirements.xml requirements
```

OpenFastTrace needs to be executed  with the command `trace` to activate the reporter. The `aspec` report is selected 
with that parameter `-o aspec`. `-f` allows to provide the name of the output file into which the XML report is 
generated.

The `aspec` reporter generates an entry per processed requirement:

* all relevant parameters of the requirement
* information if a requirement is successfully covered by other requirements
* information if all requirements covering a requirement are themselves successfully covered
* list of requirements covering a requirement
* list of requirements linked by a requirement

The XML output roughly has the following structure

```xml
<specdocument>
    <specobjects doctype="arch">
        <specobject>
            <id>arch-my-architecture-requirement</id>
            ...
        </specobject>
        ...
    </specobjects>
    ...
</specdocument>
```

`<specdocument>` is the toplevel XML element. Beneath the `<specdocument>` one `<specobjects>` entry can be found for 
each requirement type found by OpenFastTrace. The element `<specobjects>` contains all requirements  matching the type 
named in `<specobjects>`. A `<specobject>` XML tag wraps each requirement.

A `<specobject>` entry has the following form:

```xml
<specobject>
    <id>arch-my-architecture-requirement</id>
    <version>1</version>
    <shortdesc>The title</shortdesc>
    <status>approved</status>
    <sourcefile>architecture.md</sourcefile>
    <sourceline>134</sourceline>
    <description>Yet another architecture</description>
    <coverage>
        ...
    </coverage>
    <covering>
        ...
    </covering>
    <dependencies>
        ...
    </dependencies>
</specobject>
```

`<id>` and `<version>` provide ID and version of the requirement. In OFT terminology the term revision is equal to 
version used in the aspec report. `<shortdesc>` and `<description>` provide title and  description of the requirement. 
`<sourcefile>` and `<sourceline>` are the name and line number of the original file from  which the requirement has been 
imported. `<coverage>` contains more information about the coverage of the requirement and lists other requirements 
covering the requirement.

`<covering>` contains requirements that have been marked as dependency. If parts of the information described above is 
not available the corresponding XML element is omitted in the generated report.

The `<coverage>` XML element has the following form:

```xml
<coverage>
    <needscoverage>
        <needsobj>dsn</needsobj>
        ...
    </needscoverage>
    <shallowCoverageStatus>COVERED</shallowCoverageStatus>
    <deepCoverageStatus>UNCOVERED</deepCoverageStatus>
    <coveringSpecObjects>
        ...
    </coveringSpecObjects>
    <coveredTypes>
        <coveredType>dsn</coveredType>
    </coveredType>
    <uncoveredTypes>
        <uncoveredType>impl</uncoveredType>
    </uncoveredTypes>
</coverage>
```

The `<coverage>` elements provides the following sub elements:

* `<needscoverage>`: List of requirement types that are required to cover the requirement
* `<shallowCoverageStatus>`: `COVERED` if for all needed requirement types another valid requirement covers the requirement. 
   Valid in this case also means that the covering requirement has status `approved`.
  `UNCOVERED` if not all required requirement types covered successfully.
* `<deepCoverageStatus>`: `COVERED` if all request requirement types are successfully covered by other requirements
   that are themselves successfully covered transitively. `UNCOVERED` if the requirement is not successfully covered 
   transitively.
* `<coveringSpecObjects>`: The `<coveringspecobjects>` element contains a sub element for each covering requirement.
* `<coveredTypes>`: List of requirement types that are shallow covered.
* `<uncoveredTypes>`: List of requirement types that are not shallow covered.

The element `<coveringSpecObjects>` describes all covering requirements:

```xml
<coveringSpecObjects>
    <coveringSpecObject>
        <id>dsn-requirement</id>
        <version>1</version>
        <doctype>dsn</doctype>
        <ownCoverageStatus>COVERED</ownCoverageStatus>
        <deepCoverageStatus>COVERED</deepCoverageStatus>
        <coveringStatus>COVERING</coveringStatus>
    </coveringSpecObject>
        ...
</coveringSpecObjects>
```

The element `<coveringSpecObjects>` describes each requirement that provides a coverage to the enclosing requirement. Each
covering requirement is described via the element `<coveringSpecObject>`. `<id>` and `<version>` provide the requirement
ID and version of the requirement. `<doctype>` provides the requirement type of the requirement. `<ownCoverageStatus>`
describes if the covering requirement is shallow covered. A shallowly covered requirement is marked as `COVERED`.
An uncovered requirement is reported as `UNCOVERED`. `<deepCoverageStatus>` reports a transitive
covered requirement with value `COVERED` and an uncovered requirement with value `UNCOVERED`. If the covering
requirement itself transitively covers the enclosing requirement, `<coveringStatus>` reports the value `COVERING`.
If the covering requirement does not cover the enclosing requirement `<coveringStatus>` reports `UNCOVERED`. If the
covering requirement references the enclosing requirement with a wrong version `coveringStatus` reports 
`COVERING_WRONG_VERSION`. If the covering requirement is not expected to cover the enclosing requirement (e.g. it has
an unexpected requirement type) the `<coveringStatus>` is reported as `UNEXPECTED`.

A requirement described by the XML element `<specobject>` lists all other requirements that it covers in the element
`<covering>`:

```xml
<covering>
    <coveredType>
        <id>arch-requirement</id>
        <version>1</version>
        <doctype>arch</doctype>
    </coveredType>
    ...
</covering>
```

For each covered requirement `<covering>` includes a `<coveredType>` element. A `<coveredType>` lists the referenced 
requirement ID with the element `<id>`, the requirement version with `<version>` and the requirement type with the
element `<doctype>`.

If a requirement references other requirements without contributing to requirement coverage then all these references
are described by the element `<dependencies>`:

```xml
<dependencies>
    <dependsOnSpecObject>
        <id>arch-requirement</id>
        <version>1</version>
        <doctype>arch</doctype>
    </dependsOnSpecObject>
    ...
</dependencies>
```

The `<dependencies>` element provides a `<dependsOnSpecObject>` element for each referenced requirement. The 
`<dependsOnSpecObject>` element lists requirement ID with the element `<id>`, the requirement version with `<version>` 
and the requirement type with the element `<doctype>`.

## OFT API

If you are a software developer planning to integrate OFT into one of your programs or scripts, you will probably want to use the OFT API.

Below you find a few short examples of how to use the OFT API. For details check the JavaDoc documentation of the interface [org.itsallcode.openfasttrace.core.Oft](../core/src/main/java/org/itsallcode/openfasttrace/core/Oft.java) in the source code.

### Using OFT From Java

The Java interface uses the "fluent programming" paradigm to make the code more compact and easy to read.

The steps that you need to program using the OFT API depend on whether you want to covert between requirement formats

    import -> export

or run a report.

     import -> link -> trace -> report

#### Converting File from Java

The following example code use OFT as a converter that scans the current working directory recursively (default import setting) and exports the found artifacts with the standard settings to a ReqM2 file. 

```JAVA
import org.itsallcode.openfasttrace.Oft;
import org.itsallcode.openfasttrace.core.SpecificationItem;
```

Select input paths and import specification items from there:

```JAVA
final Oft oft = Oft.create();
final List<SpecificationItem> items = oft.importItems(settings);
```

Export the items:

```JAVA
oft.exportToPath(items, Paths.get("/output/path/export.oreqm"));
```

#### Tracing and Reporting From Java

The example below shows how to use OFT as a reporter.  

```JAVA
import org.itsallcode.openfasttrace.Oft;
import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.Trace;
```

The import is similar to the converter case, except this time we add an input path explicitly for the sake of demonstration: 

```JAVA
final ImportSettings settings = ImportSettings //
        .builder() //
        .addInputs("/input/path") //
        .build;
final Oft oft = Oft.create();
final List<SpecificationItem> items = oft.importItems(settings);
```

Now link the items together (i.e. make them navigable):

```JAVA
final List<LinkedSpecificationItem> linkedItems = oft.link(items);
```

Run the tracer on the linked items:

```JAVA
final Trace trace = oft.trace(linkedItems);
```

Create a report from the trace:

```JAVA
oft.reportToStdOut(trace);
```

You can also use the trace results in your own code:

```JAVA
if (trace.hasNoDefects())
{
    // ... do something
}
```

#### Reporting Formats

There are various reporting formats for OFT and one can set it using the ReportSettings object.

```JAVA
ReportSettings reportSettings = ReportSettings.builder().outputFormat("html").build();
```

The `ReportSettings` builder has other functions as well that allow you to set verbosity etc.

OFT allows you to report directly to the standard output or to a file

```JAVA
//Reporting to a file
oft.reportToPath(trace, reportPath, reportSettings);
```

```JAVA
//Reporting to stdout
oft.reportToStdOut(trace);
```

#### Configuring the Steps

Import, export and report each have an overloaded variant that can be configured using the following classes

* [org.itsallcode.openfasttrace.api.importer.ImportSettings](../api/src/main/java/org/itsallcode/openfasttrace/api/importer/ImportSettings.java)
* [org.itsallcode.openfasttrace.core.ExportSettings](../core/src/main/java/org/itsallcode/openfasttrace/core/ExportSettings.java)
* [org.itsallcode.openfasttrace.api.ReportSettings](../api/src/main/java/org/itsallcode/openfasttrace/api/ReportSettings.java)

Each of those classes comes with a builder which is called like this:

```JAVA
ReportSettings settings = ReportSettings.builder().newline(Newline.UNIX).build();
```

### Exit Codes

The OFT command line interface returns the following exit codes:

* `0` on success
* `1` on OFT error
* `2` on command line error

## Tool Support

### Tools for Authoring OFT Documents

The following editors and integrated development environments are well suited for authoring OFT documents. The list is not exhaustive, any editor with Markdown capabilities can be used.

| Editor / IDE                                         | Syntax highl. | Preview | Outline | HTML export |
| ---------------------------------------------------- | ------------- | ------- | ------- | ----------- |
| [Gedit](https://wiki.gnome.org/Apps/Gedit)           | y             |         |         |             |
| [Eclipse](https://eclipse.org) with WikiText plug-in | y             | y       | y       | y           |
| [Eclipse](https://eclipse.org) with GMF plug-in      |               | y       |         |             |
| [IntelliJ](https://www.jetbrains.com/idea/)          | y             | y       | y       | y           |
| [Vim](https://www.vim.org/)                          | y             |         |         |             |
| [Visual Studio Code](https://code.visualstudio.com/) | y             | y       | y       |             |
