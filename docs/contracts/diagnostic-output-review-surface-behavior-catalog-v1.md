# Diagnostic Output Review Surface Behavior Catalog v1

## Purpose

This catalog defines the approved presentation-only behavior vocabulary for bounded review-surface handling of already-consumed diagnostic outputs.

The catalog is descriptive only. It does not define UI implementation, interaction mechanics, validator behavior, runtime behavior, scoring logic, or packaging semantics.

## Approved Behavior Vocabulary

### `group-for-review`

- Meaning: arrange already-consumed outputs together for bounded review context while preserving each output's identity and authority level.
- Allowed use: present multiple outputs together for review orientation.
- Forbidden use: create a package, archive, manifest, canonical set, or new output family.
- Boundary note: grouping may not alter source-of-truth discipline or convert presentation into transport behavior.

### `sort-by-existing-field`

- Meaning: order already-consumed outputs by existing preserved output-local vocabulary such as `output_type`, `consumer_profile`, `fixture_type`, `status`, `severity`, `boundary_at_risk`, or `stop_rule_reference`.
- Allowed use: adjust review order using existing preserved fields only.
- Forbidden use: invent ranking logic, urgency models, priority scores, or derived ordering authority.
- Boundary note: sorting may not suppress triggered stop conditions, canonical-path visibility, provenance notes, or boundary notes.

### `filter-by-existing-field`

- Meaning: narrow review presentation by existing preserved output-local vocabulary only.
- Allowed use: show a bounded subset for review orientation.
- Forbidden use: hide or erase active stop conditions, canonical-path visibility, provenance notes, boundary notes, or single-dossier context from outputs that remain presented.
- Boundary note: filtering may not become replacement selection, authority selection, or evidence suppression.

### `highlight-severity`

- Meaning: make the existing `severity` field more visible in review presentation.
- Allowed use: show output-local boundary seriousness more clearly.
- Forbidden use: recast severity into execution priority, urgency ranking, action score, or prioritization logic.
- Boundary note: highlighting may not invent new severity classes or normalize existing severity values.

### `highlight-stop-condition`

- Meaning: make an already-present stop condition more visible in review presentation.
- Allowed use: emphasize that a `stop-report` or other triggered stop condition requires halt-aware review treatment.
- Forbidden use: downgrade, override, soften, or hide the halt requirement.
- Boundary note: when a `stop-report` or triggered stop condition is present, this behavior becomes effectively mandatory for bounded review presentation.

### `show-canonical-path-reference`

- Meaning: expose the canonical fixture path for source-of-truth visibility when an output is presented.
- Allowed use: preserve traceability to the underlying canonical fixture.
- Forbidden use: treat the surfaced path as a replacement mirror, mutable target, or implementation navigation requirement.
- Boundary note: this behavior is allowed for all five output families and required wherever an output is presented.

### `show-provenance-note`

- Meaning: expose preserved provenance-oriented contextual text when present in the already-consumed output.
- Allowed use: support bounded review of lineage, context, and authority limits.
- Forbidden use: strengthen provenance wording into new authority or use it to replace canonical source text.
- Boundary note: allowed-with-stop-rules when present.

### `show-boundary-notes`

- Meaning: expose preserved boundary-oriented contextual text when present in the already-consumed output, including packet, Water Mill, dossier, or related boundary context.
- Allowed use: support bounded review of boundary integrity and stop awareness.
- Forbidden use: reinterpret boundary notes as execution approval, runtime authority, or replacement authority.
- Boundary note: allowed-with-stop-rules when present.

## Cross-Behavior Preservation Rules

All approved presentation behaviors must preserve:

- canonical paths as source of truth
- derived outputs as non-authoritative and non-replacing
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, never runtime world state
- single-dossier continuity
- no runtime authority
- no mutation authority
- no execution authority

## Explicit Stop Conditions

Stop if any behavior definition:

- implies UI implementation details
- implies validator or runtime behavior
- implies scoring, ranking, urgency, or prioritization logic
- turns grouping into packaging semantics
- turns path visibility into mirror or mutation behavior
- weakens any upstream stop rule or boundary rule
