# OpenFastTrace 3.8.0, released 2024-02-??

Code name: RST Importer

## Summary

In this release we derived a parser for Restructured Text (RST) from our existing Markdown parser.

The Markdown parser in the process now accepts specification item titles underlined with either "=" (H1) or "-" (H2).

Also, the Markdown parser now ignores whitespace in `Needs` and `Tags` entries and correctly parses `Tags` at the start of a requirement item.

The test coverage for the Markdown importer is now at 100%, and we were able to remove tests that were done with mocks in favor or more robust low-level integration tests.

## Features

* #378: Added an RST importer

## Bugfixes

* #373: Ignore spaces after items in "Needs:" and "Tags:" lists (thanks to [@sambishop](https://github.com/sambishop) for his contribution!)
* #366: Allow all unicode characters in names of specification ID names (thanks to [@sebastianohl](https://github.com/sebastianohl) for the bug report!)
