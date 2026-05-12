# Quality Requirements

This chapter documents architecture-relevant quality requirements and technical quality goals.

User-facing acceptance scenarios are defined in [System Requirements](../system_requirements.md).

Terms such as `plugin`, `OpenFastTrace`, and `OFT` use the definitions from [System Requirements](../system_requirements.md).

## Requirement Quality

We have the following requirement hierachy in this project:

| Document            | Level | Artifact types | Meaning                                                                 | Covers                 |
|---------------------|-------|----------------|-------------------------------------------------------------------------|------------------------|
| System Requirements | 1     | `feat`         | Top level feature as you would find on a product sheet (limited number) |                        |
|                     | 2     | `req`          | High level requirement                                                  | `feat`                 |
|                     | 3     | `scn`          | Given-when-then scenario                                                | `req`                  |
| Design              | 3     | `constr`       | Technical constraint                                                    |                        |
|                     | 4     | `dsn`          | Design requirement                                                      | `req`, `scn`, `constr` |
| Production Code     | 5     | `impl`         | Implementation                                                          | `dsn`                  |
| Test Code           | 5     | `utest`        | Unit test                                                               | `dsn`                  |
|                     | 5     | `itest`        | Integration test                                                        | `dsn`                  |

Runtime design requirements `dsn` can only cover on scenario at a time `scn`.

Use OFT's forwarding notation if a technical requirement does not add new information to a user-level requirement.

## Code Quality

| Principle          | Meaning                                                                                                          |
|--------------------|------------------------------------------------------------------------------------------------------------------|
| Clean Architecture | SOLID. Testable. Dependencies point inward. Reveal intent.                                                       |
| KISS               | Simplest solution proven to work. Low cyclomatic complexity.                                                     |
| DRY                | Extract duplication, don't copy-paste.                                                                           |
| DAMP               | Readable test code. Parameterized tests for test with differnent input values for the same function under test.  |
| BDD                | Given-When-Then tests.                                                                                           |
| Red-Green-Clean    | Fail test first. Implement and pass test. Refactor until clean code.                                             |
| Explicit Code      | Speaking package, class and method names. No abbreviations. Avoid explanatory comments unless stricly necessary. |
| Thread Safe        | All objects and parameters that can be are immutable.  Avoid side effects.                                       |
| OWASP              | Avoid typical sources of vulnerabilities.                                                                        |

### Test Quality

1. Prefer test constants with explanation in comments over testing an algorithm with the same logic in the test.
2. If possible each test method has only a single assert.
3. Parameter validation is tested with multiple valid and invalid inputs. Testing valid and invalid input is done in separate test methods.
4. Test algorithms against known-good constants instead of another algorithm

### Java Implementation Quality
 
1. APIs (not implementation) are documented with JavaDoc: interfaces, abstract classes, methods, parameters, return values, side effects (if any), purpose 
2. All method parameters are final. Output parameters are only allowed when they are used in external libraries.
3. Prefer explicit types over `var`.

### Java Tests Quality

1. Use Hamcrest matchers. Extract repeated complex assertions into a separate matcher class to improve reability of the tests.
2. If multiple asserts are necessary and the latter asserts are not a follow-up symptom of the previous ones, the asserts must be wrapped in `assertAll`.
3. When exceptions are asserted, then both type and message are validated.

## Dependency Policy

Additional libraries are not allowed by default. Any new third-party dependency requires an explicit design decision and approval before it is added to the build.

## Static Analysis And Security Gates

Static code analysis runs in SonarQube Cloud and acts as a build breaker. A failing quality gate blocks integration until the reported issues are resolved or an approved exception exists.

Dependency vulnerability monitoring runs through GitHub Dependabot instead of the Gradle build. Dependabot alerts provide the repository's vulnerability check for known vulnerable dependencies, while local and CI builds no longer run OSS Index as an immediate build-breaking gate.

OpenFastTrace tracing runs as a build breaker for the specification artifacts in scope. The trace stays clean for the requirement and design artifact types used by the project.

## Testability And Coverage

Path coverage across the code base stays at or above 80%. Coverage below that threshold fails the build unless a documented exception is accepted in advance.