# UX reporter output format

## Overview

The UX reporter output format is the native output format of the openfasttrace ux-reporter.
It is the input format for OpenFastTrace UX.

## Data Structure

The generated JavaScript object follows this structure:

```javascript
window.specitem = {
  project: { /* project metadata */ },
  specitems: [ /* array of specification items */ ]
}
```

## Project Metadata

The `project` object contains high-level information about the specification project:

| Field | Type | Description |
|-------|------|-------------|
| `projectName` | string | Name of the project |
| `types` | string[] | Array of artifact types (e.g., "itest", "feat", "req", "arch", "utest") |
| `tags` | string[] | Array of all tags used in the project |
| `status` | string[] | Array of possible item statuses ("approved", "proposed", "draft", "rejected") |
| `wrongLinkNames` | string[] | Array of wrong link type names ("version", "orphaned", "unwanted") |
| `item_count` | number | Total number of specification items |
| `item_covered` | number | Number of items that are covered |
| `item_uncovered` | number | Number of items that are uncovered |
| `type_count` | number[] | Count of items per type (indexed by type array) |
| `uncovered_count` | number[] | Count of uncovered items per type |
| `status_count` | number[] | Count of items per status |
| `tag_count` | number[] | Count of items per tag |

## Specitems Array Entries

Each entry in the `specitems` array represents a single specification item with the following structure:

### Basic Properties

| Field | Type | Description |
|-------|------|-------------|
| `index` | number | Unique sequential index of the item in the array |
| `type` | number | Index into the `types` array indicating the item type |
| `title` | string | Full title of the specification item |
| `name` | string | Short name identifier of the item |
| `id` | string | Full unique identifier in format "type:name[:version]" |
| `tags` | number[] | Array of indices into the `tags` array |
| `version` | number | Revision number of the specification item |
| `status` | number | Index into the `status` array |

### Content Properties

| Field | Type | Description |
|-------|------|-------------|
| `content` | string | Description/content of the specification item |
| `comments` | string | Additional comments for the item |
| `path` | string[] | File path components where the item is defined |
| `sourceFile` | string | Source file path where the item is located |
| `sourceLine` | number | Line number in the source file (0 if not available) |

### Traceability Properties

| Field | Type | Description |
|-------|------|-------------|
| `provides` | number[] | Array of type indices that this item provides coverage for |
| `needs` | number[] | Array of type indices that this item needs coverage from |
| `covered` | number[] | Array of Coverage enum IDs per type (0=NONE, 1=UNCOVERED, 2=COVERED, 3=MISSING) |
| `uncovered` | number[] | Array of type indices that are uncovered or missing for this item |
| `covering` | number[] | Array of indices of items that this item covers |
| `coveredBy` | number[] | Array of indices of items that cover this item |
| `depends` | number[] | Array of indices of items that this item depends on |

### Link Validation Properties

| Field | Type | Description |
|-------|------|-------------|
| `wrongLinkTypes` | number[] | Array of indices into `wrongLinkNames` for invalid link types |
| `wrongLinkTargets` | string[] | Array of invalid link targets with format "target[reason]" |

## Example Entry

```javascript
{
  index: 0,
  type: 2,
  title: 'Title fea~fea1',
  name: 'fea1',
  id: 'fea:fea1',
  tags: [],
  version: 1,
  content: 'Descriptive text for fea~fea1',
  provides: [],
  needs: [3],
  covered: [0, 0, 2, 2, 1, 3],
  uncovered: [4, 5],
  covering: [],
  coveredBy: [1, 2],
  depends: [],
  status: 0,
  path: [],
  sourceFile: '',
  sourceLine: 0,
  comments: '',
  wrongLinkTypes: [],
  wrongLinkTargets: []
}
```

## Data Interpretation

### Index-Based References

Most numeric arrays in the specitems use indices to reference:
- **Type indices**: Reference the `project.types` array
- **Tag indices**: Reference the `project.tags` array
- **Status indices**: Reference the `project.status` array
- **Item indices**: Reference other items in the `specitems` array
- **Wrong link type indices**: Reference the `project.wrongLinkNames` array

### Coverage Arrays

The `covered` array contains Coverage enum IDs per type, where each position corresponds to a specItem type in the `project.types` array. The Coverage enum values are:
- **0 = NONE**: No coverage relationship exists for this type
- **1 = UNCOVERED**: The specItem is not fully covered
- **2 = COVERED**: The specItem is fully covered
- **3 = MISSING**: These coverage type are needed by this specItems or specItems that cover this specItem (deep coverage) but are not covered

For example, if `project.types = ["itest", "feat", "fea", "req", "arch", "utest"]` and `covered = [0, 0, 2, 2, 1, 3]`, this means:
- itest: NONE (no coverage relationship)
- feat: NONE (no coverage relationship)
- fea: COVERED (coverage is complete)
- req: COVERED (coverage is complete)
- arch: UNCOVERED (coverage required but missing)
- utest: MISSING (coverage exists but has issues)

### Wrong Link Targets Format

Wrong link targets are formatted as `"target[reason]"` where:
- `target` is the invalid link target specification
- `reason` explains why the link is invalid (e.g., "orphaned", "unwanted coverage", "outdated coverage")

Example: `"itest:itest_wrong_type[unwanted coverage]"`

## Usage Notes

- All string values are properly escaped for JavaScript (quotes, HTML entities)
- Long content strings are automatically wrapped across multiple lines with string concatenation
- Empty arrays and strings indicate no data for that property
- The data structure is designed for efficient lookup and filtering in web interfaces
- Indices provide memory-efficient references while maintaining data integrity

This data structure enables comprehensive traceability analysis and visualization in OpenFastTrace-UX applications.
