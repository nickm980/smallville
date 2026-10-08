# Agent Brief Examples Dossier v1

## Brief Boundary

These are specification-only brief examples.

They are:
- proposal-only
- non-executing
- tied to one constrained dossier
- subordinate to the rule that packet content is not runtime world state

Shared packet context used below:
- `dossier_id`: `dossier-bootstrap-v1`
- `packet_id`: `pkt-dossier-bootstrap-water-mill-v1`

## Archivist Brief Example

- `brief_id`: `brief-archivist-bootstrap-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `packet_id`: `pkt-dossier-bootstrap-water-mill-v1`
- `role_name`: `Archivist`
- `agent_name_or_slot`: `archivist-slot-1`
- `purpose`: `Preserve provenance across reviewed-run evidence, control context, Water Mill manifest files, and raw image observations.`
- `approved_facts`:
  - `fact_id`: `bf-archivist-pack-inventory`
    - `fact_text`: `The Water Mill pack contains nine primary JPEGs plus manifest and note files.`
    - `fact_status`: `observed`
    - `source_record_id`: `rec-water-mill-design-asset-pack`
  - `fact_id`: `bf-archivist-reviewed-anchor`
    - `fact_text`: `The reviewed run remains the primary operational anchor.`
    - `fact_status`: `approved-for-packet`
    - `source_record_id`: `rec-reviewed-run-bundle-summary`
- `task_focus`:
  - `structure classification`
  - `chronology reconciliation`
  - `map-to-space alignment`
- `forbidden_assumptions`:
  - `Do not treat design assets as observed real-world truth.`
  - `Do not merge extra dossiers.`
- `review_questions`:
  - `Which observations come directly from images versus notes or inference?`
  - `Which source paths need to stay explicit when records are promoted?`
- `brief_status`: `open-question`
- `created_at`: `2026-03-14T00:00:00Z example`
- `supporting_refs`:
  - `ref_type`: `record`
    - `ref_id`: `rec-water-mill-design-asset-pack`
    - `ref_note`: `Design-asset provenance anchor.`
  - `ref_type`: `record`
    - `ref_id`: `rec-reviewed-run-bundle-summary`
    - `ref_note`: `Operational anchor provenance.`

## Data Normalizer Brief Example

- `brief_id`: `brief-data-normalizer-bootstrap-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `packet_id`: `pkt-dossier-bootstrap-water-mill-v1`
- `role_name`: `Data Normalizer`
- `agent_name_or_slot`: `data-normalizer-slot-1`
- `purpose`: `Transform dossier inputs into archive-record candidates with strict fact-status discipline.`
- `approved_facts`:
  - `fact_id`: `bf-normalizer-visible-components`
    - `fact_text`: `Visible wheel, deck, stairs, railings, tower cavity, wiring, and solar trial-fit are image-grounded observations.`
    - `fact_status`: `observed`
    - `source_record_id`: `rec-water-mill-design-asset-pack`
  - `fact_id`: `bf-normalizer-waste-boundary`
    - `fact_text`: `Waste and composting topics remain research prompts rather than evidenced hardware.`
    - `fact_status`: `approved-for-packet`
    - `source_record_id`: `rec-waste-and-reclassification-touchpoints`
- `task_focus`:
  - `flow interpretation`
  - `maintenance dependency analysis`
  - `waste interaction tracing`
  - `compost and reclassification reasoning`
- `forbidden_assumptions`:
  - `Do not promote inferred structures to observed.`
  - `Do not collapse open questions into approved packet facts.`
- `review_questions`:
  - `Which facts need to remain inferred in the first canonical archive set?`
  - `Where should waste or compost prompts remain below settled-fact level?`
- `brief_status`: `open-question`
- `created_at`: `2026-03-14T00:00:00Z example`
- `supporting_refs`:
  - `ref_type`: `record`
    - `ref_id`: `rec-water-system-elements-water-mill`
    - `ref_note`: `Water-system element record for flow interpretation.`
  - `ref_type`: `record`
    - `ref_id`: `rec-waste-and-reclassification-touchpoints`
    - `ref_note`: `Waste-boundary anchor.`

## Scenario Packet Compiler Brief Example

