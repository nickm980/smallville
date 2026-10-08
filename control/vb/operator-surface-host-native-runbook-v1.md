# Operator Surface Host-Native Runbook v1

## Operational Read

- Status: `accepted`
- Operating mode: `host-native`
- Scope: `read-only`
- Evidence roots: [`runs`](C:/SmallVille/runs) and [`baselines`](C:/SmallVille/baselines)
- Checklist source root: [`docs/evals`](C:/SmallVille/docs/evals)

This runbook is for using the accepted operator-surface v1 stack without launching or mutating Smallville runtime.

## Entry Points

### View Entry Points

- [`Get-RunBundleSummary.ps1`](C:/SmallVille/control/vb/Get-RunBundleSummary.ps1)
- [`Get-ProposalReviewView.ps1`](C:/SmallVille/control/vb/Get-ProposalReviewView.ps1)
- [`Get-ChecklistStatusView.ps1`](C:/SmallVille/control/vb/Get-ChecklistStatusView.ps1)
- [`Get-RunComparisonView.ps1`](C:/SmallVille/control/vb/Get-RunComparisonView.ps1)

### Export Entry Points

- [`Export-RunBundleSummaryCsv.ps1`](C:/SmallVille/control/vb/Export-RunBundleSummaryCsv.ps1)
- [`Export-ProposalSummaryCsv.ps1`](C:/SmallVille/control/vb/Export-ProposalSummaryCsv.ps1)
- [`Export-ChecklistOutcomeCsv.ps1`](C:/SmallVille/control/vb/Export-ChecklistOutcomeCsv.ps1)
- [`Export-RunComparisonSummaryCsv.ps1`](C:/SmallVille/control/vb/Export-RunComparisonSummaryCsv.ps1)

## Proof Scripts Available

- [`Test-OperatorSurfaceSlice1.ps1`](C:/SmallVille/control/vb/Test-OperatorSurfaceSlice1.ps1)
- [`Test-OperatorSurfaceChecklistSlice2.ps1`](C:/SmallVille/control/vb/Test-OperatorSurfaceChecklistSlice2.ps1)
- [`Test-OperatorSurfaceComparisonSlice3.ps1`](C:/SmallVille/control/vb/Test-OperatorSurfaceComparisonSlice3.ps1)
- [`Test-OperatorSurfaceExportSlice4.ps1`](C:/SmallVille/control/vb/Test-OperatorSurfaceExportSlice4.ps1)

## Expected Inputs

Expected inputs are `accepted` as:
- bundle directories under [`runs`](C:/SmallVille/runs)
- checklist source under [`docs/evals`](C:/SmallVille/docs/evals)

Typical bundle inputs include:
- `tiny-world` smoke bundles
- `two-house-garden-v1` reviewed bundles

## Output Location Rules

Output rules are `accepted` as:
- exports must write to a user-specified directory
- that directory must remain outside [`runs`](C:/SmallVille/runs)
- that directory must remain outside [`baselines`](C:/SmallVille/baselines)
- exports are read-only derivatives and must not overwrite source evidence

## Protected Roots

The following roots are protected and must not be written into by operator-surface exports:
- [`runs`](C:/SmallVille/runs)
- [`baselines`](C:/SmallVille/baselines)

## Troubleshooting Notes

### `missing_manifest`

- Meaning: bundle metadata root is incomplete
- Owner: `runtime harness`
- Operator action: surface the warning and treat the bundle as evidence-incomplete

### `missing_operator_notes`

- Meaning: reviewer-facing notes are absent
- Owner: `runtime harness`
- Operator action: surface the warning and keep the run reviewable only as incomplete evidence

### `missing_proposal_review`

- Meaning: proposal review surface is absent from the bundle
- Owner: `runtime harness`
- Operator action: warn and avoid inferring proposal state from unrelated files

### `blank_proposal_count`

- Meaning: proposal review document contains a blank numeric count
- Owner: `writer layer`
- Operator action: preserve the stored blank and surface the warning

### `proposal_count_note_mismatch`

- Meaning: proposal count and rendered note lines disagree
- Owner: `writer layer`
- Operator action: preserve both values and surface the mismatch

### `inconsistent_run_id`

- Meaning: bundle files disagree about the run identifier
- Owner: `runtime harness`
- Operator action: treat the bundle as inconsistent evidence and avoid silent reconciliation

### `missing_restart_evidence`

- Meaning: restart capture files are incomplete
- Owner: `runtime harness`
- Operator action: mark restart review as incomplete rather than inferred

## Not Approved In This Phase

The following are `not approved in this phase`:
- runtime launch from operator surface
- proposal controls
- silent repair
- Excel or executable VB behavior
- container-required workflow

## Explicit Stop Conditions

Stop if any operator-surface use pattern would:
- write into protected evidence roots
- mutate simulation state
- hide or repair missing evidence
- depend on runtime launch to function

