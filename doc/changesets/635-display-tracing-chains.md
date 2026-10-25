# GH-635 Display Tracing Chains

## Goal

Provide a command `oft chains` that displays the tracing chains in the specifications based on artifact type needs.

## Scope

In scope:

* Add `chains` CLI command: `oft chains [option ...] [<input file or directory> ...]`.
* Support output formats:
  * `chains`: linear chain list, combining terminal items with `|` when they share the same length and ancestor prefix (e.g. `feat -> req -> dsn -> impl | itest | utest`). Default format.
  * `csv`: 2 columns (`from,to`) displaying pairwise graph transitions.
  * `mermaid`: Mermaid.js flowchart (`graph LR`).
* Normal participation of forwarding specification items (treated as regular nodes/relations in the chain without skipping or special highlighting).
* Output to standard out.
* Validation: reject unsupported formats.

Out of scope:

* Output file option (`-f`).
* Item-level granularity (tracing stays at artifact type level).

## Design References

* [System Requirements](../spec/system_requirements.md)
* [Quality Requirements](../spec/design/quality_requirements.md)
* [Design](../spec/design.md)

## Strategy

1. Add `ChainsCommand` extending `AbstractCommand` in `org.itsallcode.openfasttrace.core.cli.commands`.
2. Extract graph transitions and tracing chains from imported `SpecificationItem` objects.
3. Render output according to the chosen format (`chains`, `csv`, `mermaid`).
4. Integrate with `CliStarter`, `ArgumentValidator`, and `CliArguments`.
5. Update specification and documentation (`system_requirements.md`, `design.md`, `usage.txt`, `man_1.md`, user guide).
6. Verify with unit tests, integration tests, and OFT self-trace.

## Task List

- [x] Check out branch `InAnYan/tracing-chains`

### Requirements And Design

- [x] Add `req~cli.chains.command~1`, `req~cli.chains.output-format~1`, and `req~cli.chains.default-output-format~1` to `doc/spec/system_requirements.md`
- [x] Update `dsn~cli.command-selection~1` and add `dsn~cli.chains.output-format~1`, `dsn~cli.chains.default-output-format~1`, and `dsn~chains.graph~1` in `doc/spec/design.md`

### Implementation

- [x] Implement `ChainsFormat`, `TracingGraph`, and `TracingGraphRenderer` in `core`
- [x] Implement `ChainsCommand` in `core.cli.commands`
- [x] Update `CliArguments`, `ArgumentValidator`, and `CliStarter`
- [x] Update `usage.txt`, `man_1.md`, and `oft_command_line.md`

### Verification

- [x] Add unit tests for `TracingGraph`, `ChainsCommand`, `ArgumentValidator`, and `CliArguments`
- [x] Add integration test in `CliStarterInternalIT`
- [x] Run `./oft-self-trace.sh`
- [x] Run `mvn -T 1C test`