- `brief_id`: `brief-packet-compiler-bootstrap-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `packet_id`: `pkt-dossier-bootstrap-water-mill-v1`
- `role_name`: `Scenario Packet Compiler`
- `agent_name_or_slot`: `packet-compiler-slot-1`
- `purpose`: `Prepare interpretive packet sections without granting world-state authority.`
- `approved_facts`:
  - `fact_id`: `bf-packet-vocabulary`
    - `fact_text`: `Built-feature and water-system vocabulary can be proposed from visible components and manifest metadata.`
    - `fact_status`: `approved-for-packet`
    - `source_record_id`: `rec-water-mill-design-asset-pack`
  - `fact_id`: `bf-packet-control-boundary`
    - `fact_text`: `The packet must preserve primary reviewed-run status and tiny-world control-only status.`
    - `fact_status`: `approved-for-packet`
    - `source_record_id`: `rec-control-run-smoke-context`
- `task_focus`:
  - `hybrid-energy reasoning`
  - `spatial grouping`
  - `off-grid water and power logic framing`
  - `thesis question extraction`
- `forbidden_assumptions`:
  - `Do not produce geometry, meshes, map layers, or renderable asset language.`
  - `Do not treat packet content as runtime instruction.`
- `review_questions`:
  - `Which built features are safe for first packet promotion?`
  - `How should packet sections preserve packet-versus-world-state separation?`
- `brief_status`: `open-question`
- `created_at`: `2026-03-14T00:00:00Z example`
- `supporting_refs`:
  - `ref_type`: `record`
    - `ref_id`: `rec-built-features-water-mill`
    - `ref_note`: `Built-feature anchor.`
  - `ref_type`: `record`
    - `ref_id`: `rec-energy-support-features`
    - `ref_note`: `Hybrid-energy prompt anchor.`

## Review Analyst Brief Example

- `brief_id`: `brief-review-analyst-bootstrap-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `packet_id`: `pkt-dossier-bootstrap-water-mill-v1`
- `role_name`: `Review Analyst`
- `agent_name_or_slot`: `review-analyst-slot-1`
- `purpose`: `Test whether packet-ready claims stay within provenance and fact-status discipline.`
- `approved_facts`:
  - `fact_id`: `bf-review-pack-rule`
    - `fact_text`: `Water Mill notes explicitly frame the pack as design-context analysis only.`
    - `fact_status`: `observed`
    - `source_record_id`: `rec-water-mill-design-asset-pack`
  - `fact_id`: `bf-review-packet-boundary`
    - `fact_text`: `Packet content must remain interpretive and not become runtime world state.`
    - `fact_status`: `approved-for-packet`
    - `source_record_id`: `rec-reviewed-run-bundle-summary`
- `task_focus`:
  - `hidden-labor and human-interaction question framing`
  - `structure confidence review`
  - `chronology reconciliation`
  - `maintenance and waste-handling boundary review`
- `forbidden_assumptions`:
  - `Do not authorize automatic approval.`
  - `Do not accept regional or cultural variation prompts as truth claims without evidence.`
- `review_questions`:
  - `Which prompts remain research prompts only?`
  - `Where is provenance too weak for first canonical specimen promotion?`
- `brief_status`: `approved-for-packet`
- `created_at`: `2026-03-14T00:00:00Z example`
- `supporting_refs`:
  - `ref_type`: `record`
    - `ref_id`: `rec-waste-and-reclassification-touchpoints`
    - `ref_note`: `Hidden-labor and waste-boundary prompt anchor.`
  - `ref_type`: `record`
    - `ref_id`: `rec-water-mill-design-asset-pack`
    - `ref_note`: `Design-context rule anchor.`

## Simulation Briefing Agent Brief Example

- `brief_id`: `brief-simulation-briefing-bootstrap-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `packet_id`: `pkt-dossier-bootstrap-water-mill-v1`
- `role_name`: `Simulation Briefing Agent`
- `agent_name_or_slot`: `simulation-briefing-slot-1`
- `purpose`: `Convert reviewed packet material into a later human-readable briefing shape without binding to runtime.`
- `approved_facts`:
  - `fact_id`: `bf-sim-review-downstream`
    - `fact_text`: `Any future simulation-facing interpretation remains downstream of human review.`
    - `fact_status`: `approved-for-packet`
    - `source_record_id`: `rec-reviewed-run-bundle-summary`
  - `fact_id`: `bf-sim-design-boundary`
    - `fact_text`: `Water Mill references remain design-asset context only.`
    - `fact_status`: `approved-for-packet`
    - `source_record_id`: `rec-water-mill-design-asset-pack`
- `task_focus`:
  - `map-to-space alignment`
  - `structure classification`
  - `thesis question extraction`
  - `hybrid-energy reasoning`
  - `maintenance dependency analysis`
- `forbidden_assumptions`:
  - `Do not imply execution rights.`
  - `Do not imply background analytic work can act autonomously.`
- `review_questions`:
  - `Which briefing statements remain safe as descriptive prompts only?`
  - `How should off-grid and waste themes stay non-executing in briefing form?`
- `brief_status`: `open-question`
- `created_at`: `2026-03-14T00:00:00Z example`
- `supporting_refs`:
  - `ref_type`: `packet`
    - `ref_id`: `pkt-dossier-bootstrap-water-mill-v1`
    - `ref_note`: `Packet boundary anchor.`
  - `ref_type`: `record`
    - `ref_id`: `rec-energy-support-features`
    - `ref_note`: `Hybrid-energy prompt anchor.`

## Background Analytic Work Note

Background analytic work may be described in briefs, including:
- maintenance reasoning
- waste interaction tracing
- compost or reclassification research prompts
- off-grid water and power interpretation prompts

This work remains proposal-only intellectual work and does not authorize execution.
