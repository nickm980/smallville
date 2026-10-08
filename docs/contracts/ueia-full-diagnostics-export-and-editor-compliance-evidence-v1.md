# UEIA Full Diagnostics Export and Editor Compliance Evidence v1

## 1. Scope and Authority

This document is diagnostics-only, read-only, and non-authorizing.

It records only what the current environment exposed during this session and does not authorize fixes, implementation, build mutation, dependency mutation, toolchain mutation, IDE mutation, or scope widening.

Frozen continuity remains unchanged: baseline branch context remains `main`, the frozen implementation anchor remains `419952b`, the explicit-only validator slice remains compile-proven and test-proven, targeted evidence remains 23 tests with 0 failures and 0 errors, the conformance vocabulary remains exactly `ALLOWED`, `ALLOWED_WITH_STOP_RULES`, and `NOT_ALLOWED`, unresolved cells remain represented as `Optional.empty()`, unresolved-cell implementation remains blocked, toolchain mutation remains blocked, and the project remains in a valid stop-state unless explicit new authority changes that.

## 2. Evidence Sources Used

- Existing frozen continuity artifacts read in this session:
  - `docs/contracts/ueia-java-toolchain-audit-v1.md`
  - `docs/contracts/ueia-problem-triage-inventory-v1.md`
  - `docs/contracts/ueia-diagnostics-capture-v1.md`
  - `docs/contracts/ueia-unresolved-cell-strategy-evaluation-v1.md`
- Workspace/editor diagnostics surface as exposed by the current environment:
  - workspace-scope diagnostics query returning `216` total visible problems and a first-page detailed subset of `50`
  - targeted diagnostics re-queries for `smallville`, `java-client`, and `examples/java-example`
  - targeted diagnostics re-queries for the specific files visible in the workspace-scope subset
- Observable Java runtime and compiler context from the current shell:
  - `java -version`
  - `javac -version`
  - executable path lookup for `java` and `javac`
  - `JAVA_HOME`
- Workspace file-system inspection limited to diagnostics interpretation:
  - `smallville/pom.xml`
  - absence/presence search for `.settings`, `.classpath`, `.project`, and `.vscode/settings.json`
  - UEIA fixture source and test files in `smallville`

No build was run in this lane. No settings, caches, or files were mutated to improve visibility.

## 3. Diagnostics Visibility Envelope

- Exact visible total: `216` visible problems.
- Exact or approximate: exact for the aggregate Problems total exposed by the current environment.
- Detailed problem list status: partial.
- Full export availability: not available through the exposed diagnostics interface in this session.
- Maximum detailed export actually obtained: the first `50` problem entries returned by a workspace-scope diagnostics query.
- Stability of detailed export: limited.
- Aggregate versus targeted contradiction observed:
  - the workspace-scope query exposed `216` total problems and a detailed first-page subset of `50`
  - a targeted query for `c:\SmallVille\smallville` returned no errors
  - targeted queries for `c:\SmallVille\java-client` and `c:\SmallVille\examples\java-example` returned no errors
  - targeted file-level re-queries for the specific files named in the workspace-scope subset also returned no errors
- Conservative interpretation of the contradiction: the environment exposes an exact aggregate total, but the detailed diagnostics transport is incomplete and internally inconsistent. The aggregate count therefore cannot be treated as a complete per-file inventory, and the targeted zero-error replays cannot be treated as proof that the aggregate findings were false.

## 4. Diagnostics Export

Full export was not possible in this session. The fullest stable inventory available was the workspace-scope first-page subset of `50` problem entries, reproduced below as observed. Items not listed here were not exposed in detailed form by the environment.

### Observed subset from workspace-scope diagnostics query

#### File: `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtures.java`

Observed count: `20`

- `FixtureDefinition cannot be resolved to a type` at lines 11, 12, 13, 14, 15, 22, 27, 35
- `FixtureRef cannot be resolved to a type` at lines 21, 25, 35, 37
- `'record' is not a valid type name; it is a restricted identifier and not allowed as a type identifier in Java 1.8` at lines 63 and 71
- `Syntax error, insert "VariableDeclarators" to complete LocalVariableDeclaration` at lines 64 and 72
- `Syntax error, insert ";" to complete BlockStatements` at lines 64 and 72
- `FixtureRef cannot be resolved` at line 64
- `FixtureDefinition cannot be resolved` at line 72

#### File: `smallville/src/main/java/io/github/nickm980/smallville/api/v1/SimulationController.java`

Observed count: `2`

- `The import com.google.gson.reflect.TypeToken is never used` at line 23
- `The value of the field SimulationController.gson is not used` at line 40

#### File: `smallville/src/main/java/io/github/nickm980/smallville/Smallville.java`

Observed count: `1`

- `The import io.github.nickm980.smallville.events.agent.AgentUpdateEvent is never used` at line 17

#### File: `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtureSectionSpanIndexTest.java`

