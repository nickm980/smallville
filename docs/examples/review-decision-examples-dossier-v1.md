# Review Decision Examples Dossier v1

## Purpose

This note provides specification-only review decision examples for the UEIA single-dossier bootstrap.

All examples stop at the review boundary and do not authorize execution.

## Example 1: Archive Record Approval

- `decision_id`: `rev-record-water-mill-features-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `subject_type`: `archive-record`
- `review_scope`: `record`
- `subject_id`: `rec-built-features-water-mill`
- `gate_level`: `L1`
- `decision_status`: `approved-for-packet`
- `reviewer_role`: `Review Analyst`
- `approved_for_next_gate`: `true`
- `reviewed_at`: `2026-03-14T00:00:00Z example`
- `rationale`: `Visible built features are clearly separated from inference, and provenance is preserved to specific Water Mill images and manifest entries.`
- `required_changes`:
  - `Keep tower-core language marked inferred.`
  - `Keep service-deck use claims below settled fact level.`
- `bias_review_materials`:
  - `Visible material form is documented, but hidden internal material assumptions remain unsupported.`
- `bias_review_people`:
  - `Human access and maintenance implications are suggested but not overclaimed.`
- `bias_review_power`:
  - `No unsupported control or execution authority is implied by the record.`
- `bias_review_place`:
  - `The record stays tied to image-grounded spatial relations rather than world-state authority.`
- Rationale detail:
  - visible built features are clearly separated from inference
  - provenance is preserved to specific Water Mill images and manifest entries
- Required changes detail:
  - keep tower-core language marked `inferred`
  - keep service-deck use claims below settled fact level

## Example 2: Packet Approval

- `decision_id`: `rev-packet-bootstrap-water-mill-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `subject_type`: `derived-scenario-packet`
- `review_scope`: `packet`
- `subject_id`: `pkt-dossier-bootstrap-water-mill-v1`
- `gate_level`: `L3`
- `decision_status`: `open-question`
- `reviewer_role`: `Review Analyst`
- `approved_for_next_gate`: `false`
- `reviewed_at`: `2026-03-14T00:00:00Z example`
- `rationale`: `Packet boundary discipline is sound, but off-grid logic and waste-interaction sections still require tighter distinction between inference and approved-for-packet content.`
- `required_changes`:
  - `Preserve packet-is-not-world-state wording in the packet header.`
  - `Move unsupported composting infrastructure claims back to open-question status.`
- `bias_review_materials`:
  - `Material features should not be treated as functioning infrastructure without stronger evidence.`
- `bias_review_people`:
  - `Maintenance and hidden-labor interpretations must stay as prompts, not settled truths.`
- `bias_review_power`:
  - `Packet language must not acquire scene or world mutation authority.`
- `bias_review_place`:
  - `Spatial hints remain descriptive and may not become geometry or map layers.`
- Rationale detail:
  - packet boundary is sound
  - off-grid logic and waste-interaction sections still require tighter distinction between inference and approved packet candidates
- Required changes detail:
  - preserve packet-is-not-world-state wording in the header
  - move any unsupported composting infrastructure claim back to `open-question`

## Example 3: Brief Approval

- `decision_id`: `rev-brief-review-analyst-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `subject_type`: `agent-brief`
- `review_scope`: `brief`
- `subject_id`: `brief-review-analyst-bootstrap-v1`
- `gate_level`: `L3`
- `decision_status`: `approved-for-packet`
- `reviewer_role`: `Executive Controller`
- `approved_for_next_gate`: `true`
- `reviewed_at`: `2026-03-14T00:00:00Z example`
- `rationale`: `The brief keeps hidden-labor and human-interaction questions as inquiry rather than truth claim, and no execution language appears.`
- `required_changes`:
  - `Keep regional and cultural variation prompts explicitly marked as research prompts.`
- `bias_review_materials`:
  - `Material observations stay secondary to evidence discipline.`
- `bias_review_people`:
  - `Human-interaction framing is present without claiming universal behavior.`
- `bias_review_power`:
  - `The brief does not claim approval or execution authority.`
- `bias_review_place`:
  - `Place-based reasoning stays interpretive and local to the dossier.`
- Rationale detail:
  - the brief keeps hidden-labor and human-interaction questions as inquiry rather than truth claim
  - no execution language appears
- Required changes detail:
  - keep regional and cultural variation prompts explicitly marked as research prompts

## Example 4: Proposal-Pack Review Boundary

- `decision_id`: `rev-proposal-pack-boundary-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `subject_type`: `proposal-pack`
- `review_scope`: `proposal-pack`
- `subject_id`: `proposal-pack-bootstrap-v1`
- `gate_level`: `L4`
- `decision_status`: `deferred`
- `reviewer_role`: `Executive Controller`
- `approved_for_next_gate`: `false`
- `reviewed_at`: `2026-03-14T00:00:00Z example`
- `rationale`: `The proposal pack is specification-rich but Phase 1 and bootstrap specification work do not authorize implementation, runtime, geometry, or world-building authority.`
- `required_changes`:
  - `Stop at specification output.`
  - `Do not convert packet examples into implementation tasks.`
- `bias_review_materials`:
  - `Material description must not be inflated into real-world authority.`
- `bias_review_people`:
  - `Human labor and maintenance prompts remain review questions, not settled operational truths.`
- `bias_review_power`:
  - `No automatic approval or execution handoff is permitted.`
- `bias_review_place`:
  - `Dossier place logic must remain descriptive and non-executing.`
- Rationale detail:
  - the proposal pack is specification-rich but Phase 1 and bootstrap specification work do not authorize implementation
  - any move toward runtime, geometry, or world-building authority would breach the dossier boundary
- Required changes detail:
  - stop at specification output
  - do not convert packet examples into implementation tasks
