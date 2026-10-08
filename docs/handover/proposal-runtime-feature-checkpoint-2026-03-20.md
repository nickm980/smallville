# Proposal Runtime Feature Checkpoint

Date: 2026-03-20

## Checkpoint Purpose

This checkpoint records that the bounded proposal-runtime feature family is now proven end-to-end in repo reality.

It exists to consolidate one closed evidence sequence only:
- proposal emergence through runtime update
- proposal review and apply behavior
- action history at service level
- snapshot exposure of proposal history and pending visibility
- approve/reject endpoint contract behavior

It does not reopen design, broaden the proposal system, or start a new implementation seam.

## Feature Slice Scope

This checkpoint freezes the proposal-runtime feature family only.

Included:
- proposal emergence through `updateState`
- proposal queue visibility and review path evidence
- approve and reject behavior
- proposal action history evidence at service and snapshot level
- review endpoint contract payload stability evidence

Explicitly excluded:
- equality-with-now work
- planning freshness work
- prompt redesign
- runtime redesign beyond the already-proven proposal family
- API redesign beyond the already-proven contract surface
- UI, workflow, toolchain, or scenario work
- any unrelated untracked files

## Starting Anchor Before Proposal-Runtime Evidence Sequence

Starting HEAD before the proposal-runtime evidence sequence:
- `bd6bb844db39d5810528bb3a94a8f8bfa8adce4a`

This is the repo anchor immediately before the first proposal-runtime evidence commit.

## Ending HEAD After Latest Proposal Endpoint Contract Evidence

Ending HEAD after the latest proposal endpoint contract evidence:
- `12b86656e8cc776c00b3c6af71aca0b5d9f772b0`

## Evidence Ladder With Commit SHAs and Purpose

The consolidated proof ladder is:

1. `30653218641faf977ef42909c52c1b3a2921c60a` `test(proposals): verify runtime queue path`
   - proposal emergence through `updateState` is proven
   - queue visibility and pending proposal population are proven

2. `574ee9b9630ca9107f90d6e72ac221456b7e7f0c` `test(proposals): verify review and apply path`
   - approve and reject behavior is proven
   - review/apply semantics are proven

3. `0503d76673dfc808a8e376c492788e00860a1b7a` `test(proposals): verify action log history`
   - proposal action history is proven at service level
   - action ordering and proposal review history are proven

4. `77b105dafb57c7a9a80d175d5246ee1e1d272e50` `test(proposals): verify snapshot action history`
   - proposal action history is proven at snapshot/API level
   - pending proposal visibility after review is proven through `/world`

5. `12b86656e8cc776c00b3c6af71aca0b5d9f772b0` `test(proposals): verify review endpoint contracts`
   - approve and reject endpoint contract payloads are proven stable and coherent
   - returned proposal payloads are proven coherent with subsequent `/world` snapshot state

## Exact Files Touched Across the Evidence Sequence

Files touched across the five-commit proposal-runtime evidence sequence:
- `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/EndpointsTest.java`

Per commit:
- `30653218641faf977ef42909c52c1b3a2921c60a`: `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`
- `574ee9b9630ca9107f90d6e72ac221456b7e7f0c`: `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`
- `0503d76673dfc808a8e376c492788e00860a1b7a`: `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`
- `77b105dafb57c7a9a80d175d5246ee1e1d272e50`: `smallville/src/test/java/io/github/nickm980/smallville/EndpointsTest.java`
- `12b86656e8cc776c00b3c6af71aca0b5d9f772b0`: `smallville/src/test/java/io/github/nickm980/smallville/EndpointsTest.java`

No production repair was required anywhere in the queueing, review/apply, history, or endpoint-contract sequence.
No production Java file was changed in this evidence ladder.

## Exact Maven Commands Used Across the Sequence

Exact Maven path used in the proposal-runtime evidence sequence:
- `C:\Program Files\apache-maven-3.9.14\bin\mvn.cmd`

Focused verification commands used across the sequence:
- `Set-Location 'C:\SmallVille\smallville'`
- `& 'C:\Program Files\apache-maven-3.9.14\bin\mvn.cmd' "-Dtest=SimulationServiceTest" test`
- `& 'C:\Program Files\apache-maven-3.9.14\bin\mvn.cmd' "-Dtest=EndpointsTest" test`

Command use by evidence tier:
- service-level proposal runtime evidence used `SimulationServiceTest`
- snapshot and endpoint-contract evidence used `EndpointsTest`

## Exact Test Outcomes Across the Sequence

