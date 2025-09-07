# OpenFastTrace 4.2.1, released 2025-09-07

Code name: Peek before you guess

## Summary

This is a bugfix release. It addresses the problem that ReqM2 uses a generic `.xml` file suffix which led OpenFastTrace to treat any XML file as a ReqM2 file based solely on the extension. To avoid false detections, this version adds a peek function to the file type detection so that we no longer rely only on the file type/extension (#429).

## Bugfixes

* #429: Add content peek to file type detection so `.xml` no longer implies ReqM2; prevents misclassification of arbitrary XML files