Observed count: `12`

- `CanonicalFixtureSectionSpanIndex.IndexedFixtureSections cannot be resolved to a type` at lines 26, 40, 41, 54
- `The method indexAll() from the type CanonicalFixtureSectionSpanIndex refers to the missing type IndexedFixtureSections` at lines 26, 40, 41
- `The method require(CanonicalFixtures.FixtureRole) from the type CanonicalFixtures refers to the missing type FixtureRef` at line 37
- `CanonicalFixtureHeadingIndex.IndexedFixtureHeadings cannot be resolved to a type` at line 53
- `The method index(CanonicalFixtures.FixtureRole) from the type CanonicalFixtureHeadingIndex refers to the missing type IndexedFixtureHeadings` at line 53
- `The method index(CanonicalFixtures.FixtureRole) from the type CanonicalFixtureSectionSpanIndex refers to the missing type IndexedFixtureSections` at line 54
- `var cannot be resolved to a type` at line 66

#### File: `smallville/src/main/java/io/github/nickm980/smallville/memory/MemoryStream.java`

Observed count: `3`

- `The import java.util.Collections is never used` at line 4
- `The import java.util.HashMap is never used` at line 6
- `The import java.util.Map is never used` at line 8

#### File: `smallville/src/main/java/io/github/nickm980/smallville/entities/Conversation.java`

Observed count: `1`

- `The import io.github.nickm980.smallville.exceptions.SmallvilleException is never used` at line 7

#### File: `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtureTextLoaderTest.java`

Observed count: `11`

- `CanonicalFixtureTextLoader.LoadedFixtureText cannot be resolved to a type` at lines 27, 35, 46, 47, 60
- `The method loadAll() from the type CanonicalFixtureTextLoader refers to the missing type LoadedFixtureText` at lines 27, 46, 47
- `The method require(CanonicalFixtures.FixtureRole) from the type CanonicalFixtures refers to the missing type FixtureRef` at lines 43 and 59
- `CanonicalFixtures.FixtureRef cannot be resolved to a type` at line 59

### Observed but unavailable in detailed form

- Remaining aggregate-visible problems not exposed in detailed form: `166`
- Stable full per-file export for all `216` problems: unavailable
- Stable folder-level export for `smallville`: unavailable, because the targeted folder query returned no errors
- Stable file-level replay for the files listed above: unavailable, because direct file re-queries returned no errors despite the workspace-scope subset showing errors for those same files
- Comparable detailed diagnostics for `java-client` and `examples/java-example`: unavailable through targeted queries because both module-level queries returned no errors

## 5. Editor / Java Compliance Evidence

Only directly observable signals are recorded below.

- `java -version`:
  - `openjdk version "17.0.18" 2026-01-20`
  - `OpenJDK Runtime Environment Temurin-17.0.18+8`
  - `OpenJDK 64-Bit Server VM Temurin-17.0.18+8`
- `javac -version`:
  - `javac 17.0.18`
- Executable paths:
  - `java`: `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\java.exe`
  - `javac`: `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\javac.exe`
