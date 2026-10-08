# UEIA Java / Toolchain Audit

This is a regenerated, audit-only, handover-grade summary. Compile and test proof remains handover-verified and was not repo-rederived in this session.

## Authority Classification

- Audit-only. This review does not create a new governance authority seam.
- Non-authorizing. This review does not authorize Java, Maven, plugin, dependency, wrapper, CI, build-file, or runtime-code mutation.
- Repo declarations remain the source of truth. Environment observations support audit context only and do not override repo declarations.
- Canonical-path discipline remains unchanged.
- Pre-decision versus review-decision separation remains unchanged where the ladder is referenced.
- Water Mill remains design-asset-derived context only unless accepted repo evidence states otherwise.
- Packet content remains interpretive artifact only and never runtime world state.
- This review does not define runtime behavior, workflow behavior, routing behavior, UI behavior, control behavior, scoring, ranking, or prioritization.

## 1. Build Surface

### Repo-grounded evidence

- `smallville/pom.xml` defines the main SmallVille Maven build for artifact `io.github.nickm980:smallville:1.3.0`.
- `java-client/pom.xml` defines a separate Maven build for artifact `io.github.nickm980:smallville-client:1.0.0`.
- `examples/java-example/pom.xml` defines a separate Maven build for artifact `io.github.nickm980.smallville:example:0.0.1-SNAPSHOT`.
- `jitpack.yml` is present and targets only the `java-client` module for automated install flow.

### Attached frozen handover evidence

- The explicit-only validator slice at `419952b` is compile-proven and test-proven. Under the frozen checkpoint and full handover authority, that proof is handover-verified and is carried forward here as continuity evidence rather than repo-rederived in this session.

### Workspace-local absence

- No workspace-root aggregator Maven build file was inspected. The visible Java build surface is module-local rather than a single Maven reactor build.
- No workspace-local attached frozen checkpoint or full handover markdown file is present for direct re-reading in this session, so the compile/test proof remains classified here as handover-verified rather than repo-rederived.

### Operational shape

- The repo exposes three separate Maven entry points through `smallville/pom.xml`, `java-client/pom.xml`, and `examples/java-example/pom.xml`.
- `jitpack.yml` adds build metadata for only one of those modules, creating an asymmetric visible automation surface.

## 2. Java and Compiler Surface

### Repo-grounded evidence

- `smallville/pom.xml` explicitly sets `maven.compiler.source=17` and `maven.compiler.target=17`.
- `java-client/pom.xml` explicitly sets `maven.compiler.source=17` and `maven.compiler.target=17`.
- `smallville/pom.xml` explicitly configures `org.apache.maven.plugins:maven-compiler-plugin:3.9.0` and replaces Maven’s default `compile` and `testCompile` executions with named executions.
- `java-client/pom.xml` does not declare an explicit compiler plugin, so compilation behavior depends on Maven defaults plus the declared source and target properties.
- `examples/java-example/pom.xml` declares no compiler source, target, release, or compiler plugin configuration.
- `jitpack.yml` pins the automation environment to OpenJDK `17.0.3` for the `java-client` build flow.

### Attached frozen handover evidence

- The UEIA explicit-only validator slice is already treated as compile-proven and test-proven under the frozen handover authority. This audit does not reopen that proof.

### Workspace-local absence

- There is no repo-grounded explicit compiler-level declaration in `examples/java-example/pom.xml`.
- There is no workspace-local build metadata in this audit showing a shared compiler policy across all three modules.

### Interpretation

- Java 17 is explicit for `smallville/pom.xml` and `java-client/pom.xml`.
- The effective Java and compiler level for `examples/java-example/pom.xml` is not repo-explicit in the inspected metadata.

## 3. Dependency and Repository Surface

### Repo-grounded evidence from `smallville/pom.xml`

- External repository declared: `https://maven.reposilite.com/snapshots`.
- Build plugins declared: `maven-compiler-plugin:3.9.0` and `maven-shade-plugin:3.2.4`.
- Packaging behavior includes shading with `createDependencyReducedPom=false` and a manifest main class of `io.github.nickm980.smallville.Smallville`.
- Core dependency surface includes Javalin, Javalin test tools, Jackson, SnakeYAML, SLF4J, Stanford CoreNLP, Easy BERT, OkHttp, Gson, Mustache, JCommander, SimpleNLG, JWI, Commons CLI, MockK, Mockito, and JUnit 4.
- Pre-release dependencies are present: `io.javalin.community.routing:routing-core:5.6.2-RC.1`, `io.javalin.community.routing:routing-annotated:5.6.2-RC.1`, and `com.squareup.okhttp3:okhttp:5.0.0-alpha.11`.

