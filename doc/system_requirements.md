# Features
## Requirement Tracing<a id="feat:requirement_tracing"/>
OFT traces requirements from specification to any kind of coverage (document, implementation, test, etc.)

Needs: req

## Requirement Import<a id="feat:requirement_import"/>
OFT imports requirements from different text formats. The default is Markdown.

Needs: req

# High Level Requirements
## Coverage Status of a Requirement<a id="req:coverage_status"/>
OFT determines the coverage status of a requirement. The possible options are:

  * Uncovered: an specification item requires coverage but is not covered
  * Covered: a specification item requires coverage and is covered 
  * Over covered: coverage for a specification item was found that does not exist

Covers:

  * [Requirement Tracing](#feat:requirement_tracing)
  
## Markdown import<a id="req:markdown_import"/>
OFT imports specification items from Markdown

Covers:

  * [Requirement Import](#feat:requirement_import)