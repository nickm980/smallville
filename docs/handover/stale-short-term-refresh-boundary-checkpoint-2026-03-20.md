# Stale Short-Term Refresh Boundary Checkpoint

Date: 2026-03-20

## Checkpoint Purpose

This checkpoint records that the bounded stale short-term refresh repair is landed and verified in repo reality.

It exists to freeze one runtime freshness seam only:
- already-expired non-empty short-term plans should no longer persist indefinitely across `UpdatePlans` cycles when no observation-trigger refresh fires

It does not reopen broader refresh policy, prompt-boundary work, or runtime redesign.

## Seam Scope

This checkpoint freezes the next bounded SmallVille memory seam only.

Included:
- one bounded stale short-term refresh repair in the update path
- one focused runtime test proving the repaired short-term refresh behavior

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
- `b962e1f57cfd8962b351fd3fd044331fa91f8d50`

Ending HEAD after lane:
- `d09147a8eb34ff0797a457473cec3bc92c3a5bb8`

Checkpoint commit:
- `d09147a8eb34ff0797a457473cec3bc92c3a5bb8` `feat(memory): refresh stale short-term plans in update path`

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
- `SimulationServiceTest`: 20 tests, 0 failures, 0 errors, 0 skipped
- `BUILD SUCCESS`

## What Defect Was Proven Before Repair

The prior evidence commit proved that expired non-empty short-term plans could persist without refresh.

Specifically, the evidence showed that:
- an already-expired short-term plan could survive repeated `UpdatePlans` cycles unchanged
- no observation-trigger refresh was required for that persistence to happen
- future long-term plans could still be present while the stale short-term family remained untouched

That evidence was recorded at:
- `b962e1f57cfd8962b351fd3fd044331fa91f8d50` `test(memory): cover short-term plan freshness persistence boundary`

## Exact Runtime Rule Now In Force

The repair captures one stable update-cycle time and evaluates the short-term subset only.

Accepted repair behavior:
- if the short-term subset is non-empty and every short-term plan is before the captured update-cycle time, the short-term family is treated as stale
- if all short-term plans are expired, only the short-term family is refreshed
- future long-term plans remain intact
- existing reaction-triggered full refresh behavior remains unchanged
- existing empty-plan backfill behavior remains unchanged

## What Was Verified After Repair

The focused run verified that:
- an already-expired short-term plan no longer remains untouched after `UpdatePlans` runs without observation-trigger refresh
- the stale short-term family is replaced through short-term-only regeneration
- `plansUpdated` reflects the short-term refresh appropriately
- future long-term plans remain intact

## What Remains Unchanged By Design

Still unchanged by design after this checkpoint:
- prompt templates, including `prompts.yaml`
- prompt-boundary behavior for long-term and short-term continuity seams
- long-term refresh policy
- reaction logic
- retrieval behavior and retrieval redesign
- scheduler redesign
- UI, API, workflow, and toolchain behavior
- unresolved-cell work

## Drift Boundaries Preserved

The lane remained bounded as instructed:
- no prompt files were modified
- no `PromptBuilderTest.java` changes were made
- no long-term policy work was opened
- no reaction-logic work was opened
- no retrieval, scheduler, UI, API, workflow, or unresolved-cell work was opened
- no docs were created during the implementation lane
- no additional seam was opened beyond the stale short-term refresh repair

## Commit SHA

- `d09147a8eb34ff0797a457473cec3bc92c3a5bb8`

## Recommended Next Bounded Lane

Recommended next bounded lane:
- review whether mixed short-term families with both expired and future-relevant entries need a matching bounded evidence test, without reopening prompt work or broad refresh-policy design