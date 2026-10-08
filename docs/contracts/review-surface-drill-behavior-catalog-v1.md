# Review Surface Drill Behavior Catalog v1

## Purpose

This catalog defines the approved descriptive behavior vocabulary for bounded review-surface consumption drills over already-presented diagnostic outputs.

The catalog is descriptive only. It does not define UI mechanics, runtime hooks, validator behavior, automation, control behavior, executable state, or prioritization logic.

## Approved Drill Behavior Vocabulary

### `read-presented-output`

- Meaning: read an already-presented diagnostic output together with already-preserved review-surface context.
- Allowed use: orient bounded review around the presented output as currently surfaced.
- Forbidden use: reopen derivation, replace canonical source text, or invent new output authority.
- Boundary note: reading drill context does not change output meaning or authority.

### `preserve-presented-order`

- Meaning: preserve the order already present in the review-surface context while conducting the drill.
- Allowed use: keep an existing review order visible for audit continuity.
- Forbidden use: convert preserved order into workflow sequencing, execution sequencing, or navigation design.
- Boundary note: preserved order may orient review only.

### `preserve-grouped-review-context`

- Meaning: preserve already-presented grouped review context while conducting the drill.
- Allowed use: keep existing grouped context visible for bounded review continuity.
- Forbidden use: reinterpret grouped context as packaging, archive, manifest, canonical set, or new output family.
- Boundary note: grouped context remains descriptive only.

### `pause-on-drift`

- Meaning: pause bounded review attention on already-surfaced drift-oriented content without converting the pause into control or correction behavior.
- Allowed use: hold review attention on surfaced drift for human review context.
- Forbidden use: treat pause as correction authority, prioritization logic, or execution sequencing.
- Boundary note: pause is not a hard stop and is not appropriate when a surfaced hard stop already exists.

### `stop-on-hard-condition`

- Meaning: stop bounded review walkthrough behavior when a surfaced `stop-report` or other surfaced hard stop condition is present.
- Allowed use: preserve halt semantics from upstream stop rules.
- Forbidden use: soften, defer, hide, or override the halt requirement.
- Boundary note: when a surfaced hard stop is present, this behavior becomes effectively mandatory.

### `preserve-canonical-path-visibility`

- Meaning: keep the canonical fixture path visible wherever the drill references a presented diagnostic output.
- Allowed use: preserve traceability to the underlying canonical source of truth.
- Forbidden use: hide the canonical path, treat it as optional, or reinterpret it as a mutable or replaceable target.
- Boundary note: this behavior is allowed and required for all five output families.

### `preserve-provenance-note-visibility`

- Meaning: keep surfaced provenance notes visible when they are already present in the review-surface context.
- Allowed use: preserve lineage, context, and authority limits for bounded review.
- Forbidden use: strengthen provenance into new authority or use it to replace canonical source text.
- Boundary note: allowed-with-stop-rules when present.

### `preserve-boundary-note-visibility`

- Meaning: keep surfaced boundary notes visible when they are already present in the review-surface context.
- Allowed use: preserve packet, Water Mill, dossier, and related boundary context.
- Forbidden use: reinterpret boundary notes as execution approval, runtime authority, or replacement authority.
- Boundary note: allowed-with-stop-rules when present.

### `preserve-severity-visibility`

- Meaning: keep surfaced severity visible as already-presented boundary seriousness context.
- Allowed use: preserve existing severity visibility for bounded review orientation.
- Forbidden use: recast severity into execution priority, urgency ranking, action score, or prioritization logic.
- Boundary note: severity remains context only and may not drive execution or control semantics.

## Cross-Behavior Preservation Rules

All approved drill behaviors must preserve:

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

- implies UI mechanics or navigation design
- implies runtime hooks or runtime workflow
- implies automation, validator behavior, or control behavior
- implies scoring, ranking, urgency, or prioritization logic
- turns grouped context into packaging semantics
- weakens any upstream stop rule or preserved boundary rule
