# Evidence Checklist

Use this checklist when reverse-engineering requirements and design from an unfamiliar code base.

## User Intent Sources

Search for:

* `README*`
* `docs/`, `doc/`, `manual/`, `guide/`, `user_guide*`
* tutorials, examples, sample projects, demo scripts, screenshots
* CLI help, man pages, shell completion files
* public API docs, generated docs, OpenAPI specs, GraphQL schemas
* release notes, changelogs, migration guides

Capture the user's vocabulary before writing item IDs or terms.

## Behavior Sources

Search for:

* acceptance, integration, system, end-to-end, and golden-file tests
* examples and fixtures that encode expected outputs
* command handlers, controllers, routes, actions, jobs, schedulers, event handlers
* validation logic, error messages, warnings, logs, and exit codes
* configuration schemas, defaults, feature flags, and environment variables
* public interfaces, exported classes, package APIs, plugin extension points

Use behavior sources to fill gaps after the user-guide intent is represented.

## Design Sources

Search for:

* module, package, or component boundaries
* dependency injection configuration and service registration
* build files, dependency declarations, plugin descriptors, manifests
* persistence layers, schema migrations, repositories, caches
* network clients, external service integrations, protocols, file formats
* concurrency, background processing, retry, timeout, and cancellation logic
* security-sensitive code: authentication, authorization, secrets, sandboxing, input parsing

## Contradiction Patterns

Record an open issue when:

* documentation describes behavior that tests or code do not implement
* tests assert behavior not mentioned in user-facing documentation
* two user-facing documents use different terms for the same concept
* code exposes configuration, commands, endpoints, or file formats with no documented intent
* implementation constraints contradict claimed portability, privacy, performance, or compatibility
* design dependencies or runtime behavior conflict with system requirements

## Confidence Labels

Use these labels in notes while drafting:

* High: user-facing docs and tests/code agree.
* Medium: only one strong source exists, or source code and tests agree without user-facing docs.
* Low: inferred from internal structure only, or sources conflict.
