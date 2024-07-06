# OpenFastTrace 4.0.2, released 2024-07-06

Code name: Fix usability of HTML report

## Summary

This release updates the HTML report so that jumping to anchored tags does no longer hide the target section under the navbar. Thanks to [@RobertZickler](https://github.com/RobertZickler) for fixing this!

The release also fixes some inconsistencies in the documentation and in error messages reported by [@RobertZickler](https://github.com/RobertZickler):
* OFT now reports the exact name for unexpected parameters instead of removing dashes and converting it to lower case.
* The user guide now also lists `aspec` as possible option for `--output-format`
* The user guide now shows the correct option `--report-verbosity` instead of `--verbosity-level`
* The user guide example command line for generating an Aspec tracing report fixes a typo in the reporter name `aspac` instead of `aspec`

## Bugfixes

* #420: Fix jumping in HTML reports ([@RobertZickler](https://github.com/RobertZickler))
* #419: Fixed inconsistencies in documentation and error messages (reported by [@RobertZickler](https://github.com/RobertZickler))
