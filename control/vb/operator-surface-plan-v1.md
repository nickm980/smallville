# Read-Only Operator Surface Plan v1

## Purpose

This plan defines the first read-only operator tools above the engine for the promoted harness family.

Control harness:
- `tiny-world`

Formal protocol harness:
- `two-house-garden-v1`

Operator tools in this phase must remain read-only. They may summarize, compare, filter, and export run evidence, but they must not mutate simulation state, proposal state, prompts, or engine configuration.

## Surface 1: Run-Bundle Summary View

Primary goal:
- let an operator open one run bundle and immediately understand what ran, whether startup passed, how many ticks were captured, and whether shutdown and restart checks passed

Minimum data sources:
- `manifest.yaml`
- `operator_notes.md`
- `proposal_review.md`
- presence/absence of required capture files

Minimum view elements:
- run id
- scenario name
- model
- launch mode
- port
- startup result
- tick count captured
- restart captured yes/no
- shutdown result
- proposal count summary
- quick links to cold-start, tick, and restart artifacts

## Surface 2: Checklist Status View

Primary goal:
- show the review status against the documented checklist without forcing operators to read raw JSON first

Minimum data sources:
- `operator_notes.md`
- `docs/evals/two-house-garden-v1-review-checklist.md`
- selected world/state captures

Minimum status categories:
- plan integrity
- location validity
- task coherence
- water/garden relevance
- bounded proposals
- action-log readability
- restart reproducibility

First version rule:
- status may be manual or operator-entered
- do not invent automated pass/fail scoring that is not already present in the bundle

## Surface 3: Proposal Review View

Primary goal:
- make pending proposal evidence easy to inspect without changing proposal state

Minimum data sources:
- `proposal_review.md`
- `endpoint_world_proposals_cold.json`
- `endpoint_world_tick_*.json`

Minimum view elements:
- proposal count
- agent
- type
- parent location
- name
- proposed state
- reason
- tick observed
- repeated proposal detection by normalized text match

Read-only boundary:
- no approve/reject controls in this phase

## Surface 4: Run Comparison View

Primary goal:
- compare two runs of the same harness family without engine knowledge

Suggested first comparisons:
- cold-start world structure
- agent names and start locations
- pending proposal patterns
- tick-by-tick action summaries
- restart structural match

Minimum use cases:
- `tiny-world` control smoke vs prior `tiny-world` control smoke
- `two-house-garden-v1` reviewed run vs later reviewed run
- control harness vs protocol harness for high-level contrast only

## Surface 5: CSV / Excel Export Support

Primary goal:
- export human-reviewable summaries into spreadsheet-friendly form

Priority export tables:
- run summary table
- proposal summary table
- tick summary table
- action log table
- checklist outcome table

First version rule:
- export normalized summaries from run-bundle artifacts
- do not transform or reinterpret source evidence beyond stable column mapping

## Implementation Shape

Recommended first implementation order:

1. bundle summary reader
2. proposal review reader
3. checklist status panel
4. run comparison panel
5. CSV / Excel export helpers

Recommended source-of-truth rule:
- always read from the run bundle on disk
- never synthesize missing evidence as if it were present

## Step A `proposal_count` Blank Quirk

Ownership:
- this belongs to **run-bundle writer logic**

Reason:
- the saved `proposal_review.md` source artifact from the accepted Step A smoke run already contains a blank `proposal_count` field
- because the source artifact is wrong at write time, operator surfaces should not be treated as the fix location

Operator-surface handling rule:
- display the source value as-is
- add a warning when proposal notes exist but the numeric count is blank
- do not silently repair the stored artifact in the UI

Future correction boundary:
- fix the run-bundle writer or proposal-review generation logic that emits the blank field
- do not classify this as an engine issue

## Guardrails

- read-only only
- no proposal mutation
- no simulation control surface beyond opening and reviewing approved bundles
- no parser or engine coupling
- no dashboard replacement in this phase
