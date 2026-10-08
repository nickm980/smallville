# UEIA Problem Triage Inventory v1

## 1. Scope and Authority

This document is evaluation-only, read-only, and non-authorizing. It records a bounded diagnostics triage of the currently visible problem set and does not authorize fixes, implementation, or workflow widening.

The frozen baseline remains unchanged: baseline context remains `main`, the frozen implementation anchor remains `419952b`, the explicit-only validator slice remains compile-proven and test-proven, targeted evidence remains 23 tests with 0 failures and 0 errors, the conformance vocabulary remains exactly `ALLOWED`, `ALLOWED_WITH_STOP_RULES`, and `NOT_ALLOWED`, unresolved cells remain `Optional.empty()`, unresolved-cell implementation remains blocked, toolchain mutation remains blocked, and the project remains in a valid stop-state unless explicit new authority changes that.

## 2. Problem Set Visibility

- 216 problems were visible through the workspace/editor diagnostics surface in this session.
- That 216 count is exact for the visible Problems total.
- Detailed inspection used the editor diagnostics API and targeted read-only file inspection in the `smallville` module plus existing UEIA contract notes in `docs/contracts`.
- The detailed enumerated list was partial rather than complete: the diagnostics API exposed only the first 50 problem entries for direct review, and follow-up per-file re-queries were not fully consistent with the aggregate Problems view.
- The triage below is therefore grounded in the exact visible total count and the visible 50-entry subset, without inventing unseen diagnostics.

## 3. Problem Clusters

### Cluster A

- Cluster name: UEIA fixture Java language-level parsing mismatch
- Estimated count: 20 visible instances in the reviewed subset
- Affected module(s): `smallville` main source, centered on `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture`
- Problem type: import / symbol resolution, source parsing, and language-feature compatibility signals
- Whether the cluster looks primary or cascading: primary
- Visible evidence: the Problems view reported `record` as not allowed in Java 1.8 and then reported missing nested types such as `FixtureRef` and `FixtureDefinition` in `CanonicalFixtures.java`; code inspection shows Java 16+ `record` declarations and `Stream.toList()` usage in the same UEIA fixture slice while `smallville/pom.xml` declares Java 17.

### Cluster B

- Cluster name: UEIA fixture test symbol fallout
- Estimated count: 23 visible instances in the reviewed subset
- Affected module(s): `smallville` test source, centered on `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture`
- Problem type: test-surface import / symbol resolution and source-set language-feature parsing
- Whether the cluster looks primary or cascading: cascading
- Visible evidence: test diagnostics reported missing nested fixture types such as `CanonicalFixtureTextLoader.LoadedFixtureText` and `CanonicalFixtureSectionSpanIndex.IndexedFixtureSections`, plus `var cannot be resolved to a type`; these symptoms align with the main-source parse failures and with Java 10+ `var` usage in the visible tests.

### Cluster C

- Cluster name: Non-UEIA unused import and unused field warnings
- Estimated count: 7 visible instances in the reviewed subset
- Affected module(s): `smallville` runtime and API sources including `api/v1`, the application entrypoint, `memory`, and `entities`
- Problem type: editor-only / IDE-only hygiene warnings
- Whether the cluster looks primary or cascading: primary, but low-severity and independent in the visible subset
- Visible evidence: the Problems view reported unused imports and an unused field in `SimulationController.java`, `Smallville.java`, `MemoryStream.java`, and `Conversation.java`.

## 4. Root-Cause Candidates

- Cluster A most likely root-cause category: stale cache / environment / tooling surface, specifically an IDE or language-service Java compliance mismatch interpreting UEIA fixture files as Java 1.8 while module metadata declares Java 17.
- Cluster A secondary candidate category: package / source-set issue limited to editor configuration rather than repo build metadata, because the visible problem text references Java 1.8 syntax restrictions while the module POM explicitly sets source and target to 17.
- Cluster B most likely root-cause category: cascading import / symbol resolution failures caused by Cluster A, with an additional likely source-set language-level issue where test files using `var` are parsed under an older effective Java level.
- Cluster C most likely root-cause category: editor-only hygiene warnings unrelated to the frozen UEIA authority boundary and not a visible root cause for the dominant UEIA error cluster.

## 5. Blocked vs Potentially Actionable Categories

### Clearly blocked under the current frozen authority

- Any fix to UEIA main-source diagnostics in `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture`
- Any fix to UEIA test diagnostics in `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture`
- Any edit to `smallville/pom.xml`, `java-client/pom.xml`, `examples/java-example/pom.xml`, or `jitpack.yml`
- Any dependency / resolution change
- Any toolchain mutation, IDE configuration mutation, or runtime design change
- Any unresolved-cell implementation work, consuming-seam design, vocabulary expansion, or runtime / UI / API widening
- Any cleanup edits for the non-UEIA unused import or unused field warnings

### Could be addressed later only under new explicit authority

- A narrower diagnostics-only lane to obtain a complete exported Problems inventory rather than the partially exposed 50-entry subset
- A bounded environment triage lane to verify the editor's effective Java compliance level, JDK selection, and language-service state for the `smallville` module without mutating code or build files
- A later bounded fix lane, if explicitly authorized, to address any confirmed editor or source-set mismatch after the diagnostics-only lane establishes whether the issue is editor-only noise or a real compile-surface problem
- A later bounded cleanup lane, if explicitly authorized, for the independent unused import and unused field warnings

Nothing in this section is currently authorized to fix.

## 6. Triage Priority Order

1. UEIA fixture Java language-level parsing mismatch
2. UEIA fixture test symbol fallout
3. Non-UEIA unused import and unused field warnings

This order is planning only. It reflects likely dependency between clusters, with Cluster B appearing to cascade from Cluster A and Cluster C appearing independent and lower-risk.

## 7. Recommended Next Move

Authorize one narrower diagnostics-only lane that captures a complete exported Problems inventory and the editor's effective Java compliance/JDK state for the `smallville` module, because the current diagnostics surface provides an exact visible total of 216 problems but only a partial and internally inconsistent detailed listing.

## 8. What This Document Does Not Authorize

- No fixes
- No code edits
- No build-file edits
- No dependency changes
- No unresolved-cell implementation
- No consuming-seam design
- No runtime / UI / API widening
- No toolchain mutation