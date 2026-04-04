### OpenFastTrace (OFT) Claude Skill

OpenFastTrace (OFT) is a requirement tracing suite. This skill enables Claude to author specification files, run traces, and perform selective tracing for different levels of the project hierarchy.

OFT uses text files (Markdown, RST) for specification and code markers to represent coverage links. These markers are embedded in comments within source code, tests, and configuration files. OFT scans files or directories and detects specification documents and coverage. It calculates a network of requirements and coverage and reports the results.

#### Writing Specification Files Correctly

OFT uses Markdown with a specific syntax for specification items. "Specification Item" is the term that OFT uses to refer to a requirement, feature, design, or any other artifact that needs to be traced.

**Specification Item ID Format:**
`<artifact-type>~<name>~<revision>`

**Common Artifact Types:**
- `feat` (feature), `req` (requirement), `arch` (architecture), `dsn` (design)
- `impl` (implementation), `utest` (unit test), `itest` (integration test), `stest` (system test)

While these are found in many projects, OFT allows users to use their own artifact types. Artifact types require no declaration. If a new artifact type is used, OFT will automatically recognize and support it.

**Markdown Syntax Example:**
```markdown
### Requirement Title
`req~requirement-id~1`

This is the description of the requirement. It can be multi-line.

Rationale:
  
This is why this requirement exists.

Status: approved

Needs: dsn

Covers:
- `feat~feature-id~1`

Tags: Security, Backend
```

**Key Keywords:**
- `Status`: `draft`, `proposed`, `approved`, `rejected`
- `Covers`: List of items this item provides coverage for (use bullet points).
- `Needs`: Artifact types required to cover this item (comma-separated or list).
- `Depends`: List of items this item depends on (not for coverage).
- `Rationale`, `Comment`: Informative text blocks.
- `Tags`: Comma-separated labels for filtering.

**Status of a Specification Item**
By default an item is in status `approved`. This is for projects that use their specs to always reflect the status quo. If a project wants to use a specification as a planning tool for future work, it can use `draft` or `proposed` status. `rejected` is reserved for specification items that were explicitly rejected but should remain documented in the specification.

**Forwarding (Shorthand):**
To forward a requirement to lower levels without a new item:
`arch --> dsn : req~web-ui-uses-corporate-design~1`

This shorthand is used whenever a specification layer does not add new information to the covered specification item. As an example, if the user requirement says a report should be produced as PDF-A, then the designer can decide that they have nothing to add to that requirement.

**Source Code Tags:**
Embed coverage in source code comments:
```
// [impl->dsn~validate-authentication-request~1]
```

The comment character depends on the programming language. For example, in Java, JavaScript, C++ and so on it is `//`, in Python or shell scripts it is `#`. The comment character itself is ignored by OFT and only used to prevent the programming language from interpreting an OFT coverage tag as regular code.

#### Importers and File Support

OFT uses different **importers** to read various file formats and extract specification items or coverage links. It automatically selects the appropriate importer based on the file extension (suffix).

*   **Markdown Importer** (`.md`, `.markdown`): The primary importer for specification documents authored in Markdown.
*   **Tag Importer**: Scans source code and configuration files for coverage tags (e.g., `// [impl->dsn~id~1]`) embedded in comments. It supports a wide range of programming and markup languages, including:
    -   **Languages**: (`java`, `py`, `cpp`, `c`, `cs`, `js`, `ts`, `sh`, `bash`, `zsh`, `php`, `pl`, `pm`, `go`, `groovy`, `lua`, `r`, `rs`, `swift`, `sql`).
    -   **Configurations**: (`json`, `yaml`, `yml`, `toml`, `proto`, `tf`, `cfg`, `conf`, `ini`).
    -   **Markup/Models**: (`html`, `xhtml`, `puml`, `plantuml`).
*   **SpecObject Importer** (`.xml`, `.oreqm`): Used for ReqM2/SpecObject XML files. It automatically identifies `.xml` files containing a `<specdocument` header.
*   **reStructuredText Importer** (`.rst`): For specification items authored in reStructuredText.
*   **Zip Importer** (`.zip`): Allows OFT to trace contents of compressed ZIP files.

#### Installing OFT

