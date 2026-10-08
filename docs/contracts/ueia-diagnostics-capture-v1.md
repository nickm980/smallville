# UEIA Diagnostics Capture v1

## 1. Scope and Authority

This document is diagnostics-only, read-only, and non-authorizing. It records the currently observable diagnostics surface and related Java/editor context for the `smallville` module without authorizing fixes, implementation, toolchain mutation, or scope widening.

The frozen baseline remains unchanged: baseline branch context remains `main`, the frozen implementation anchor remains `419952b`, the explicit-only validator slice remains compile-proven and test-proven, targeted evidence remains 23 tests with 0 failures and 0 errors, the conformance vocabulary remains exactly `ALLOWED`, `ALLOWED_WITH_STOP_RULES`, and `NOT_ALLOWED`, unresolved cells remain represented as `Optional.empty()`, unresolved-cell implementation remains blocked, toolchain mutation remains blocked, and the project remains in a valid stop-state unless explicit new authority changes that.

## 2. Diagnostics Visibility

- Total visible problem count: 216.
- Count exact or approximate: exact for the aggregate Problems total exposed by the editor diagnostics surface.
- Diagnostics source: the workspace/editor diagnostics API as surfaced through the environment.
- Detailed list complete or partial: partial.
- Known visibility limits: the diagnostics API exposed only the first 50 problem entries when queried at workspace scope.
- Known visibility limits: repeated targeted diagnostics queries against `smallville/src/main/java`, `smallville/src/test/java`, `smallville`, `java-client`, `examples/java-example`, and `dashboard` returned no errors, which conflicts with the aggregate 216-problem count.
- Known visibility limits: because of that inconsistency, the environment exposed the full visible count but did not expose a stable, complete per-file inventory through the available diagnostics interface.

## 3. Editor / Compliance Context

- Effective Java compliance level if visible: not directly exposed as a stable editor setting through the available tools.
- Effective Java compliance signal from diagnostics text: visible aggregate diagnostics explicitly reported `record` as invalid in Java 1.8 and reported `var cannot be resolved to a type` in UEIA fixture tests.
- JDK/runtime/version if visible: the observable system runtime and compiler were `openjdk version "17.0.18" 2026-01-20` and `javac 17.0.18`, both from Eclipse Temurin 17 at `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin`.
- Language-service or editor-state signal relevant to interpretation: the diagnostics surface appears internally inconsistent because aggregate workspace diagnostics reported 216 problems while narrower folder-level diagnostics calls returned no errors.
- Workspace Java configuration signal relevant to interpretation: no workspace `.settings` directory or workspace `settings.json` was found in the inspected paths.
- Whether the observed context matches or conflicts with `smallville/pom.xml`: `smallville/pom.xml` explicitly declares `maven.compiler.source=17` and `maven.compiler.target=17`, so the visible Java 1.8-style editor diagnostics conflict with the module's declared compiler posture.

## 4. Problem Cluster Map

### Cluster A

- Cluster name: UEIA fixture main-source Java language-level mismatch
- Estimated count: 20 visible instances in the reviewed subset
- Affected modules/files: `smallville`, especially `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtures.java`
- Problem type: source parsing, language-level compatibility, and downstream symbol resolution
- Whether it looks primary or cascading: primary

### Cluster B

- Cluster name: UEIA fixture test fallout from missing parsed types
- Estimated count: 23 visible instances in the reviewed subset
- Affected modules/files: `smallville`, especially `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtureSectionSpanIndexTest.java` and `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtureTextLoaderTest.java`
- Problem type: test-surface symbol resolution and source-set language-feature parsing
- Whether it looks primary or cascading: cascading

### Cluster C

- Cluster name: Independent unused-import and unused-field warnings
- Estimated count: 7 visible instances in the reviewed subset
- Affected modules/files: `smallville/src/main/java/io/github/nickm980/smallville/api/v1/SimulationController.java`, `smallville/src/main/java/io/github/nickm980/smallville/Smallville.java`, `smallville/src/main/java/io/github/nickm980/smallville/memory/MemoryStream.java`, and `smallville/src/main/java/io/github/nickm980/smallville/entities/Conversation.java`
- Problem type: editor-only / IDE-only hygiene warnings
- Whether it looks primary or cascading: primary, but low-severity and apparently independent

## 5. Root-Cause Candidates

- Primary cluster: UEIA fixture main-source Java language-level mismatch
- Most likely root-cause category: editor/language-service compliance mismatch or stale environment/tooling surface
- Evidence basis: aggregate diagnostics explicitly referenced Java 1.8 restrictions for `record`, while code inspection shows Java 16+ `record` usage and Java 16+ `Stream.toList()` usage in the same UEIA fixture slice, and `smallville/pom.xml` declares source/target 17.
- Confidence level: high

- Primary cluster: independent unused-import and unused-field warnings
- Most likely root-cause category: ordinary editor-only hygiene diagnostics unrelated to the dominant UEIA language-level mismatch
- Evidence basis: the visible warnings are localized to unused imports and an unused field in non-UEIA files and do not depend on the Java 1.8 parsing symptom.
- Confidence level: medium

- Primary cluster candidate at aggregate level: diagnostics-surface inconsistency
- Most likely root-cause category: incomplete or unstable diagnostics visibility in the current environment rather than a proven broad compile surface
- Evidence basis: aggregate workspace diagnostics reported 216 problems, but repeated narrower diagnostics calls for folders and modules returned no errors, preventing a stable full inventory.
- Confidence level: high

## 6. What Remains Unknown

- The complete per-file inventory behind the aggregate 216 visible problems remains unknown because the environment exposed only the first 50 problem entries.
- Whether the remaining 166 visible problems are mostly additional cascades of the same Java-language mismatch or separate independent issues remains unknown.
- The editor or language-service's actual effective compliance setting for the `smallville` module is not directly exposed through the available tools.
- Whether the aggregate Problems view reflects transient stale cache state, persistent language-service misconfiguration, or some other editor-side condition remains unknown.
- Build truth is still unknown from this lane alone because editor diagnostics were inconsistent and no build-validation lane was authorized here.

## 7. Safest Next Move

Authorize a narrower diagnostics capture lane only if needed to extract a stable complete Problems inventory and direct editor/language-service compliance metadata; until then, hold because the current visibility is exact on total count but still incomplete and internally inconsistent at detail level.

## 8. What This Document Does Not Authorize

- No fixes
- No code edits
- No build-file edits
- No dependency changes
- No unresolved-cell implementation
- No consuming-seam design
- No runtime / UI / API widening
- No toolchain mutation