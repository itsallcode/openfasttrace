# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

- Support building, testing and running with Java 16 [#287](https://github.com/itsallcode/openfasttrace/issues/287) / [#288](https://github.com/itsallcode/openfasttrace/issues/288)

## [3.2.1] - 2021-07-13

### Upgrade dependencies:
- Test dependencies
  - [JUnit](https://github.com/itsallcode/openfasttrace/pull/275)
  - [mockito-junit-jupiter](https://github.com/itsallcode/openfasttrace/pull/284)
  - [equalsverifier](https://github.com/itsallcode/openfasttrace/pull/285)
- Build dependencies
  - [license-maven-plugin](https://github.com/itsallcode/openfasttrace/pull/276)
  - [flatten-maven-plugin](https://github.com/itsallcode/openfasttrace/pull/278)
  - [maven-javadoc-plugin](https://github.com/itsallcode/openfasttrace/pull/279)
  - [plantuml](https://github.com/itsallcode/openfasttrace/pull/81)
  - [pitest-maven](https://github.com/itsallcode/openfasttrace/pull/282)
  - [maven-deploy-plugin](https://github.com/itsallcode/openfasttrace/pull/283)

## [3.2.0] - 2021-05-30

### Added

- Aspec (augmented specobject) exporter [#260](https://github.com/itsallcode/openfasttrace/issues/260) [#261](https://github.com/itsallcode/openfasttrace/pull/261)

### Changed

- Importing of files for which no importer or more than one importer is found don't let the build fail [#258](https://github.com/itsallcode/openfasttrace/pull/258)


## [3.1.0] - 2021-05-20

### Changed

- Added support for JVM languages: Clojure, Kotlin and Scala [#270](https://github.com/itsallcode/openfasttrace/issues/270) [#271](https://github.com/itsallcode/openfasttrace/issues/271)

## [3.0.2]

### Fixed

- Fix deployment to maven central: attributes in pom.xml where missing.

## [3.0.1]

### Fixed

- Published pom files contained invalid version: `<version>${revision}</version>`

## [3.0.0]

### Changed

- Added support for more file types and use Java 11 [#238](https://github.com/itsallcode/openfasttrace/issues/238) [#239](https://github.com/itsallcode/openfasttrace/issues/239)
- Rename Java packages so that they are unique for each module [#237](https://github.com/itsallcode/openfasttrace/pull/237)

## [2.3.5] - 2019-04-14

### Changed

- New project structure with sub-modules [#213](https://github.com/itsallcode/openfasttrace/issues/216) [#213](https://github.com/itsallcode/openfasttrace/pull/213)
- Extract reporters into sub-module [#221](https://github.com/itsallcode/openfasttrace/issues/221) [#226](https://github.com/itsallcode/openfasttrace/pull/226)
- Simplify maven setup [#225](https://github.com/itsallcode/openfasttrace/pull/225) [#224](https://github.com/itsallcode/openfasttrace/issues/224)

### Added

- Pretty print specobjects [#229](https://github.com/itsallcode/openfasttrace/pull/229) [#219](https://github.com/itsallcode/openfasttrace/issues/219)

### Fixed

- Fix maven project deployment [#231](https://github.com/itsallcode/openfasttrace/issues/231) [#231](https://github.com/itsallcode/openfasttrace/pull/232)
- Export specobject title [#212](https://github.com/itsallcode/openfasttrace/pull/212/) [#209](https://github.com/itsallcode/openfasttrace/issues/209)
- Fix exit codes [#218](https://github.com/itsallcode/openfasttrace/pull/218) [#215](https://github.com/itsallcode/openfasttrace/issues/215)

## [2.3.4] - 2019-04-14 [YANKED]

## [2.3.3] - 2019-04-14 [YANKED]

## [2.3.2] - 2019-04-05 [YANKED]

## [2.3.1] - 2019-03-31 [YANKED]

## [2.3.0] - 2019-03-31 [YANKED]
