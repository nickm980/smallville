# Reviewed Consequence Proof Freeze Checkpoint - 2026-03-23

## Purpose

Freeze the proven reviewed-harness seam stack into a clean checkpoint so that future lanes do not drift or misattribute which seam produced the first visible world mutation via exact-match approved proposal.

## Repo state at freeze

- Branch: `main`
- HEAD before this commit: `b8538cd4db5d8ac66b03e7a1fdbf82c6ead14c3f`
- Date: 2026-03-23

## What this seam stack proves

One `add_location` proposal now flows through the full reviewed harness and produces a visible, verified world mutation:

- `approval_status=matched_and_applied`
- `approved_count=1`
- `applied_count=1`
- `world_state_delta_count=1`
- Added location: `Garden: South Bed: Raised Planter` (parent=`Garden: South Bed`, state=`empty`)

## Exact proof bundle

`runs/20260323-011257-two-house-garden-v1-reviewed-approved-live-target-first-consequence/proposal_review.md`

### Key proof fields from that bundle

```
approval_status: matched_and_applied
approved_count: 1
applied_count: 1
world_state_delta_status: non_zero
world_state_delta_count: 1
application_evidence_observed: true
structural_status: structurally_complete
semantic_status: semantically_degraded
```

## Supporting reviewed bundle (pre-approval iteration)

`runs/20260323-000110-two-house-garden-v1-reviewed-llama3.2-3b-16k/proposal_review.md`

This bundle proved structurally complete proposal flow with queued proposals but no approval step. It confirmed the proposal normalization and semantic grounding seams were working before the live-target approval test.

## Seam-stack files included in this freeze

### Java runtime changes

| File | Purpose |
|------|---------|
| `smallville/src/main/java/.../prompts/ChatService.java` | Proposal normalization, plan temporal cleanup, temperature tuning, ignorable-line filtering, plan continuation merging |
| `smallville/src/main/java/.../prompts/dto/WorldModel.java` | Valid/invalid add_location parent grounding, object-like leaf detection |
| `smallville/src/main/java/.../prompts/dto/WorldProposalCandidate.java` | `normalizationDetail` field for proposal diagnostic tracing |
| `smallville/src/main/java/.../api/v1/SimulationService.java` | Proposal review logging (`[ProposalReview]`), structured validation failure reasons, pre-queue detail classification, answer normalization, duplicate/exists guard, diagnostic sanitization |
| `smallville/src/main/resources/prompts.yaml` | Proposal prompt grounding (valid parents, example response, explicit answer rules), plan prompt temporal discipline (no narration, no split lines, example format) |

### Test changes

| File | Purpose |
|------|---------|
| `smallville/src/test/java/.../ChatServiceTest.java` | Tests for proposal normalization, type-from-answer, blank handling, plan continuation merging, ignorable wrapper filtering |
| `smallville/src/test/java/.../SimulationServiceTest.java` | Tests for proposal review logging, validation failure classification, pre-queue detail, answer normalization, world-state-delta guard |

### Harness scripts

| File | Purpose |
|------|---------|
| `scripts/Write-ProposalReview.ps1` | Extended to emit approval_status, applied_count, temporal/activity/world-state-delta warnings, consequence notes, approval detail notes |
| `scripts/Invoke-TwoHouseReviewedRun.ps1` | New reviewed-harness orchestrator: builds, starts server, runs ticks, captures proposals, runs optional live-target approval, captures world-state delta, writes reviewed proposal_review.md |

## Files explicitly excluded (unrelated residue)

| File | Status | Reason |
|------|--------|--------|
| `docs/contracts/smallville-memory-triplet-schema-v1.md` | Staged (A) | Memory-triplet contract draft — not part of this seam |
| `docs/contracts/smallville-reflection-and-retrieval-v1.md` | Staged (A) | Reflection/retrieval contract draft — not part of this seam |
| `output/pdf/smallville-app-summary.pdf` | Staged (A) | PDF summary residue — not part of this seam |
| `docs/design-assessment/DESIGN_ASSESSMENT_2026-03-22.md` | Untracked (??) | Design guidance doc — not part of this seam |

## Known remaining degradation or limits

1. **semantic_status: semantically_degraded** — The LLM still occasionally produces semantically repetitive or generic proposals. Normalization catches structural defects but not quality.
2. **rule_slice_status: blocked_for_rule_slice_justification** — No game-rule slice is yet wired. Proposals are accepted/rejected by structural validity only, not by game-rule logic.
3. **Duplicate proposal from second agent** — Jamie proposed an identical `Raised Planter` which was correctly dropped as `duplicate_pending_target`. This is correct behavior but reveals the LLM is not yet differentiated per-agent.
4. **Temperature tuning is static** — Proposal temperature lowered to 0.2, plan temperatures to 0.4. These are empirical and may need per-scenario adjustment later.
5. **Plan narration filtering is heuristic** — `isIgnorablePlanWrapperLine` uses string-prefix checks rather than structural classification.

## Recommended next lane

**First simple game-rule slice** tied to the proven consequence path:
- Wire a minimal rule-slice that can accept or reject a proposal based on one concrete game rule (e.g., "cannot add a location if the parent already has N children")
- This builds directly on the `queued_proposal` → `approval` path proven here
- Do not widen into reflection, memory-triplet, retrieval, or UI work

## Authority note

- This checkpoint freezes working-tree state at the point where first consequence was proven
- Repo code and proof bundles outrank prose
- The proof bundle path above is the canonical source of truth for what was demonstrated
