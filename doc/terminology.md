---
layout: default
title: Terminology
nav_order: 5
---

# OpenFastTrace Terminology

## A

### Artifact
A source or container for [specification items](#specification-item) (e.g., file, database).

### Artifact Type
Classification of an artifact's role in the trace hierarchy (e.g., `feat`, `req`, `dsn`).

## C

### Coverage
Relationship between a [specification item](#specification-item) and the items that detail, implement, or verify it.

### Coverage Link
A directed relationship between a requester and a provider of coverage.

### Coverage Provider
A [specification item](#specification-item) that fulfills a coverage requirement of another item.

### Coverage Requester
A [specification item](#specification-item) that explicitly demands coverage by specific [artifact types](#artifact-type).

## D

### Deep Coverage
Full coverage of a [specification item](#specification-item) including all its transitive providers down to [terminating specification items](#terminating-specification-item).

## F

### Forwarding
Shorthand for delegating coverage responsibility to other [artifact types](#artifact-type) without adding new design decisions.

### Full Coverage
Condition where all required coverage links are present and valid for a given scope.

## I

### Informative Passage
Content in a specification document that provides context without containing [specification items](#specification-item).

## N

### Normative Passage
Content that defines [specification items](#specification-item) or coverage links.

## O

### OpenFastTrace
The requirement tracing tool suite.

## OFT

See [OpenFastTrace](#openfasttrace)

### Overcovered
A condition where a [specification item](#specification-item) has more coverage links than required.

## R

### Rationale
Optional part of a [specification item](#specification-item) explaining the reasoning behind a requirement.

### ReqM2
A legacy requirement tracing format supported for import.

## S

### Specification Artifact
See [Artifact](#artifact)

### Specification Item
The atomic unit of a specification, representing a requirement, design decision, or coverage marker.

### Specification Item ID
Unique identifier of a [specification item](#specification-item), consisting of [artifact type](#artifact-type), [name](#specification-item-name), and [revision](#specification-item-revision).

### Specification Item Name
The unique name part of a [specification item ID](#specification-item-id).

### Specification Item Revision
Number in the [specification item ID](#specification-item-id) used to invalidate coverage when an item's meaning changes.

## SRS

See [System Requirement Specification](#system-requirement-specification)

### Status
Defines the maturity level of a [specification item](#specification-item): `draft`, `proposed`, `approved` or `rejected` .

### System Requirement Specification
A document describing the requirements of a system.

## T

### Tag
Label for categorizing [specification items](#specification-item), used for filtering or work distribution.

### Terminating Specification Item
A [specification item](#specification-item) that does not require further coverage (e.g., code, tests).

### Transitive Defect
Coverage gap caused by an undercovered provider in the trace chain.

## U

### Undercovered
A condition where one or more required coverage links for a [specification item](#specification-item) are missing.
