# Fixture Consumer Output Fields v1

## Purpose

This document defines approved descriptive field vocabulary for future read-only diagnostic outputs derived from canonical fixtures. This is vocabulary only. It is not schema design, file-format design, serialization guidance, validation logic, or implementation guidance.

## Approved Field Vocabulary

### `output_type`

Names the approved output family being used. It refers only to the approved diagnostic output families defined in this phase.

### `consumer_profile`

Names the approved consumer profile under which the derived output was produced.

### `fixture_id`

Identifies the canonical fixture specimen being discussed. It refers back to the frozen promoted specimen set and may not invent a new canonical identity.

### `fixture_type`

Identifies the relevant canonical fixture type, such as promoted archive record, promoted derived packet, promoted agent brief, or promoted review decision.

### `canonical_path`

Points to the canonical fixture path that remains the source of truth. This vocabulary item supports traceability and may not imply a replaceable mirror path.

### `derived_from_capability`

Names the approved read-only capability context under which the derived output was formed. It may not introduce a new capability class.

### `status`

`status` means output-local assessment status only, not fixture fact status or review decision status.

### `severity`

`severity` means output-local boundary seriousness only, not execution priority.

### `observed_issue`

Describes the issue, drift, or boundary concern observed in relation to canonical fixture reading or comparison.

### `boundary_at_risk`

Names the boundary that is at risk, such as source-of-truth discipline, Water Mill boundary, packet/world-state separation, or single-dossier continuity.

### `stop_rule_reference`

Points to the relevant stop overlay or frozen stop-rule context when a stop-and-report condition applies.

### `provenance_note`

Provides descriptive provenance-aware context without replacing canonical source language.

### `fact_status_note`

Provides descriptive notes about fact-status discipline without redefining canonical fact-status authority.

### `packet_boundary_note`

Provides descriptive notes that preserve the rule that the packet is an interpretive artifact only and not runtime world state.

### `water_mill_boundary_note`

Provides descriptive notes that preserve the rule that Water Mill content remains design-asset-derived context only.

### `dossier_boundary_note`

Provides descriptive notes that preserve single-dossier continuity and block silent second-dossier expansion.

### `recommended_action`

`recommended_action` is review-only follow-up guidance and may not authorize mutation, runtime action, execution, or canonical replacement.

### `replacement_forbidden`

Carries explicit boundary language that the derived output may not replace canonical source text.

### `runtime_binding_forbidden`

Carries explicit boundary language that the derived output may not bind to runtime state, runtime action, or runtime approval.

### `mutation_forbidden`

Carries explicit boundary language that the derived output may not authorize mutation, rewrite, or silent correction.

## Vocabulary Boundary Notes

- These terms are descriptive vocabulary only.
- They do not imply requiredness, serialization, data typing, or validation behavior.
- They may not be used to soften canonical source-of-truth rules.
- They may not be used to elevate a derived output into a new authority layer.
