# UEIA Primary Fix-Lane Proposal v1

## 1. Scope and Authority

This document is planning-only, evaluation-only, and non-authorizing.

It proposes the smallest safe future remediation lane that could be considered later under new explicit authority, but it does not authorize fixes, execution, code mutation, build mutation, dependency mutation, toolchain mutation, IDE mutation, or scope widening.

Frozen continuity remains unchanged: baseline branch context remains `main`, the frozen implementation anchor remains `419952b`, the explicit-only validator slice remains compile-proven and test-proven, targeted evidence remains 23 tests with 0 failures and 0 errors, the conformance vocabulary remains exactly `ALLOWED`, `ALLOWED_WITH_STOP_RULES`, and `NOT_ALLOWED`, unresolved cells remain represented as `Optional.empty()`, unresolved-cell implementation remains blocked, toolchain mutation remains blocked unless separately authorized, and the project remains in a valid stop-state unless explicit new authority changes that.

## 2. Evidence Basis

The existing frozen evidence supports one narrow proposed first lane rather than a broad attempt to address all 216 visible problems.

- `docs/contracts/ueia-problem-triage-inventory-v1.md` identifies the primary visible cluster as a UEIA fixture Java language-level parsing mismatch centered on `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtures.java`.
- `docs/contracts/ueia-problem-triage-inventory-v1.md` identifies the visible UEIA fixture test failures as a likely cascade from that primary cluster, rather than as an independent first target.
- `docs/contracts/ueia-diagnostics-capture-v1.md` records that the editor surface shows an exact aggregate total of 216 visible problems but exposes only a partial and internally inconsistent detailed inventory.
- `docs/contracts/ueia-full-diagnostics-export-and-editor-compliance-evidence-v1.md` records that the detailed visible subset is concentrated in `smallville`, that the strongest current hypothesis is an editor or language-service Java compliance mismatch against `smallville`, and that the main-source cluster is centered on `CanonicalFixtures.java`.
- `docs/contracts/ueia-full-diagnostics-export-and-editor-compliance-evidence-v1.md` records that the environment runtime and compiler are Java 17, `smallville/pom.xml` declares Java 17, and the visible diagnostics nevertheless report Java-1.8-style restrictions for `record` and `var`.
- `docs/contracts/ueia-java-toolchain-audit-v1.md` records that `smallville`, `java-client`, and `examples/java-example` are separate Maven surfaces and that build-surface mutation risk is wider than the current evidence needs.
- `docs/contracts/ueia-unresolved-cell-strategy-evaluation-v1.md` records that unresolved-cell implementation, consuming-seam design, and vocabulary expansion remain blocked and must stay outside any first remediation lane.

Conservative proposal basis: the safest first remediation target is the narrowest primary cluster with the strongest evidence and the smallest plausible file surface.

## 3. Proposed Primary Target Cluster

The proposed first remediation lane should target only the UEIA fixture main-source language-level mismatch centered on `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtures.java`.

This is the narrowest cluster with the strongest current evidence because:

- it is the primary visible cluster rather than a cascade
- it is the file where the visible diagnostics explicitly report Java-1.8-style rejection of `record`
- it is the file where visible downstream missing-type errors originate in the exported subset
- it is narrower than a generic `smallville` lane and much narrower than a repo-wide `216 problems` lane

## 4. Proposed Lane Boundary

Files and modules proposed inside scope for a future authorized implementation lane:

- `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtures.java`
- `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtureSectionSpanIndexTest.java` only if later authority explicitly allows direct adjustment of test expectations that are tightly coupled to `CanonicalFixtures.java`
- `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtureTextLoaderTest.java` only if later authority explicitly allows direct adjustment of test expectations that are tightly coupled to `CanonicalFixtures.java`
- `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixturesTest.java` only if later authority explicitly allows direct adjustment of test expectations that are tightly coupled to `CanonicalFixtures.java`

Files and modules proposed outside scope:

- all other files under `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture/`
- all other files under `smallville/src/test/java/io/github/nickm980/smallville/ueia/fixture/`
- all non-UEIA files in `smallville`, including `api/v1`, runtime, memory, entities, and application entrypoint files associated with the independent unused-warning cluster
- `smallville/pom.xml`
- `java-client/pom.xml`
- `examples/java-example/pom.xml`
- `jitpack.yml`
- all files under `java-client`
- all files under `examples/java-example`
- all workspace, editor, language-service, or IDE configuration files

Kinds of changes that would be in scope later only if new explicit authority were granted:

- source-level edits inside `CanonicalFixtures.java` that are strictly limited to the primary UEIA fixture language-level mismatch and its directly coupled symbol surface
- test-level edits in the three named UEIA tests only if they are strictly required to keep those tests aligned with an authorized change inside `CanonicalFixtures.java`
- validation-limited evidence capture needed to determine whether the primary cluster and its direct cascades actually clear within that bounded file set

