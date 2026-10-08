# Mixed Short-Term Cleanup Boundary Checkpoint

Date: 2026-03-20

## Checkpoint Purpose

This checkpoint records that the bounded mixed short-term-family cleanup repair is landed and verified in repo reality.

It exists to freeze one runtime freshness seam only:
- when a short-term family contains both expired and future-relevant short-term entries, expired short-term siblings should no longer remain in the family indefinitely just because one future-relevant entry suppresses refresh

It does not reopen broader refresh policy, prompt work, or runtime redesign.

## Seam Scope

This checkpoint freezes the next bounded SmallVille memory seam only.

Included:
- one bounded mixed short-term-family cleanup repair in the update path
- one focused runtime test proving the repaired mixed-family cleanup behavior

Explicitly excluded:
- prompt changes
- long-term refresh-policy redesign
- reaction-logic redesign
- retrieval redesign or tuning
- scheduler redesign
- unresolved-cell work
- UI, API, workflow, or toolchain work
- startup, launcher, or scenario work
- any broader memory-system redesign

## Repo Grounding

Starting HEAD before lane:
- `e20c4acba863c34504e9c8d19520c67573a4d86f`

Ending HEAD after lane:
- `6aa712a7c155349cee130492ad1407840d73c60d`

Checkpoint commit:
- `6aa712a7c155349cee130492ad1407840d73c60d` `feat(memory): prune stale mixed short-term plans in update path`

## Exact Files Changed

Files changed in the landed seam:
- `smallville/src/main/java/io/github/nickm980/smallville/update/UpdatePlans.java`
- `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`

The lane remained bounded to the two files above.

## Exact Maven Path Used

- `C:\Program Files\apache-maven-3.9.14\bin\mvn.cmd`

## Exact Test Command Run

Focused verification command used:
- `Set-Location 'C:\SmallVille\smallville'`
- `& 'C:\Program Files\apache-maven-3.9.14\bin\mvn.cmd' "-Dtest=SimulationServiceTest" test`

## Exact Test Outcome

Focused verification passed with:
- `SimulationServiceTest`: 21 tests, 0 failures, 0 errors, 0 skipped
- `BUILD SUCCESS`

## What Defect Was Proven Before Repair

The prior mixed-family evidence proved that one future-relevant short-term entry suppressed refresh while expired short-term siblings were retained.

Specifically, the evidence showed that:
- the short-term subset could contain both one expired short-term plan and one future-relevant short-term plan
- the presence of the future-relevant short-term entry suppressed the all-expired refresh path
- the expired short-term sibling remained in the short-term family even though it was already stale
- long-term plans could remain intact while that stale mixed short-term family persisted

That evidence was recorded at:
- `e20c4acba863c34504e9c8d19520c67573a4d86f` `test(memory): cover mixed short-term family freshness boundary`

## Exact Runtime Rule Now In Force

The repair evaluates the short-term subset only and captures one stable update-cycle time.

Accepted repair behavior:
- in the mixed-family case, expired short-term entries are pruned while future-relevant short-term entries remain
- long-term plans remain intact
- no prompt-driven regeneration is required for that cleanup path
- if pruning actually changes the short-term family, `plansUpdated` is set true
- the already-landed all-expired short-term refresh path remains intact
- existing reaction-triggered full refresh behavior remains unchanged
- existing empty-plan backfill behavior remains unchanged

## What Was Verified After Repair

The focused run verified that:
- one expired short-term plan can be seeded beside one future-relevant short-term plan
- update runs without observation-trigger refresh
- the expired short-term entry is removed from the short-term family
- the future-relevant short-term entry remains
- long-term plans remain intact
- `plansUpdated` is true when the selective cleanup prunes the stale sibling
- no prompt-driven regeneration is required for that narrow cleanup path

## What Remains Unchanged By Design

Still unchanged by design after this checkpoint:
- prompt templates, including `prompts.yaml`
- `PromptBuilderTest.java`
- long-term refresh policy
- reaction logic
- retrieval behavior and retrieval redesign
- scheduler redesign
- UI, API, workflow, and toolchain behavior
- unresolved-cell work

## Drift Boundaries Preserved

The lane remained bounded as instructed:
- no Java files outside the two seam files were modified
- no prompt files were modified
- no tests outside `SimulationServiceTest.java` were modified
- no long-term policy work was opened
- no reaction-logic work was opened
- no retrieval, scheduler, UI, API, workflow, or unresolved-cell work was opened
- no additional seam was opened beyond the mixed short-term cleanup repair

## Commit SHA

- `6aa712a7c155349cee130492ad1407840d73c60d`

## Recommended Next Bounded Lane

Recommended next bounded lane:
- review-only confirmation of whether the current short-term freshness boundary should continue treating plans exactly at the captured update-cycle time as non-expired, without reopening refresh design