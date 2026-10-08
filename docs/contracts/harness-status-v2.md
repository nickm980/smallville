# Harness Status v2

## Checkpoint Status

- Status: `accepted`
- Active accepted checkpoint tag: `smallville-post-proposal-parent-fix`
- Active accepted checkpoint commit: `150e2073fdb608efb4e76ad05cdacbfdb7119222`
- Parser checkpoint status: `frozen` at `smallville-stable-loop-01`
- Historical status note: `docs/contracts/harness-status-v1.md` remains `frozen` history and is not superseded in place

## Harness Roles

- `tiny-world`: `accepted` as the canonical control harness
- `two-house-garden-v1`: `accepted` as the formal protocol harness
- Manual proposal review: `accepted` and still required
- Docker as the Smallville runtime substrate: `deferred`

## Post-Fix Reviewed Runtime Pair

- Control smoke run: `runs/20260313-194619-tiny-world-smoke-control-llama3.2-3b-16k`
- Reviewed protocol run: `runs/20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k`
- Proposal review writer path used for reviewed runtime generation: `scripts/Write-ProposalReview.ps1`

## High-Level Pre-Fix vs Post-Fix Comparison

- Pre-fix reviewed protocol run: `runs/20260313-173414-two-house-garden-v1-reviewed-llama3.2-3b-16k`
- Post-fix reviewed protocol run: `runs/20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k`

- Pre-fix status: `accepted historical evidence`
  - Pending `add_location` proposals appeared with object-like parents such as `Garden: Bench` and `Green House: Glass Table`.
- Post-fix status: `accepted current evidence`
  - Invalid object-like-parent `add_location` proposals no longer appeared in pending proposal review.
  - Valid container-parent `add_location` proposals remained visible and reviewable.
  - `proposal_review.md` is now stable for proposal counts and review notes.

## Proposal Review Output Stability

- Tiny-world post-fix smoke evidence: `accepted`
  - `runs/20260313-194619-tiny-world-smoke-control-llama3.2-3b-16k/proposal_review.md` records `proposal_count: 0` with the zero-case note.
- Two-house post-fix reviewed evidence: `accepted`
  - `runs/20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k/proposal_review.md` records `proposal_count: 2` with both review notes present.

## Evidence Gaps

- Observed gap: `accepted as a known evidence defect`
  - `runs/20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k/operator_notes.md` was missing from the reviewed bundle.
- Requirement: `accepted`
  - Future reviewed runs must include `operator_notes.md`.
- Ownership: `deferred follow-up`
  - This is an evidence completeness issue in the runtime harness layer, not a reason to reopen parser, scenario, or proposal-parent engine work.

## Guardrails

- Parser scope remains `frozen`.
- Manual proposal review remains `required`.
- `tiny-world` remains unchanged as the control harness.
- `two-house-garden-v1` remains the formal protocol harness.
- No automatic proposal approval paths are accepted.
- Future runtime harnesses must explicitly invoke `scripts/Write-ProposalReview.ps1`.
