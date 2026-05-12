# System Requirements

## Introduction

Describe the product from the user's point of view. Base this section primarily on the user guide, README usage sections, tutorials, examples, public API documentation, screenshots, release notes, or CLI help.

## Goals

List the product goals inferred from user-facing documentation.

* `<goal>`

## Evidence Base

This draft was reverse-engineered from:

* `<user-guide-or-primary-user-documentation>`
* `<readme-or-examples>`
* `<tests-or-fixtures>`
* `<source-code-areas>`

## Notation

This document uses OpenFastTrace specification items to express product features, user requirements, and acceptance scenarios. Each specification item has a unique identifier in the form `<artifact-type>~<name>~<revision>`.

In this document, feature items use the artifact type `feat`, user requirements use `req`, and acceptance scenarios use `scn`. Design items in `doc/design.md` and `doc/design/` cover the scenarios with artifact type `dsn`. Architecture constraints in `doc/design/constraints.md` use artifact type `constr` and are also covered by `dsn` items.

Informative text explains background, scope, and intent. Specification items define the normative content of the document. Relationships between items are expressed with OpenFastTrace keywords such as `Needs` and `Covers`.

## Terms and Abbreviations

### `<Term>`

`<Definition>`

## User Roles

Describe the people or systems that use, operate, administer, integrate, or maintain the product. Use the general term `user` only when a requirement does not depend on a specific role.

### `<Role>`

`<Role description and expected interaction with the product.>`

## Features

This chapter describes product features at a level suitable for product communication. Detailed user needs and constraints are refined in the requirement items that cover these features.

### `<Feature Title>`
`feat~<feature-id>~1`

`<User-visible capability and why it matters.>`

Status: draft

Needs: req

## User Requirements

The following requirements refine the product features into user-visible behavior, constraints, and quality expectations.

### `<Requirement Title>`
`req~<requirement-id>~1`

`<Requirement stated from the user's perspective. Avoid implementation structure unless it is visible or contractually relevant.>`

Rationale:

`<Intent inferred from the user guide or other user-facing evidence.>`

Status: draft

Covers:
- `feat~<feature-id>~1`

Needs: scn

## Acceptance Scenarios

The following scenarios describe observable behavior in Given-When-Then form.

### `<Scenario Title>`
`scn~<scenario-id>~1`

**Given** `<initial state or precondition>`
**When** `<user action or external event>`
**Then** `<observable result>`

Status: draft

Covers:
- `req~<requirement-id>~1`

Needs: dsn

## Open Issues

Record unresolved questions, contradictions, and weakly supported inferences. Do not remove an issue until the user has resolved it or a stronger source has been found.

### `<Short Issue Title>`

Source evidence:

* `<source and location>`
* `<conflicting source and location>`

Issue:

`<Describe the contradiction, missing intent, or uncertainty.>`

Decision needed:

`<Question for the user or future maintainer.>`