- `JAVA_HOME`:
  - `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\`
- Observable workspace/editor Java compliance setting:
  - no direct workspace/editor compliance setting was exposed by the available tools in this session
- Observable language-service or project-configuration signals:
  - the diagnostics text itself reported Java-1.8-style parsing restrictions in `smallville`, including `'record' ... not allowed ... in Java 1.8` and `var cannot be resolved to a type`
  - direct source inspection confirms Java-16+-level `record` declarations and `Stream.toList()` usage in `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtures.java`
  - direct test-source inspection confirms `var` usage in `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtureSectionSpanIndexTest.java`
- Presence or absence of common editor/project metadata files:
  - `.settings`: not found in the workspace
  - `.classpath`: not found in the workspace
  - `.project`: not found in the workspace
  - `.vscode/settings.json`: not found in the workspace
- Direct comparison to `smallville/pom.xml`:
  - `smallville/pom.xml` explicitly declares `maven.compiler.source=17`
  - `smallville/pom.xml` explicitly declares `maven.compiler.target=17`
  - `smallville/pom.xml` explicitly configures `maven-compiler-plugin` version `3.9.0`
- Match versus conflict assessment:
  - observable shell/runtime/compiler context matches Java 17
  - repo declarations in `smallville/pom.xml` match Java 17
  - visible editor diagnostics conflict with that posture because they reflect Java-1.8-style parsing for `record` and `var`

## 6. Cluster Consolidation

### Cluster A

- Cluster name: UEIA fixture main-source language-level mismatch
- Estimated count: `20` visible problems in the exported subset
- Affected modules/files: `smallville`, centered on `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtures.java`
- Problem type: Java language-level parsing and downstream symbol resolution
- Primary or cascading: primary
- Confidence level: high

### Cluster B

- Cluster name: UEIA fixture test fallout
- Estimated count: `23` visible problems in the exported subset
- Affected modules/files: `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtureSectionSpanIndexTest.java` and `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtureTextLoaderTest.java`
- Problem type: missing nested types, missing method result types, and test-source language-level fallout
- Primary or cascading: mostly cascading from Cluster A, with a possible shared language-level parsing component
- Confidence level: high

### Cluster C

- Cluster name: independent unused-import and unused-field warnings
- Estimated count: `7` visible problems in the exported subset
- Affected modules/files: `smallville/src/main/java/io/github/nickm980/smallville/api/v1/SimulationController.java`, `smallville/src/main/java/io/github/nickm980/smallville/Smallville.java`, `smallville/src/main/java/io/github/nickm980/smallville/memory/MemoryStream.java`, and `smallville/src/main/java/io/github/nickm980/smallville/entities/Conversation.java`
- Problem type: editor hygiene warnings
- Primary or cascading: primary but low-severity and apparently independent of Clusters A and B
- Confidence level: medium

### Cluster D

- Cluster name: diagnostics transport inconsistency
- Estimated count: applies to the entire unexported remainder of the `216`-problem aggregate because detailed replay was incomplete and contradictory
- Affected modules/files: workspace-level diagnostics surface; observed most clearly against `smallville`
- Problem type: aggregate-versus-targeted diagnostics mismatch
- Primary or cascading: primary environment-observation issue
- Confidence level: high

Repo structure signal from the observed diagnostics surface remains conservative:

- The exported detailed subset is concentrated in `smallville`.
- Targeted module queries for `java-client` and `examples/java-example` returned no errors.
- No comparable detailed diagnostic cluster was exposed for those other Java modules in this session.
- Because the environment did not expose a complete detailed inventory, concentration outside `smallville` cannot be ruled out for the unseen remainder, but it was not observed.

## 7. Strongest Current Root-Cause Hypotheses

### Hypothesis 1

- Most likely root-cause category: editor/language-service Java compliance mismatch against `smallville`
- Evidence basis:
  - aggregate diagnostics explicitly report Java-1.8-style restrictions for `record`
  - aggregate diagnostics explicitly report `var cannot be resolved to a type`
  - direct source inspection confirms use of `record`, `Stream.toList()`, and `var`
  - `smallville/pom.xml` explicitly declares Java 17 source and target
  - observable shell runtime and compiler are Java 17
- Confidence level: high

### Hypothesis 2

- Most likely root-cause category: cascade from the primary language-level mismatch into dependent UEIA fixture test symbols
- Evidence basis:
  - the test failures are dominated by missing nested types and missing method result types that depend on the UEIA fixture main-source types
  - the affected tests sit in the same UEIA fixture slice as the main-source parsing mismatch
- Confidence level: high

### Hypothesis 3

- Most likely root-cause category: diagnostics-surface instability or editor-side stale state affecting export fidelity
- Evidence basis:
  - workspace-scope diagnostics reported `216` problems and exposed a 50-entry subset
  - the same workspace returned no errors when queried at `smallville`, `java-client`, and `examples/java-example` folder scope
  - the same individual files named in the workspace-scope subset returned no errors when queried directly
- Confidence level: high

Conservative answer to the current aggregate hypothesis:

- Yes, the observed evidence supports the hypothesis that the visible `216`-problem surface is primarily an editor/language-level mismatch plus cascades.
- That support is strong for the visible subset and for the Java-compliance conflict signal.
- It is not complete proof for all `216` problems, because detailed export remained partial and internally inconsistent.

## 8. What Remains Unknown

- The full per-file inventory for all `216` visible problems remains unknown.
- The file and module distribution of the unseen `166` detailed problems remains unknown.
- Whether any material portion of the unseen `166` problems sits outside `smallville` remains unknown.
- The editor or language-service's actual effective compliance setting for the `smallville` project was not directly exposed.
- Whether the aggregate Problems view reflects persistent project-model mismatch, stale editor cache state, language-server desynchronization, or another editor-side condition cannot be determined from this environment alone.
- Whether the aggregate-visible problems correspond to real compile-surface failures remains unknown in this lane, because editor diagnostics are not build proof and no build-validation lane was authorized here.
- Whether the targeted zero-error replays mean partial refresh, stale aggregate state, or limitations in the diagnostics query mechanism remains unknown.

## 9. Safest Next Move

Authorize a narrower diagnostics-only follow-up that is limited to extracting direct editor/language-service project-model and effective compliance metadata for `smallville`, because the current evidence already strongly indicates a Java compliance mismatch but the diagnostics transport remains too internally inconsistent to justify any fix lane.

## 10. What This Document Does Not Authorize

- no fixes
- no code edits
- no build-file edits
- no dependency changes
- no unresolved-cell implementation
- no consuming-seam design
- no runtime / UI / API widening
- no toolchain mutation
- no IDE or workspace configuration mutation