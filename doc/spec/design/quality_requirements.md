# Quality Requirements

This chapter documents architecture-relevant quality requirements and technical quality goals.

User-facing acceptance scenarios are defined in [System Requirements](../system_requirements.md).

Terms such as `plugin`, `OpenFastTrace`, and `OFT` use the definitions from [System Requirements](../system_requirements.md).

## Requirement Quality

We have the following requirement hierachy in this project:

1. `feat`: Top level feature as you would find on a product sheet (limited number)
2. `req`: High level requirement
3. `scn`: Given-when-then scenario
4. `dsn`: Design requirement
   `constr`: Technical constraint
5. `impl`: Implementation
   `utest`: Unit test
   `itest`: Integration test

Runtime design requirements `dsn` can only cover on scenario at a time `scn`. You can use OFT's forwarding notation if a technical requirement does not add new information to a user scenario.

## Code Quality

1. Production code and test code follow clean-code principles.
2. The implementation prefers speaking names over explanatory comments. Comments are only acceptable when intent cannot be expressed clearly in code.
3. Abbreviations are only used where they are guaranteed common (meaning everyone knows them) otherwise all symbol names are written out.
4. Methods avoid side effects where possible.
5. Methods stay short and keep cyclomatic complexity low. When behavior grows beyond a small, readable unit, the design is refactored into smaller collaborating types or methods.
6. When the cyclomatic complexity is too high, use extract method refactoring.
7. APIs (not implementation) are documented with JavaDoc: interfaces, abstract classes, methods, parameters, return values, side effects (if any), purpose
8. All objects that can be, are immutable.
9. The implementation prefers static object allocation over dynamic allocation where possible.
10. All method parameters are final. Output parameters are only allowed when they are used in external libraries.

### Test Code Guideline

1. Test method names follow the given-when-then naming principle.
2. Method parameters are final.
3. The project uses Hamcrest matchers.
4. If possible each test method has only a single assert.
5. If multiple asserts are necessary and the latter asserts are not a follow-up symptom of the previous ones, the asserts must be wrapped in `assertAll`.
6. When exceptions are asserted, then both type and message are validated.
7. Where possible similar tests are bundled into parameterized tests (e.g., when multiple variants of input are tested against the same implementation method).
8. Prefer parameterized tests over tests that exercise different scenarios of the same method under test with multiple asserts.
9. Parameter validation is tested with multiple valid and invalid inputs. Testing valid and invalid input is done in separate test methods.

## Dependency Policy

The plugin uses the minimum set of dependencies required for:

* building and running a JetBrains plugin
* integrating the OpenFastTrace library

Additional libraries are not allowed by default. Any new third-party dependency requires an explicit design decision and approval before it is added to the build.

The Gradle dependency verification metadata in `gradle/verification-metadata.xml` is committed to source control. Maintainers update and review this metadata whenever dependency declarations, Gradle plugin versions, the IntelliJ Platform version, or other build inputs change the resolved dependency artifacts. The standard update command is `./gradlew --write-verification-metadata sha256 help`.

Generated local IntelliJ Platform Ivy metadata for bundled plugin and module artifacts is trusted without checksum verification because the IntelliJ Platform Gradle Plugin can generate environment-specific Ivy descriptors for the local IntelliJ Platform artifact repository. This exception applies only to `ivy-*.xml` descriptors in the `bundledPlugin` and `bundledModule` groups. Resolved JAR artifacts remain checksum verified.

## Static Analysis And Security Gates

Static code analysis runs in SonarQube Cloud and acts as a build breaker. A failing quality gate blocks integration until the reported issues are resolved or an approved exception exists.

Dependency vulnerability monitoring runs through GitHub Dependabot instead of the Gradle build. Dependabot alerts provide the repository's vulnerability check for known vulnerable dependencies, while local and CI builds no longer run OSS Index as an immediate build-breaking gate.

OpenFastTrace tracing runs as a build breaker for the specification artifacts in scope. The trace stays clean for the requirement and design artifact types used by the project.

The Gradle build applies the latest OpenFastTrace Gradle plugin version approved for the project and executes `traceRequirements` locally and in CI through the standard `check` lifecycle.

## Testability And Coverage

Automated tests use JUnit 5 together with Hamcrest matchers.

IntelliJ Platform light fixture tests may keep a JUnit 4 compatibility dependency when JetBrains test base classes still require the legacy `TestCase` API.

Path coverage across the code base stays at or above 80%. Coverage below that threshold fails the build unless a documented exception is accepted in advance.

The architecture favors testable units with clear boundaries so the coverage target can be met without relying mainly on brittle UI-level tests.

Automated plugin testing follows the IntelliJ Platform test strategy and uses real platform components in a headless environment instead of extensive mocking. Most plugin behavior is verified as model-level functional tests against test data files and expected results.

Plugin tests prefer light platform tests whenever possible because they reuse the project setup and run faster. Tests for plugin logic without Java-specific PSI use fixtures or base classes equivalent to `BasePlatformTestCase`. Heavier project-scoped tests are reserved for behavior that requires a fresh project, multiple modules, or project-level services that cannot be covered with light tests.

Editor-facing features such as syntax highlighting, inspections, annotators, references, and navigation use the IntelliJ test fixtures and test data conventions. Highlighting tests store expected results in test files and verify them with the platform highlighting fixture support.

End-to-end IDE startup and workflow checks use dedicated integration tests with the IntelliJ Platform Starter and Driver infrastructure. These tests run from a separate integration test task against the built plugin distribution, verify that the plugin loads into the target IDE, and cover only a small number of critical workflows because the UI driver APIs remain slower and more brittle than model-level tests.

Continuous integration runs plugin-specific verification tasks in addition to ordinary automated tests. The build verifies plugin packaging and descriptor validity and runs IntelliJ Plugin Verifier checks against the supported IDE builds to detect binary compatibility problems early.

## Platform Compatibility

The required Java version follows the standard Java requirement of the targeted JetBrains platform version. The project does not introduce a language level or bytecode target that exceeds that platform requirement.