OFT is available as a standalone executable JAR. To install OFT, download the latest release from the [OFT GitHub repository](https://github.com/itsallcode/openfasttrace/releases) and place the JAR file in a directory accessible to your LLM.

Always use the latest release.

#### Running OFT

OFT is an executable JAR. You run oft from Java by executing the JAR file with the `java -jar` command.

```shell
java -jar  openfasttrace-<major.minor.fix>.jar <switches>
```

Replace the version number with the actual version of the downloaded jar.

If you have a code interpreter or shell tool available, use it. Otherwise, always generate the command for the user to run manually.

The examples below assume a wrapper script called `oft` in the binary path exists. In the following part of this skill description we assume that this script is available in the binary path. This is just a shorthand for `java -jar openfasttrace-<major.minor.fix>.jar <switches>` though, so that variant is equally viable.

OFT has a trace mode and an exporter mode. The trace mode traces the requirement network and reports the results. The exporter mode exports the specification items in an exchange format.


#### Save Operation (Dry Run)

Requirement tracing is usually done in sensitive projects. Here are hints on how to ensure safe operation.

OFT never changes any of its input files. Exports and reports are written to STDOUT by default. If you use the `-f` option, OFT will write the output to the specified file instead of STDOUT. In this case, existing exports or reports under that path will be overwritten.

Without previous user consent:

- Only use STDOUT
- Don't change any input files

In safe mode, you can still analyze tracing problems with the help of OFT and explain them to the user.

#### Brave Mode

If the user accepts, you can modify input files. This mode is intended for advanced users who understand the risks and have taken the necessary precautions. This allows you to fix tracing problems autonomously. Always ask for user consent before making any changes.

#### Path Discovery

While projects often adopt typical layouts with directories like `/doc` or `/src`, this depends on the programming language and preferences of the project author. Scan and understand the directory structure before the first trace.

Directory scans in OFT are recursive. So if you have OFT check the `/doc` directory, it will also scan all subdirectories.

#### Running a Trace

Successful execution of OFT results in an exit code of `0`. If any issues are encountered, OFT will return a non-zero exit code. This can be used to determine if the trace was successful or if there were any errors.

**Basic Trace:**
```bash
oft trace doc/ src/
```

Note that the directories in this example are placeholders and should be replaced with actual paths relevant to the project.

**Trace with HTML Report:**
```bash
oft trace -o html -f target/report.html doc/ src/
```

**Verbosity Levels:**
- `--v failure_details` (default): Shows only what is broken.
- `--v all`: Shows all items, including successful ones.
- `--v summary`: Shows only the summary statistics.

For regular tracing the default verbosity is enough.

#### Step-wise Selective Tracing

Use artifact type filtering (`-a` to include only specific types, `-i` to ignore others) to focus on specific layers of the tracing chain.

**Step 1: Design to User Requirements (High-Level)**
Check if features and user requirements are covered by design/architecture. Ignore implementation and tests to get a clean high-level report.
```bash
oft trace -a feat,req,arch,dsn -i impl,utest,itest,stest <input-paths>
```

Adapt the artifact types depending on what the project actually uses.

**Step 2: Implementation and Test to Design (Low-Level)**
Check if design requirements are covered by implementation and tests. Ignore high-level features/requirements if you only want to focus on technical coverage.
```bash
oft trace -a dsn,impl,utest,itest,stest <input-paths>
```

Adapt the artifact types depending on what the project actually uses. Remember, each artifact type used in a specification item, automatically exists. There is no declaration required.

**Step 3: Component-Specific Trace (via Tags)**
Trace only items belonging to a specific tag/component.
```bash
oft trace -t ComponentName <input-paths>
```
To include untagged items as well (e.g., general requirements): `-t _,ComponentName`.

#### Understanding the Report

OFT produces a summary for each specification item.

**Successful Item Example:**
`ok [ in: 2 / 2 ✔ | out: 1 / 1 ✔ ] dsn~my-dsn~1 (impl, utest)`
- `in: 2 / 2 ✔`: This item has 2 incoming links (coverage), and all are OK.
- `out: 1 / 1 ✔`: This item has 1 outgoing link (covers another item), and it is OK.
- `(impl, utest)`: This item requires coverage in `impl` and `utest`. Since it is `ok`, it has both.

**Failed Item Example:**
`not ok [ in: 0 / 1 ❌ | out: 0 / 1 ❌ ] dsn~my-dsn~1 (-impl, utest)`
- `0 / 1 ❌`: There is a problem with the links.
- `-impl`: The required `impl` coverage is missing.
- `+impl`: `impl` was covered even though coverage by this artifact type is not required.
- `utest`: The required `utest` coverage is present.

### Fixing the Reported Tracing Errors

Tracing errors in OFT are transitive. If a tracing link is broken, it will propagate to all downstream items, causing them to also appear as failed. This ensures that all dependencies are checked for completeness. That means a single broken link can lead to multiple specification items being marked as not covered correctly.

Here are strategies to detect and resolve tracing errors:

1. Always check for spelling errors in IDs first. This is the most common cause of broken links.
2. Check if the correct artifact type is being traced in a link. OFT will indicate missing artifact coverage with a minus sign in front of the artifact type. A plus sign indicates coverage by an unexpected artifact type.
3. If coverage exists but covers the wrong revision of a specification item, OFT reports a link as outdated or predated. Ask the user, if they forgot to update the requirement itself or the revision number. In most cases it will be the former.
4. Duplicate specification item IDs mean that coverage cannot be safely determined. Check for copy and paste errors which are the most common cause of duplicate IDs. If you can, try to discern which requirement ID is the correct one. Suggest a correction for the duplicate where possible.
5. If coverage is not recognized, even though it is present in a file, check the [user guide](#user-guide) to see if the file extension is supported. Inform the user if coverage appears in an unsupported file type.
6. Make sure that files and directories that contain example specification items are excluded from coverage checks. OFT uses a positive list of files and directories, so not listing them will exclude them from coverage checks.
7. Complex projects exchange specification files. When a trace is incomplete, check if the upstream specification documents were included.
8. If there are a lot of errors in a report, help the user reduce them gradually. Fix trivial errors like typos that you detect automatically. If you are unsure, ask the user for clarification.
9. For transitive errors use an iterative approach. Fix them from specific to general, starting at the lowest level where the failure occurs. Often fixing the lowest level error will resolve the complete transitive error.
10. Since you do not have to declare artifact types in OFT, a typo in an artifact type will create a new artifact type. If artifact types are not covered, check if they are seldom used. This might be a sign of a typo. In such a case inform the user.
11. If a suffix is not supported by OFT, you cannot import it. Ask the user to create a feature request for the missing suffix in the tracker of the OFT project.
12. Rerun the trace to verify all issues are resolved.

#### Reference Documentation

##### User Guide

For more detailed information, consult the OpenFastTrace User Guide at the following stable GitHub URL:
[https://github.com/itsallcode/openfasttrace/blob/main/doc/user_guide.md](https://github.com/itsallcode/openfasttrace/blob/main/doc/user_guide.md)

##### Project Home on GitHub

The OFT project can be found under:
[https://github.com/itsallcode/openfasttrace](https://github.com/itsallcode/openfasttrace)

#### Architecture Template

An architecture template that uses an arc42 style is available at:
https://github.com/itsallcode/openfasttrace-architecture-template