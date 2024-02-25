# OpenFastTrace 3.8.0, released 2024-02-??

Code name: RST Importer

## Summary

Good news for our 🐍 Python friends: in this release we derived a parser for Restructured Text (RST) from our existing Markdown parser.

The Markdown parser in the process now accepts specification item titles underlined with either "=" (H1) or "-" (H2).

Also, the Markdown parser now ignores whitespace in `Needs` and `Tags` entries and correctly parses `Tags` at the start of a requirement item.

The test coverage for the Markdown importer is now at 100%, and we were able to remove tests that were done with mocks in favor or more robust low-level integration tests.

The HTML report now allows expanding all details sections with command line option `--details-section-display expand`.

## Features

* #378: Added an RST importer
* #377: Allow expanding the details sections in the HTML report

## Bugfixes

* #366: Allow all unicode characters in names of specification ID names (thanks to [@sebastianohl](https://github.com/sebastianohl) for the bug report!)
* #373: Ignore spaces after items in "Needs:" and "Tags:" lists (thanks to [@sambishop](https://github.com/sambishop) for his contribution!)
* #378: Merged integration test coverage with unit test coverage for representative overall figure
* #303: Escape special characters in HTML report to avoid broken HTML when a specification item contains text like `<section>`

## Refactoring

* #222: Fixed Java compiler warnings
