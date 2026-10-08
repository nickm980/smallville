# UEIA Java Toolchain Compatibility Review v1

## Purpose

This document reviews repo-grounded Java and build compatibility signals across the current Maven projects. It is limited to consistency review, unknowns, and mutation-risk identification. It does not authorize upgrades, build changes, or runtime implementation.

## Cross-Project Consistency Review

### Consistent Signals

- `smallville/pom.xml` and `java-client/pom.xml` both explicitly declare `maven.compiler.source` and `maven.compiler.target` as `17`.
- All repo-visible build entry points are Maven POM files.
- No repo-visible Gradle build files, wrappers, or CI pipeline files were found.
- No repo-visible parent POM, Maven reactor, or shared dependency-management block was found.

### Repo-Grounded Mismatches Or Unknowns

- `examples/java-example/pom.xml` does not explicitly declare `maven.compiler.source` or `maven.compiler.target`, so its effective compiler level is not fixed by repo-local declaration in the same way as `smallville` and `java-client`.
- `examples/java-example/pom.xml` depends on `com.github.nickm980:smallville:2b663b0`, which is an external commit-hash-style artifact reference rather than a repo-local module dependency.
- `smallville/pom.xml` declares a snapshot repository (`https://maven.reposilite.com/snapshots`), while `examples/java-example/pom.xml` declares `https://jitpack.io`.
- `smallville/pom.xml` includes RC / alpha style dependency versions:
  - `io.javalin.community.routing:routing-core:5.6.2-RC.1`
  - `io.javalin.community.routing:routing-annotated:5.6.2-RC.1`
  - `com.squareup.okhttp3:okhttp:5.0.0-alpha.11`
- `smallville/pom.xml` and `java-client/pom.xml` both include JUnit 4 test dependencies, while `java-client/pom.xml` also explicitly includes JUnit Jupiter and Vintage artifacts.
- Maven effective build behavior for `examples/java-example` remains unknown from repo-only inspection because no compiler plugin or compiler properties are declared there and Maven CLI is unavailable on `PATH` in the current environment.

## Compatibility Notes By Project

### `smallville`

- Explicit Java 17 compiler declarations reduce ambiguity for this module.
- Explicit compiler and shade plugins mean packaging behavior is at least partially declared in-repo.
- Snapshot / RC / alpha dependencies increase future mutation risk because they may have narrower compatibility windows than fully stable pinned versions.

### `java-client`

- Explicit Java 17 compiler declarations are consistent with `smallville`.
- The project declares a source-plugin build step but no explicit compiler or surefire plugin version beyond default Maven plugin resolution.
- Mixed JUnit 4, Jupiter, and Vintage test dependencies suggest compatibility is intentionally broad or transitional, but repo-only review cannot prove the effective test engine behavior without Maven execution.

### `examples/java-example`

- The example module is structurally the least self-contained from a compatibility standpoint because it relies on external artifact resolution through JitPack and does not explicitly declare compiler source/target values.
- The example module currently has no repo-visible test source root and no explicit plugin declarations.

## Mutation Risks If Changes Were Attempted Now

- changing Java level in one module without aligning the others could create cross-module incompatibility
- changing plugin versions in `smallville` could change compile or shaded packaging behavior
- changing plugin or dependency versions in `java-client` could alter test-engine expectations
- changing the example module without first deciding whether it should remain JitPack-based could break example reproducibility
- changing repositories or wrapper policy would widen scope from audit into build mutation
- treating current implementation-readiness notes as mutation authority would violate the frozen ladder boundaries

## Conditional Future Mutation Order If Later Approved

This order is conditional planning only and exists to reduce mutation risk if a later explicitly authorized change phase ever begins:

1. confirm which Maven projects are intended to remain first-class build entry points
2. confirm whether the example module should continue resolving `smallville` externally or be aligned with a repo-local strategy
3. confirm expected Java source/target declarations for every Maven project, including the example module
4. confirm desired plugin and test-engine policy across `smallville` and `java-client`
5. only after those decisions, evaluate any specific Java, plugin, repository, or dependency mutation proposal

## Boundary Carry-Forward

- This review does not change canonical-path discipline.
- This review does not weaken pre-decision versus review-decision separation where the ladder is referenced.
- Water Mill remains design-asset-derived context only unless accepted repo evidence states otherwise.
- Packet content remains interpretive artifact only and never runtime world state.
- This review does not define runtime behavior, workflow behavior, routing behavior, UI behavior, control behavior, scoring, ranking, or prioritization.

## Explicit Non-Authorization

"This document is audit-only and does not create a new governance authority seam."

"This document does not authorize Java, Maven, Gradle, plugin, dependency, wrapper, CI, or build-file mutation."

"Any future mutation order described here is conditional planning only and does not authorize upgrades."

"Implementation readiness does not imply implementation authority."
