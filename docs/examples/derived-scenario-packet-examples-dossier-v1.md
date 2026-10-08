# Derived Scenario Packet Examples Dossier v1

## Packet Boundary

These packet examples are specification-only and interpretive only.

They are:
- reviewable packet examples
- not runtime world state
- not direct scene or world mutation authority
- not geometry or renderable asset language

## Schema-Aligned Example Packet

- `packet_id`: `pkt-dossier-bootstrap-water-mill-v1`
- `dossier_id`: `dossier-bootstrap-v1`
- `baseline_tag`: `smallville-post-proposal-parent-fix`
- `baseline_commit`: `150e2073fdb608efb4e76ad05cdacbfdb7119222`
- `source_record_ids`:
  - `rec-reviewed-run-bundle-summary`
  - `rec-control-run-smoke-context`
  - `rec-water-mill-design-asset-pack`
  - `rec-built-features-water-mill`
  - `rec-water-system-elements-water-mill`
  - `rec-access-and-maintenance-structures`
  - `rec-energy-support-features`
  - `rec-waste-and-reclassification-touchpoints`
- `harness_family`: `two-house-garden-v1 primary operational dossier with tiny-world control context`
- `scenario_target`: `single-dossier bootstrap interpretive packet only`
- `packet_summary`: `Interpretive packet example that combines accepted reviewed-run context, accepted control context, and Water Mill design-asset references without granting runtime, geometry, or world-state authority.`
- `compiler_role`: `Scenario Packet Compiler`
- `created_at`: `2026-03-14T00:00:00Z example`
- `packet_status`: `open-question`
- `supporting_refs`:
  - `ref_type`: `record`
    - `ref_id`: `rec-water-mill-design-asset-pack`
    - `ref_note`: `Design-asset pack provenance anchor.`
  - `ref_type`: `record`
    - `ref_id`: `rec-reviewed-run-bundle-summary`
    - `ref_note`: `Primary operational dossier anchor.`
  - `ref_type`: `record`
    - `ref_id`: `rec-control-run-smoke-context`
    - `ref_note`: `Control-context boundary anchor.`

## world_facts

- `fact_id`: `wf-built-mill-house`
  - `fact_text`: `A mill-house body is visible in the Water Mill design assets.`
  - fact_status: `observed`
  - `source_record_id`: `rec-built-features-water-mill`
  - `packet_use_note`: `Carry forward as descriptive built-feature vocabulary only.`
- `fact_id`: `wf-water-axle`
  - `fact_text`: `A water-wheel axle interface is visible entering the side housing.`
  - `fact_status`: `observed`
  - `source_record_id`: `rec-water-system-elements-water-mill`
  - `packet_use_note`: `Use as packet-ready water-system element without modeled flow claims.`
- `fact_id`: `wf-energy-hybrid`
  - `fact_text`: `The design supports a hybrid water-wind-solar interpretation prompt.`
  - `fact_status`: `inferred`
  - `source_record_id`: `rec-energy-support-features`
  - `packet_use_note`: `Interpretive prompt only, not integrated system proof.`
- `fact_id`: `wf-control-only`
  - `fact_text`: `tiny-world remains control context only and does not widen the operational dossier.`
  - `fact_status`: `approved-for-packet`
  - `source_record_id`: `rec-control-run-smoke-context`
  - `packet_use_note`: `Preserves single-dossier discipline.`
- `fact_id`: `wf-waste-boundary`
  - `fact_text`: `Waste and composting topics remain prompt-level inquiry rather than evidenced infrastructure.`
  - `fact_status`: `deferred`
  - `source_record_id`: `rec-waste-and-reclassification-touchpoints`
  - `packet_use_note`: `Keep below settled fact level.`

## task_classes

- `structure classification`
- `flow interpretation`
- `maintenance dependency analysis`
- `waste interaction tracing`
- `hybrid-energy reasoning`
- `thesis question extraction`

## constraints

- `one constrained dossier only`
- `Water Mill pack remains design-asset reference only`
- `packet is not runtime world state`
- `no direct world or scene mutation authority`
- `no runtime mutation`
- `no implementation or geometry authority`
- `no unsupported promotion of designed structures into observed real-world fact`

## open_questions

- `question_id`: `oq-water-path`
  - `question_text`: `What exact water path is intended?`
  - `question_status`: `open-question`
  - `source_record_ids`: [`rec-water-system-elements-water-mill`]
- `question_id`: `oq-tower-integration`
  - `question_text`: `Is the wind tower integrated with or adjacent to the mill design logic?`
  - `question_status`: `open-question`
  - `source_record_ids`: [`rec-energy-support-features`]
- `question_id`: `oq-waste-placement`
  - `question_text`: `Where should maintenance residue, composting, or reclassification prompts sit without overclaiming infrastructure?`
  - `question_status`: `open-question`
  - `source_record_ids`: [`rec-waste-and-reclassification-touchpoints`]
- `question_id`: `oq-canonical-promotion`
  - `question_text`: `Which packet facts are safe for first canonical specimen promotion?`
  - `question_status`: `deferred`
  - `source_record_ids`: [`rec-reviewed-run-bundle-summary`, `rec-control-run-smoke-context`]

