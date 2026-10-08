# Promoted Review Decision v1

## Selected Canonical Specimen

- `decision_id`: `rev-brief-review-analyst-v1`
- Source family: [`review-decision-examples-dossier-v1.md`](C:/SmallVille/docs/examples/review-decision-examples-dossier-v1.md)

## Why This Is The Best First Canonical Target

This review decision is the best first canonical review specimen because it closes the cleanest end of the promoted chain:
- it evaluates the promoted brief directly
- it preserves the approval boundary without drifting into execution
- it keeps interpretive richness while remaining concise and schema-complete

It is preferable to the packet review decision as a first canonical target because the brief approval example is cleaner, less structurally noisy, and easier to use as a first fixture while still preserving the packet boundary through the brief's packet linkage.

## Promotion Rationale

- Provenance clarity: references the promoted brief and keeps dossier lineage explicit.
- Schema alignment quality: clean coverage of `decision_id`, `review_scope`, `gate_level`, `reviewer_role`, `required_changes`, and all four bias-review fields.
- Interpretive richness without overclaiming: preserves hidden-labor and human-interaction framing as inquiry only.
- Tooling/test usefulness: strong first fixture for review-decision serialization, gate-level checks, and approval-boundary assertions.
- Single-dossier discipline: stays entirely inside `dossier-bootstrap-v1`.
- Non-executing boundary strength: approves for next review gate only and does not imply execution.

## Canonical Fields

- `decision_id`: `rev-brief-review-analyst-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `subject_type`: `agent-brief`
- `subject_id`: `brief-review-analyst-bootstrap-v1`
- `review_scope`: `brief`
- `gate_level`: `L3`
- `decision_status`: `approved-for-packet`
- `reviewer_role`: `Executive Controller`
- `approved_for_next_gate`: `true`

## Canonical Use Boundary

This promoted review decision may serve as:
- the first canonical review-decision fixture
- the approval-boundary specimen for future tests
- the terminal review node in the first canonical chain

It must not be treated as:
- runtime authorization
- execution approval
- implementation signal
