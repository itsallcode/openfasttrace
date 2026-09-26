---
layout: docs
title: Input Format Support
parent: Reference
grand_parent: User Guide
nav_order: 3
---

### Input Format Support

#### Tags in Programming Language or Markup Files

The Tag Importer is the most basic importer OFT offers. While it supports a wide variety of source formats, it does not
really understand the surrounding format, but instead looks for certain patterns that define specification items.

To avoid conflict with the formats actual contents, you embed these definitions in comments usually.

Tags have the following format:

```
[ <covered-artifact-type> -> <list-of-specification-object-ids> ]
```

Spaces above were only added for readability. They are optional. In fact usually people prefer a more compact form.
Here is an example of a tag embedded into a Java comment:

```java
// [impl->dsn~validate-authentication-request~1]
private validate(final AuthenticationRequest request){
    // ...
}
```

##### Tags in Markdown and RST Documentation

Markdown documentation files (`.md` and `.markdown`) and RST files (`.rst`) can cover specification items without being routed through the Tag Importer. Place a full tag in a standalone, single-line native comment:

```markdown
<!-- [doc->req~user-guide~1] -->
```

```rst
.. [doc->req~user-guide~1]
```

Only complete, standalone Markdown HTML comments and single-line RST comments are recognized. Inline or multi-line comments and RST directives do not import coverage tags. Text outside a native comment that merely resembles a tag is also ignored.

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

##### Needed Coverage

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

* Ada (`.ads`, `.adb`)
* C (`.c`, `.h`)
* C++ (`.C`, `.cpp`, `.c++`, `.cc`, `.H`, `.hpp`, `.h++`, `.hh`)
* C# (`.c#`, `cs`)
* Doxygen (`.dox`)
* Database related (`.sql`, `.pls`)
* Configuration files (`.cfg`, `.conf`, `.ini`)
* [Go](https://golang.org/) (`.go`)
* Groovy (`.groovy`)
* Java (`.java`, `.fxml`)
* JavaScript (`.js`, `.ejs`, `.cjs`, `.mjs`)
* Kotlin (`.kt`, `.kts`)
* Lua (`.lua`)
* Objective C (`.m`, `.mm`)
* Perl (`.pl`, `.pm`)
* PHP (`.php`)
* Protocol Buffers (`.proto`)
* Python (`.py`)
* R (`.r`)
* Rust (`.rs`)
* Shell programming (`.sh`, `.bash`, `.zsh`)
* Swift (`.swift`)
* Terraform (`.tf`, `.tfvars`)
* TypeScript (`.ts`)
* Windows batch files (`.bat`)

**Configuration and Serialization Formats** 

* JSON (`.json`)
* Protobuf (`.proto`)
* TOML (`.toml`)

**Markup languages**

* HTML (`.html`, `.htm`, `.xhtml`)
* YAML (`.yaml`, `.yml`)
* XML (`xml`)

**Modeling languages**

* [PlantUML](https://plantuml.com) (`.pu`, `.puml`, `.plantuml`)

Note that XML is at the moment not yet supported by the Tag Importer, because it would collide with the SpecObj Importer. Once import fallback is implemented, XML will be supported too.

**Test Specification languages**

* [Gherkin](https://cucumber.io/docs/gherkin/) (`.feature`)

#### Gherkin

OFT imports Gherkin `Scenario` and `Scenario Outline` blocks in `.feature` files when the immediately preceding tag region contains one OFT ID tag. Place optional `Covers` and `Needs` comments after the tags and before the scenario header:

```gherkin
@smoke
@id:scn~user-can-log-in~1
# Covers: req~authentication~1
# Needs: dsn, itest
Scenario: A registered user logs in
  Given a registered user
  When they enter valid credentials
  Then access is granted
```

The ID tag becomes the item location and the scenario header becomes its title; executable steps become the description. `Covers` and `Needs` are case-sensitive and optional. Multiple `Covers` comments accumulate coverage IDs, while `Needs` may appear once; all lists must be non-empty and comma-separated. Invalid IDs, types, or directives skip only the affected scenario and emit a warning.

Existing full coverage tags remain supported in Gherkin comments, for example `# [impl~login~1 -> dsn~authentication~1]`. OFT deliberately ignores coverage-tag-shaped text in executable Gherkin lines.
 
#### Markdown

The main importer of OFT accepts Markdown files with the extensions `.md` and `.markdown`.

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
