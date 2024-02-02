# OpenFastTrace 3.7.2, released 2024-??-??

Code name: Bugfixes for parsing `Needs` and `Tags` in Markdown

## Summary

This release fixes parsing of `Needs` and `Tags` entries in Markdown. OFT now ignores whitespace in both and also correctly parses `Tags` in beginning of a requirement item.

## Bugfixes

* #373: Ignore spaces after items in "Needs:" and "Tags:" lists (thanks to [@sambishop](https://github.com/sambishop) for his contribution!)
