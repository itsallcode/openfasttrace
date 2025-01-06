# OpenFastTrace 4.2.0, released ???

Code name: Markdown code blocks

## Summary

In this release we changed the behavior of the Markdown importer, so that if we are inside a code block, no OFT specification items are found. This is a corner case, but we think that this behavior is what users would expect (#480).

We also added a whole section about understanding and fixing broken links between specification items to the user guide.

## Features

* #431: Documented "unwanted coverage" in user guide.
* #480: Ignore specification items inside Markdown code blocks
