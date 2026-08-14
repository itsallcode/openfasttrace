# GH-527 SPDX SBOM

## Goal

Produce one SPDX 3 JSON software bill of materials (SBOM) for the OpenFastTrace
product during the Maven build and attach it to every GitHub release. The SBOM
must represent the product at module granularity, include compile, runtime, and
provided dependencies, exclude test dependencies, and retain available
dependency-license information.

## Scope

In scope:

* Define the build and release requirements for the product SPDX SBOM.
* Add and configure the SPDX Maven Plugin for the `product` module only.
* Generate `openfasttrace-<version>.spdx3.json` as part of the regular Maven
  build.
* Include compile, runtime, and provided dependencies.
* Exclude test-scope dependencies from the released-product SBOM.
* Ensure the release workflow uploads the SBOM with the product JAR and its
  checksum.

Out of scope:

* File-level or snippet-level SPDX resolution.
* SBOMs for individual OpenFastTrace modules.
* Changing OpenFastTrace runtime behavior or its public CLI/API.

## Design References

* [System Requirements](../spec/system_requirements.md)
* [Design](../spec/design.md)
* [Quality Requirements](../spec/design/quality_requirements.md)
* [Maven build configuration](../../parent/pom.xml)
* [Product module](../../product/pom.xml)
* [Release workflow](../../.github/workflows/release.yml)
* [GitHub release script](../../.github/workflows/github_release.sh)
* [SPDX Maven Plugin documentation](https://spdx.github.io/spdx-maven-plugin/createSPDX-mojo.html)

## Strategy

Configure the selected SPDX Maven Plugin in `product/pom.xml`, rather than in
the shared parent, so the reactor creates exactly one SBOM for the distributable
product. Bind `createSPDX` to the normal Maven lifecycle, use SPDX 3 JSON-LD
output and the required product filename, and explicitly configure dependency
scope inclusion to make the acceptance criteria independent of plugin defaults.

Extend the release script's explicit asset list with the generated SBOM. Keep
the SBOM beside the product JAR in `product/target` so the local build and the
release workflow consume the same artifact.

The plugin's public documentation confirms that it produces SPDX documents from
Maven POM metadata, can emit SPDX 3 JSON-LD (`.spdx3.json`), and exposes the
required dependency-scope controls. Its documented default lifecycle binding is
`verify`; select and test the binding deliberately so both the current CI
`install` build and release build produce the file.

## Task List

- [x] Create and check out branch `feature/527_spdx_sbom`.

### Requirements And Design

- [x] Add `req~build.spdx-sbom~2` to `doc/spec/system_requirements.md`, covering
      the product SBOM content, module granularity, required dependency scopes,
      SPDX 3 JSON filename, and release availability; add scenarios for a
      successful product build and a GitHub release asset.
- [x] Stop and ask the user for a review of the system requirements.
- [x] Add design items to `doc/spec/design.md` for product-module-only SPDX
      generation and for publishing the generated SBOM with the release; map
      each runtime design item to one scenario and require `impl` plus `itest`
      coverage.
- [x] Record the selected generator version, output mode, lifecycle phase,
      aggregation strategy, scope settings, and license metadata handling in
      the design.
- [x] Stop and ask the user for a review of the design and approval to add the
      external `org.spdx:spdx-maven-plugin`.

### Implementation

- [x] Add the approved SPDX Maven Plugin version and an execution to
      `product/pom.xml`; configure `createSPDX`, SPDX 3 JSON-LD output,
      `openfasttrace-${revision}.spdx3.json`, module granularity, and
      compile/runtime/provided inclusion and test-scope exclusion.
- [x] Confirm the plugin generates only the product SBOM, contains the resolved
      direct and transitive dependencies at module level, and preserves all
      available declared license data; add narrowly scoped license overrides
      only if the generated document demonstrates a required correction.
- [x] Add the SBOM as an explicit GitHub release asset in
      `.github/workflows/github_release.sh`, alongside the JAR and `.sha256`.

### Verification

- [x] Add a Maven-build integration test that asserts exactly one product SBOM
      exists with the versioned filename and verifies SPDX 3 JSON structure,
      module-level dependency entries, the required scopes, and representative
      available license fields.
- [x] Test the release script to prove it passes the SBOM path to
      `gh release create`.
- [ ] Run `mvn -T 1C verify` and resolve all test, coverage, reproducibility,
      static-analysis, and security-gate failures.
- [x] Run `./oft-self-trace.sh` and keep requirement, scenario, design,
      implementation, and test coverage clean.

### Update User Documentation

- [x] Update `doc/developer_guide.md` to state where the SBOM is produced, how
      to inspect it, and that release assets include it. Do not add end-user CLI
      documentation because this introduces no runtime option.

## Version And Changelog Update

- [ ] During release preparation, update the version according to the release
      policy and add the SPDX SBOM to `doc/changes/changes.md` and the matching
      versioned changelog file.
