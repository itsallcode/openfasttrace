### AGENTS.md — OpenFastTrace

This file provides guidance for AI agents and LLMs working on the OpenFastTrace (OFT) project.

### Agent Role & Persona

You are an expert Java developer specializing in requirement tracing and software quality. Your goal is to help maintain and evolve OpenFastTrace, following "Clean Code" principles and ensuring high reliability.

### Project Overview

OpenFastTrace is a requirement tracing suite that tracks the implementation and verification of specifications.
- **Stack:** Java 17+, Maven, JUnit 5, Mockito, Hamcrest Matchers.
- **Architecture:** Modular system with Core, API, Importers, Exporters, and Reporters.
- **Core Concept:** Requirements are parsed from various sources (e.g., Markdown, Source Code, XML), traced through the system, and written into reports.

### Key Commands

All commands should be run from the repository root.

| Task                     | Command                                                                  |
|:-------------------------|:-------------------------------------------------------------------------|
| **Build (full)**         | `mvn -T 1C clean package -DskipTests`                                    |
| **Run Unit Tests**       | `mvn -T 1C test`                                                         |
| **Verify (All tests)**   | `mvn -T 1C verify`                                                       |
| **Run Single Test**      | `mvn test -Dtest=ClassName`                                              |
| **Run Integration Test** | `mvn -Dit.test=CliStarterIT failsafe:integration-test -projects product` |
| **Self-Trace**           | `./oft-self-trace.sh`                                                    |
| **Check Dependencies**   | `mvn versions:display-dependency-updates`                                |

### Self-trace

When contributing to this project, you must also **use** OFT to trace your own changes.

1. **Tag Your Changes:** If you add a new feature or fix a requirement, add the corresponding coverage markers in the code comments (e.g., `// [impl-> req~example~1]`).
2. **Verify Tracing:** After making changes, run the self-trace script:
   ```sh
   ./oft-self-trace.sh
   ```
   Ensure no new "uncovered" or "invalid" items are introduced.
3. **Syntax & Usage:** For detailed syntax (Markdown, Tags) and CLI options, refer to the [OFT Skill Guide](.agents/skills/openfasttrace/SKILL.md).

### Architecture & Module Map
OFT is organized into several functional modules:
- `openfasttrace-api`: Core interfaces and contracts.
- `openfasttrace-core`: Main tracing logic.
- `openfasttrace-importer-*`: Importers for different formats (Markdown, Tag, XML, etc.).
- `openfasttrace-exporter-*`: Exporters for SpecObject format.
- `openfasttrace-reporter-*`: Generators for HTML and PlainText reports.
- `product`: The final executable "uber-JAR" assembly.

### Code Style & Conventions

- **Clean Code:** Follow Robert C. Martin's "Clean Code" principles (meaningful names, small functions, single responsibility).
- **Formatting:** Use the project's Eclipse formatter configuration. If not found in root, check `doc/itsallcode_formatter.xml` or individual module settings.
- **Logging:** Use `java.util.logging`. Configuration for tests is in `core/src/test/resources/logging.properties`.
- **Requirements:** Every new feature or fix should ideally be traced. Use `./oft-self-trace.sh` to check the tracing status.

### Testing Strategy

- **Framework:** JUnit 5 with Mockito for mocking.
- **Verification:** Use `mvn verify` to ensure unit tests, integration tests, and static analysis pass.
- **Coverage:** Aim for high coverage, especially in the `core` and `api` modules.

### Development Workflow

1. **Branching:** Work on a feature branch from `main`. Use the following naming convention: `<type>/<number>_<short-description-lower-snake-case>` (e.g., `feature/533_add_agents_md` or `fix/123_fix_parsing_error`).
2. **Review:** All code changes must be reviewed.
3. **CI:** GitHub Actions runs the `build.yml` workflow on every push.
4. **AGENTS.md Updates:** If you change the build process, add new modules, or change conventions, update this file.

### Agent Skills

For detailed technical skills, domain-specific knowledge (Specification Item syntax, artifact types), and LLM interaction guidelines, see: [`.agents/skills`](.agents/skills)

### Critical Files

- `pom.xml`: Root Maven configuration.
- `README.md`: General project overview.
- `doc/developer_guide.md`: Detailed technical documentation.
- `doc/spec/system_requirements.md`: System and user requirements.
- `doc/spec/design.md`: High-level design and architecture.
- `CONTRIBUTING.md`: Guidelines for human and AI-assisted contributions.
- `core/src/main/resources/usage.txt`: CLI usage documentation.