Kinds of changes that remain out of scope even then:

- any edit to Maven build files, dependencies, plugins, repositories, wrappers, or CI
- any toolchain mutation or JDK selection change
- any IDE or workspace configuration mutation
- any unresolved-cell implementation, consuming-seam design, or vocabulary change
- any runtime, workflow, routing, approval, UI, API, or cross-module work
- any broad cleanup of unrelated warnings or unrelated diagnostics outside the named files

## 5. Why This Is The Smallest Safe Lane

This boundary is smaller and safer than a broad `fix 216 problems` lane because the evidence does not support a stable full inventory of all 216 problems. The current evidence supports only an exact total count, a partial detailed export, and a strong primary-cluster hypothesis centered on one UEIA fixture file.

Targeting `CanonicalFixtures.java` first is safer than targeting the whole `smallville` module because the visible test-surface failures appear to cascade from that file. Including only the directly coupled tests as conditional scope preserves a narrow lane while still allowing later validation of whether the apparent cascade clears.

This boundary is also safer than any build-file, dependency, toolchain, or IDE-oriented lane because the existing frozen evidence points to a likely editor or language-service compliance mismatch but does not yet justify widening into repo-wide build or environment mutation.

## 6. Candidate Acceptance Criteria

These are proposal criteria only. They are not active instructions.

- A future lane changes only the files explicitly named as inside scope.
- No build-file, dependency, toolchain, IDE, or workspace configuration files are changed.
- The primary visible diagnostics in `CanonicalFixtures.java` that report Java-1.8-style rejection of `record` and the associated missing `FixtureRef` or `FixtureDefinition` symbols are no longer present in the future validation surface used by that lane.
- The directly coupled visible cascade diagnostics in `CanonicalFixtureSectionSpanIndexTest.java` and `CanonicalFixtureTextLoaderTest.java` are either cleared or demonstrably reduced as a consequence of the scoped primary change.
- `CanonicalFixturesTest.java`, if included in the later lane at all, remains limited to direct alignment with `CanonicalFixtures.java` and does not widen the lane.
- The explicit-only validator slice remains unchanged in behavior unless a later authority explicitly says otherwise.
- The frozen conformance vocabulary remains exactly `ALLOWED`, `ALLOWED_WITH_STOP_RULES`, and `NOT_ALLOWED`.
- Unresolved cells remain represented as `Optional.empty()` and remain unresolved.
- Any later validation package preserves or reproves the existing targeted evidence of 23 tests with 0 failures and 0 errors for the explicit-only validator slice.

## 7. Risks and Ambiguities

Evidence gaps:

- The detailed diagnostics surface remains partial and internally inconsistent, so the unseen remainder of the 216 visible problems is not fully attributable.
- The editor or language-service effective compliance setting for `smallville` is still not directly exposed in the frozen evidence.
- The current evidence strongly suggests an editor or language-service mismatch, but it does not conclusively prove whether the primary symptom can be resolved wholly within source files rather than in external project-model state.

Execution risks for any future lane:

- A change limited to `CanonicalFixtures.java` may not be sufficient if the actual root cause is entirely external to source.
- The apparently cascading test diagnostics may clear automatically after the primary issue is addressed, or they may reveal a second narrower issue that is not yet visible because of the current editor inconsistency.
- A lane that starts narrow could still be pressured to widen into build files, IDE state, or additional UEIA files if the initial hypothesis is wrong; that widening should be treated as a stop condition, not as implied permission.

Exact new authority required before implementation could begin:

- explicit authority to edit `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtures.java`
- if desired, explicit authority to edit only the three named directly coupled UEIA tests and no other tests
- explicit authority for the exact validation surface that will be used to judge the lane
- explicit confirmation that `smallville/pom.xml`, other build files, toolchain settings, IDE settings, unresolved-cell behavior, and consuming seams remain outside scope unless separately reauthorized

## 8. Fallback Option

If the primary lane is judged not yet safe to open, the single fallback lane should be one more diagnostics-only clarification lane limited to extracting direct editor or language-service project-model and effective compliance metadata for the `smallville` module, with no code edits and no build or toolchain mutation.

## 9. Recommended Next Move

Authorize one narrow implementation lane only if a new explicit authority package is willing to permit edits to `smallville/src/main/java/io/github/nickm980/smallville/ueia/fixture/CanonicalFixtures.java` and, only if strictly necessary, the three directly coupled UEIA tests named in this proposal, while continuing to prohibit build-file, dependency, toolchain, IDE, unresolved-cell, consuming-seam, runtime, UI, and API widening.

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