### Repo-grounded evidence from `java-client/pom.xml`

- Build plugin declared: `maven-source-plugin:3.3.1`.
- Runtime and library dependencies: `org.json:json:20231013` and `com.google.code.gson:gson:2.10.1`.
- Test dependencies: `junit:4.13.2`, `junit-jupiter-api:5.8.0`, `junit-jupiter-engine:5.8.0`, and `junit-vintage-engine:5.8.0`.

### Repo-grounded evidence from `examples/java-example/pom.xml`

- External repository declared: `https://jitpack.io`.
- Single direct dependency declared: `com.github.nickm980:smallville:2b663b0`.

### Repo-grounded evidence from `jitpack.yml`

- JitPack installs Maven and runs Maven install against `java-client/pom.xml` with tests skipped.

### Workspace-local absence

- No additional build metadata was inspected showing repository policy normalization across modules.
- No workspace-local metadata was inspected showing lockfile-style dependency pinning or a central BOM.

### Interpretation

- The dependency surface is split and not centrally governed by a visible parent build file.
- External artifact resolution relies on both a snapshot repository and JitPack, with one example module pinned to a commit-like artifact version rather than a local module version.

## 4. Module Coupling Surface

### Repo-grounded evidence

- `smallville/pom.xml` is a standalone module and does not declare a parent or local inter-module dependency on `java-client/pom.xml` or `examples/java-example/pom.xml`.
- `java-client/pom.xml` is a standalone module and does not declare a parent or dependency on the local `smallville` module.
- `examples/java-example/pom.xml` depends on `com.github.nickm980:smallville:2b663b0` through JitPack rather than the local coordinates declared in `smallville/pom.xml`, which are `io.github.nickm980:smallville:1.3.0`.
- `jitpack.yml` automates only the `java-client` path, not the `smallville` or `examples/java-example` paths.

### Workspace-local absence

- No inspected root aggregator Maven build file ties `smallville/pom.xml`, `java-client/pom.xml`, and `examples/java-example/pom.xml` into a single reactor.
- No inspected build metadata establishes a shared release train or unified versioning policy across the three modules.

### Interpretation

- Coupling is loose at the Maven-structure level.
- The example module is coupled to an external published artifact lineage rather than the local workspace module identity.
- The visible automated build path is narrower than the visible module set.

## 5. Mutation Risk Surface

### Repo-grounded risks

- `smallville/pom.xml` depends on non-final dependencies and a snapshot repository, which increases artifact-resolution variability.
- `smallville/pom.xml` uses shaded packaging plus custom compiler execution replacement, which increases the regression surface of any future build mutation.
- `java-client/pom.xml` mixes JUnit 4, JUnit 5, and Vintage Engine in one module, which increases test-execution variability risk if later changes are made.
- `examples/java-example/pom.xml` depends on a JitPack artifact with commit-like version `2b663b0`, which increases drift risk between local module metadata and example consumption behavior.
- `examples/java-example/pom.xml` does not declare explicit compiler level metadata, which creates uncertainty relative to the explicit Java 17 posture in `smallville/pom.xml` and `java-client/pom.xml`.
- `jitpack.yml` builds only `java-client/pom.xml`, leaving the visible automation surface narrower than the visible module surface.

### Attached frozen handover evidence

- The compile/test proof for the UEIA explicit-only validator slice reduces uncertainty for that slice specifically, but it does not remove the separate build-surface asymmetries visible across `smallville/pom.xml`, `java-client/pom.xml`, `examples/java-example/pom.xml`, and `jitpack.yml`.

### Workspace-local absence

- No workspace-local attached handover artifact was available for direct reinspection here, so this audit does not expand handover claims beyond the explicitly carried compile/test proof.
- No workspace-local unified build metadata was found that would reduce the observed asymmetry across modules.

### Conservative conclusion

- The dominant build risk is not one isolated version choice. It is the combination of module-local build definitions, uneven automation coverage, external repository reliance, pre-release dependencies in `smallville/pom.xml`, and external artifact coupling in `examples/java-example/pom.xml`.

## 6. What This Audit Does Not Authorize

- This audit does not authorize edits to `smallville/pom.xml`, `java-client/pom.xml`, `examples/java-example/pom.xml`, or `jitpack.yml`.
- This audit does not authorize Java upgrades, Maven upgrades, plugin upgrades, dependency upgrades, repository changes, or CI changes.
- This audit does not authorize widening into unresolved-cell handling, consuming-seam design, runtime work, UI/API work, `java-client` implementation work, or project-structure changes.
- This audit records repo-grounded evidence, handover-verified continuity evidence, and workspace-local absences only. It is read-only and suitable for carry-forward into a future handover.