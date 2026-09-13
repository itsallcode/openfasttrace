![oft-logo](../../core/src/main/resources/openfasttrace_logo.svg)

# OpenFastTrace (OFT) User Guide

## In a Nutshell

OFT is a requirement tracing tool. It helps you make sure that all defined requirements are covered in your code. It also helps you find outdated code passages.

1. Create requirement and specification documents in Markdown, including OFT-readable specification items
2. Put tags into your source code that mark the coverage of items from the specification
3. Use OFT to trace the requirements from the source to the final implementation

## [Introduction](introduction/introduction.md)
* [Who Should Read This Document?](introduction/who_should_read_this_document.md)
* [What is Requirement Tracing?](introduction/what_is_requirement_tracing.md)
* [Why do I Need Requirement Tracing?](introduction/why_do_i_need_requirement_tracing.md)
* [Concepts and Terms](introduction/concepts_and_terms.md)

## [Installation](installation/installation.md)
* [Linux Installation](installation/linux.md)
* [macOS Installation](installation/macos.md)
* [Windows Installation](installation/windows.md)

## [Use Cases](use_cases/use_cases.md)
* [Writing a Specification](use_cases/writing_a_specification.md)
* [Excluding Parts of a Specification Document for OFT Parsing](use_cases/excluding_parts_of_a_specification_document_for_oft_parsing.md)
* [Delegating Requirement Coverage](use_cases/delegating_requirement_coverage.md)
* [Distributing the Detailing Work](use_cases/distributing_the_detailing_work.md)
* [Filtering by Status](use_cases/filtering_by_status.md)
* [Tracing the Whole Chain](use_cases/tracing_the_whole_chain.md)
* [Tracing the Whole Chain in the Same File System](use_cases/tracing_the_whole_chain_in_the_same_file_system.md)
* [HTML Tracing Reports](use_cases/html_tracing_reports.md)
* [Understanding and Fixing Broken Requirement Branches](use_cases/understanding_and_fixing_broken_requirement_branches.md)

## [Reference](reference/reference.md)
* [OFT Command Line](reference/oft_command_line.md)
* [Build Integration](reference/build_integration.md)
* [Input Format Support](reference/input_format_support.md)
* [Console Tracing Report](reference/console_tracing_report.md)
* [Report Summary](reference/report_summary.md)
* [XML Tracing Report](reference/xml_tracing_report.md)

## [OFT API](oft_api/oft_api.md)
* [Using OFT From Java](oft_api/using_oft_from_java.md)
* [Exit Codes](oft_api/exit_codes.md)

## [Tool Support](tool_support/tool_support.md)
* [Tools for Authoring OFT Documents](tool_support/tools_for_authoring_oft_documents.md)
* [Templates for IDEs](tool_support/templates_for_ides.md)