Focused outcomes recorded across the proposal-runtime evidence sequence:
- `30653218641faf977ef42909c52c1b3a2921c60a`: `SimulationServiceTest` suite at that commit contained 26 tests and the lane landed green
- `574ee9b9630ca9107f90d6e72ac221456b7e7f0c`: `SimulationServiceTest` suite at that commit contained 30 tests and the lane landed green
- `0503d76673dfc808a8e376c492788e00860a1b7a`: `SimulationServiceTest` focused verification passed with 32 tests, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`
- `77b105dafb57c7a9a80d175d5246ee1e1d272e50`: `EndpointsTest` focused verification shape was 14 tests, 0 failures, 0 errors, 1 skipped, `BUILD SUCCESS`
- `12b86656e8cc776c00b3c6af71aca0b5d9f772b0`: `EndpointsTest` focused verification passed with 14 tests, 0 failures, 0 errors, 1 skipped, `BUILD SUCCESS`

Current repo verification of the closed feature family also confirms:
- `SimulationServiceTest`: 32 tests, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`
- `EndpointsTest`: 14 tests, 0 failures, 0 errors, 1 skipped, `BUILD SUCCESS`

## What Behavior Is Now Proven

The proposal-runtime feature family is now proven end-to-end.

Specifically proven:
- proposal emergence through `updateState` is proven
- pending proposal queue visibility is proven
- approve behavior is proven
- reject behavior is proven
- review/apply behavior is proven
- action history is proven at service level
- action history is proven at snapshot/API level
- endpoint contract payloads are proven stable and coherent
- returned approve and reject payloads line up with subsequent `/world` snapshot state
- operator-readable proposal fields remain stable across the review endpoint contract surface

## What Production Defects Were Or Were Not Found

The proposal family required no production repair in the queueing, review/apply, history, or API contract sequence.

Explicit outcome:
- no production defect was found in the proposal seam family during the evidence ladder
- no controller repair was required
- no service repair was required
- no entity or DTO repair was required
- the sequence closed as evidence-first verification, not runtime defect correction

## What Remains Intentionally Parked

Still intentionally parked:
- equality-with-now remains parked
- planning freshness seam family remains closed and parked
- broader proposal-system redesign remains out of scope
- prompt changes remain out of scope
- non-proposal runtime redesign remains out of scope

## Drift Boundaries Preserved

The proposal-runtime evidence ladder preserved the required drift boundaries:
- unrelated untracked files remained out of scope throughout
- no production repair was required
- no prompt work was reopened
- no planning freshness seam was reopened
- equality-with-now was not reopened
- no architecture redesign was opened
- no documentation was introduced during the test evidence sequence itself
- the current documentation lane changes only this one markdown file

Current unrelated untracked files that remained out of scope:
- `docs/contracts/smallville-memory-triplet-schema-v1.md`
- `docs/contracts/smallville-reflection-and-retrieval-v1.md`
- `twohousev.1`

## Recommended Next Bounded Lane

Recommended next bounded lane:
- review-only confirmation of whether invalid proposal ID error behavior should be explicitly frozen in one narrow endpoint-contract checkpoint or left implicit under the existing global exception contract, without reopening proposal runtime implementation

## Follow-On Invalid ID Corrective Seam

Follow-on bounded corrective seam completed after the review-only lane found that invalid proposal IDs were not actually normalized by live server wiring.

Reason this seam was required:
- `approve` and `reject` proposal endpoints passed the proposal ID directly into service lookup
- missing proposal IDs correctly raised `SmallvilleException("Proposal not found")` in service code
- shared `ExceptionRoutes` already defined a `SmallvilleException -> 400` contract family
- but `ExceptionRoutes` was not registered in `SmallvilleServer` startup
- live invalid proposal review requests therefore returned `500` with body `Server Error` instead of an intentional error contract

Narrow corrective choice taken:
- endpoint-local normalization for `POST /world/proposals/{id}/approve`
- endpoint-local normalization for `POST /world/proposals/{id}/reject`
- global `ExceptionRoutes` registration was not taken in this seam because it would widen unrelated endpoint behavior beyond the proposal invalid-ID boundary

Corrected live behavior now frozen:
- invalid approve proposal ID returns HTTP `400`
- invalid reject proposal ID returns HTTP `400`
- response body is `{"error":"Proposal not found"}`

Direct lock added:
- `EndpointsTest` now directly verifies invalid approve proposal ID behavior
- `EndpointsTest` now directly verifies invalid reject proposal ID behavior

This follow-on seam stayed narrow:
- no proposal-family redesign was opened
- no global exception-routing migration was opened
- no unrelated endpoint contracts were changed intentionally
