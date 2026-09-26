# GH-610 Allow Adding Plugins via Configuration

## Goal

Provide a public, programmatic way to pass plugin JAR paths to OFT at runtime so that
embedders (e.g. the `openfasttrace-gradle` plugin, which resolves `pluginDependencies`
through a Gradle configuration) no longer need the fragile workaround of replacing the
thread context class loader (TCCL) with a child-first loader and copying OFT's built-in
provider JARs into it.

Today OFT can only discover third-party plugins from the fixed directory
`$HOME/.oft/plugins/<plugin-name>/*.jar`. `ServiceLoaderFactory` is package-private and
hardcodes that directory, `InitializingServiceLoader.load(Class<T>, C)` accepts no plugin
locations, and `ClassPathServiceLoader.filterOtherClassLoader` rejects any service whose
class loader differs from the origin's. As a result, embedders that resolve plugins through
their own dependency mechanism cannot register them without breaking built-in importing and
reporting.

## Scope

In scope:

* A public builder API in `core` to configure plugin JARs: `Oft.builder().addPlugin(...).build()`.
* Support for **multiple JARs per plugin**: each `addPlugin(Path... jars)` call forms one
  plugin loaded through a single `ChildFirstClassLoader`, matching the existing
  `$HOME/.oft/plugins/<plugin-name>/*.jar` semantics.
* Explicitly configured plugins are loaded **in addition to** the default plugin directory.
* Built-in providers continue to load from the current class path, so configuring a plugin
  no longer makes built-in importers/exporters/reporters disappear.
* Programmatic configuration only (no CLI flags).

Out of scope:

* CLI options for plugin configuration.
* Replacing or disabling the default `$HOME/.oft/plugins` discovery.
* Changing how built-in providers are discovered.
* Changing plugin packaging or the plugin developer contract.

## Design References

* [System Requirements](../spec/system_requirements.md)
* [Quality Requirements](../spec/design/quality_requirements.md)
* [Design](../spec/design.md) — "Discovering and Loading Plugins"

## Strategy

Keep the existing plugin-loading internals (`ServiceLoaderFactory`, `ServiceOrigin`,
`ChildFirstClassLoader`, `ClassPathServiceLoader`) unchanged in behavior and continue to
hide them behind the package-private boundary. Thread the configured plugin groups from the
public facade down to the three factory loaders:

1. `OftBuilder` (new, public, in `core`) collects plugin groups as `List<List<Path>>` and
   builds an `OftRunner` backed by a `ServiceFactory` that carries the groups.
2. `ServiceFactory` passes the groups to `ImporterFactoryLoader`, `ExporterFactoryLoader`,
   and `ReporterFactoryLoader`.
3. Those loaders call a new public `InitializingServiceLoader.load(Class<T>, C, List<List<Path>>)`
   overload, which builds a `ServiceLoaderFactory` that appends one `ServiceOrigin.forJars(group)`
   per configured plugin after the default-directory origins.

`Oft.create()` remains for backward compatibility and delegates to the builder with no
configured plugins. Validation of configured paths happens in `OftBuilder.addPlugin(...)`
so invalid input is rejected at the API boundary.

## Task List

- [ ] Create and checkout a new Git branch `feature/610_allow_adding_plugins_via_configuration`

### Requirements And Design

- [ ] Add `req~plugins.loading.configuration~1` to `doc/spec/system_requirements.md` under
      "Third Party Plugins" (Covers `feat~plugins~1`, Needs dsn)
- [ ] Stop and ask user for a review of the system requirements
- [ ] Add `dsn~plugins.loading.configuration~1` to `doc/spec/design.md` under
      "Discovering and Loading Plugins" (Covers the new requirement, Needs impl, utest, itest),
      documenting additive semantics, one class loader per configured plugin, and preservation
      of built-in providers
- [ ] Stop and ask user for a review of the design

### Implementation

- [ ] Add public `OftBuilder` in `core` with `addPlugin(Path... jars)` (reject null/empty
      groups and non-existent or non-`.jar` paths with `IllegalArgumentException`) and `build()`
- [ ] Add `Oft.builder()` returning `OftBuilder`; keep `Oft.create()` delegating to the builder
- [ ] Extend `ServiceLoaderFactory` to accept additional plugin groups and append one
      `ServiceOrigin.forJars(group)` per group in `findServiceOrigins()`
- [ ] Add public `InitializingServiceLoader.load(Class<T>, C, List<List<Path>>)` overload
- [ ] Add constructor overloads to `ImporterFactoryLoader`, `ExporterFactoryLoader`, and
      `ReporterFactoryLoader` that accept plugin groups
- [ ] Add `ServiceFactory(List<List<Path>> plugins)` constructor and keep the no-arg constructor
      for `Oft.create()`

### Verification

- [ ] `ServiceLoaderFactoryTest`: grouped JARs produce a single origin; configured origins are
      appended after default-directory origins
- [ ] `InitializingServiceLoaderTest`: the new overload forwards configured plugin groups
- [ ] New `OftBuilderTest`: parameter validation (null, empty, missing, non-JAR) and build wiring
- [ ] `ServiceLoaderFactoryIT` (product): load a real plugin JAR from a configured path and
      assert the plugin loads while built-in reporters remain available
- [ ] Keep the OpenFastTrace trace clean (`./oft-self-trace.sh`)
- [ ] Keep required build and plugin verification tasks green (`mvn -T 1C verify`)

### Update user documentation

- [ ] Document `Oft.builder().addPlugin(...)` in `doc/user_guide/oft_api/using_oft_from_java.md`
- [ ] Mention programmatic plugin configuration in `doc/plugins.md`

## Version and Changelog Update

- [ ] Raise the version to 4.11.0 (this is a feature release)
- [ ] Write the changelog entry for 4.11.0 in `doc/changes/changes_4.11.0.md` and link it from
      `doc/changes/changes.md`