## built_features

- `feature_name`: `mill-house body`
  - fact_status: `observed`
  - `source_record_id`: `rec-built-features-water-mill`
  - `feature_note`: `Visible structural body only; not authoritative real-world building evidence.`
- `feature_name`: `service deck / upper platform`
  - fact_status: `observed`
  - `source_record_id`: `rec-built-features-water-mill`
  - `feature_note`: `Visible elevated platform surface.`
- `feature_name`: `tower-core or service core`
  - fact_status: `inferred`
  - `source_record_id`: `rec-built-features-water-mill`
  - `feature_note`: `Interpretive class only.`
- `feature_name`: `maintenance landing`
  - fact_status: `approved-for-packet`
  - `source_record_id`: `rec-access-and-maintenance-structures`
  - `feature_note`: `Packet-ready descriptive label only.`

## water_system_elements

- `element_name`: `side-mounted water wheel`
  - fact_status: `observed`
  - `source_record_id`: `rec-water-system-elements-water-mill`
  - `element_note`: `Visible wheel structure only.`
- `element_name`: `axle interface into side housing`
  - fact_status: `observed`
  - `source_record_id`: `rec-water-system-elements-water-mill`
  - `element_note`: `Visible interface point only.`
- `element_name`: `wheel-chamber relationship`
  - fact_status: `inferred`
  - `source_record_id`: `rec-water-system-elements-water-mill`
  - `element_note`: `Interpretive label only.`
- `element_name`: `water path logic`
  - fact_status: `open-question`
  - `source_record_id`: `rec-water-system-elements-water-mill`
  - `element_note`: `Open hydraulic interpretation prompt.`

## structure_class

Schema mapping:
- carried through `built_features` names and notes
- summarized in `packet_summary`

- `mill-house`
  - status: `approved-for-packet`
- `service-deck`
  - status: `approved-for-packet`
- `tower-core`
  - status: `inferred`
- `wind-assist tower`
  - status: `approved-for-packet`
- `maintenance-access-point`
  - status: `approved-for-packet`

## spatial_context

Schema mapping:
- carried through `packet_summary`
- supported by `visualization_hints`

- primary operational anchor remains the accepted reviewed run
- `tiny-world` remains control context only
- Water Mill spatial content is descriptive reference material only
- platform, wheel side, rear landing, and tower positions may be described relationally
- no packet section may become geometry, mesh, runtime map layer, or scene asset instruction

## waste_interaction_points

Schema mapping:
- carried through `world_facts`, `open_questions`, and `research_thesis_links`

- `deck-side sorting surface`
  - fact_status: `open-question`
  - note: research prompt only; not directly evidenced as waste hardware
- `landing-adjacent residue handling point`
  - fact_status: `inferred`
  - note: interpretive maintenance prompt only

## composting / reclassification candidates

Schema mapping:
- carried through `research_thesis_links` and `open_questions`

- `organic by-product staging`
  - fact_status: `open-question`
  - note: candidate topic for later human review only
- `reclassification of maintenance residues`
  - fact_status: `approved-for-packet`
  - note: dossier theme only, not settled infrastructure fact

## off_grid_water_power_logic

Schema mapping:
- carried through `world_facts`, `constraints`, and `research_thesis_links`

- `water wheel as mechanical driver`
  - fact_status: `inferred`
- `wind tower as assistive or adjacent energy logic`
  - fact_status: `inferred`
- `solar trial-fit as supplemental energy-support feature`
  - fact_status: `observed`
- `combined off-grid routing model`
  - fact_status: `open-question`

## research_thesis_links

- `thesis_id`: `rtl-hybrid-boundary`
  - `prompt_text`: `How should hybrid water-wind-solar reasoning be bounded without overstating integration?`
  - `link_status`: `open-question`
  - `source_record_ids`: [`rec-energy-support-features`]
  - `notes`: `Interpretive prompt only, not truth claim.`
- `thesis_id`: `rtl-surface-zones`
  - `prompt_text`: `Which surfaces read as maintenance, observation, sorting, or microwork zones?`
  - `link_status`: `approved-for-packet`
  - `source_record_ids`: [`rec-built-features-water-mill`, `rec-access-and-maintenance-structures`]
  - `notes`: `Packet-ready question framing only.`
- `thesis_id`: `rtl-hidden-labor`
  - `prompt_text`: `How do hidden labor and human-interaction patterns appear in access routes, platforms, and maintenance surfaces?`
  - `link_status`: `approved-for-packet`
  - `source_record_ids`: [`rec-access-and-maintenance-structures`, `rec-waste-and-reclassification-touchpoints`]
  - `notes`: `Research prompt, not settled social claim.`
- `thesis_id`: `rtl-regional-variation`
  - `prompt_text`: `How should regional or cultural variation questions be framed without turning them into truth claims?`
  - `link_status`: `deferred`
  - `source_record_ids`: [`rec-water-mill-design-asset-pack`]
  - `notes`: `Deferred until stronger accepted evidence exists.`

## visualization_hints

- use relational language such as `wheel-side`, `rear landing`, `upper deck`, and `tower cavity`
- preserve that visual hints are descriptive only
- do not convert hints into geometry or asset directives
