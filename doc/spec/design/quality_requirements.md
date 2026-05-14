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

The following rules are written for both human contributors and coding agents. The anchor names are short reminders; the rules below are normative.

### Production Code Rules

#### Clean Architecture

1. New code must keep dependencies pointing toward stable abstractions and module APIs instead of concrete implementation details.
2. Code must be testable through public or package-visible seams already used in the code base. Do not require global state, hidden environment coupling, or hard-wired I/O to test behavior.
3. Names and structure must reveal intent. If the design is hard to explain without a long comment, simplify the design first.

#### KISS

1. Choose the simplest implementation that satisfies the requirement and existing design constraints.
2. Avoid speculative abstractions, premature generalization, and configuration options that are not required by the current requirements or design.
3. Keep methods and control flow small enough that the behavior can be understood locally.

#### DRY

1. Do not copy-paste production logic across classes or modules.
2. Extract shared behavior when duplication would cause multiple sources of truth for the same rule, algorithm, or parsing logic.
3. Do not introduce an abstraction only to remove a few repeated lines when that abstraction makes the code harder to read.

#### Explicit Code

1. Use descriptive package, class, method, and variable names.
2. Do not introduce new non-domain abbreviations. Established domain or technical abbreviations such as `OFT`, `CLI`, `XML`, or similar widely understood terms are allowed.
3. Prefer code that explains itself through naming and structure.
4. Comments are only allowed when they capture intent or context that the code cannot express clearly on its own.

#### Thread Safe

1. Prefer immutable objects and side-effect-free methods.
2. Avoid shared mutable state unless it is required by the design.
3. If mutable shared state is necessary, synchronization and ownership must be explicit in the code and justified by the implementation context.

#### OWASP

1. Keep the OWASP anchor as a secure-coding reminder and apply the following minimum controls:
2. Validate untrusted input at module boundaries.
3. Reject invalid, malformed, or unexpected input explicitly.
4. Avoid unsafe file, path, process, deserialization, reflection, or XML handling patterns.
5. Do not log secrets, credentials, or other sensitive data.
6. When security-relevant behavior changes, add or update tests that cover the expected safe behavior.

### Test Rules

#### DAMP

1. Optimize tests for readability over deduplication.
2. Small duplication of literals or setup is allowed when it makes the test easier to understand.
3. Use parameterized tests when the same behavior must be verified against multiple input and output combinations.
4. Extract helpers, builders, or matchers only when they remove repeated test logic or significantly reduce noisy setup.

#### BDD

1. Tests must describe behavior in Given-When-Then form.
2. The structure may be expressed through method naming, local variable naming, whitespace, or short comments.
3. Each test must make the precondition, action, and expected outcome easy to identify.

#### Red-Green-Clean

1. For behavior changes, write or update the test before changing production code.
2. The new or changed test must fail for the expected reason before production code is changed to satisfy it.
3. Only after the failing test exists may production code be written or changed to make the test pass.
4. After the test passes, refactor as needed while keeping the tests green.

#### Test Quality

1. Each test should verify one logical expectation where possible.
2. If multiple assertions are required to validate one behavior, group them with `assertAll`.
3. Prefer known-good constants and explicit expected values over re-implementing the production algorithm in the test.
4. If a test constant needs explanation, add a short comment that explains why the value matters.
5. Parameter validation tests are required when invalid-input handling or boundary behavior is non-trivial.
6. When parameter validation is tested, cover both valid and invalid inputs and keep them in separate test methods or parameterized test groups.
7. When exceptions are asserted, verify both the exception type and the relevant message content.

### Java Implementation Rules

1. Document externally consumed APIs with JavaDoc. This includes public interfaces, abstract classes, and public methods that form part of an externally consumed API.
2. JavaDoc must describe purpose, parameters, return values, and observable side effects when they exist.
3. Declare method parameters as `final`.
4. Output parameters are only allowed when required by external libraries.
5. Prefer explicit types over `var`.

### Java Test Rules

1. Use Hamcrest matchers for assertions.
2. Extract repeated complex assertions into dedicated matcher classes when that improves readability.
3. Declare only the specific checked exceptions that are directly thrown by the code under test. Do not use generic exceptions (e.g., Exception, Throwable) in test method signatures.
4. When asserting exceptions, the assertion should invoke exactly one method call—the method under test. Avoid nesting additional method calls inside the assertion. Prepare all inputs outside the assertion so the failure is attributable to a single call.
5. Prefer @ParameterizedTest for testing multiple input variations instead of generating or mutating test data within a single test method (e.g., loops or inline variations).

## Dependency Policy

Additional libraries are not allowed by default. Any new third-party dependency requires an explicit design decision and approval before it is added to the build.

## Static Analysis And Security Gates

Static code analysis runs in SonarQube Cloud and acts as a build breaker. A failing quality gate blocks integration until the reported issues are resolved or an approved exception exists.

Dependency vulnerability monitoring runs through GitHub Dependabot instead of the Gradle build. Dependabot alerts provide the repository's vulnerability check for known vulnerable dependencies, while local and CI builds no longer run OSS Index as an immediate build-breaking gate.

OpenFastTrace tracing runs as a build breaker for the specification artifacts in scope. The trace stays clean for the requirement and design artifact types used by the project.

## Testability And Coverage

Path coverage across the code base stays at or above 80%. Coverage below that threshold fails the build unless a documented exception is accepted in advance.
