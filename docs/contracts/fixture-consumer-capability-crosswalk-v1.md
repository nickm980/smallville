# Fixture Consumer Capability Crosswalk v1

## Purpose

This note defines the capability-level crosswalk between approved consumer profiles, approved canonical fixture types, and the approved v1 capability vocabulary.

## Legend

- `allowed`: the named capability is permitted within the bounded profile-fixture interaction.
- `allowed-with-stop-rules`: the named capability is permitted only while stop overlays remain untriggered.
- `not-allowed`: the named capability is outside the approved bounded interaction for that profile-fixture context.

No classification grants blanket downstream authority. Every classification remains subordinate to immutable fixtures, canonical paths as source of truth, Water Mill as design-asset-derived context only, packet/world-state separation, single-dossier continuity, and non-replacing derived outputs.

## Shared Across All Profiles And All Four Canonical Fixture Types

- `mutate-fixture`: `not-allowed` - immutable canonical fixtures may not be changed in place.
- `rewrite-fixture`: `not-allowed` - rewrite behavior becomes hidden mutation and weakens source-of-truth discipline.
- `replace-canonical-source`: `not-allowed` - no derived artifact may replace the frozen canonical fixture at its canonical path.
- `bind-to-runtime`: `not-allowed` - runtime coupling is outside the approved read-only capability layer.
- `infer-away-uncertainty`: `not-allowed` - uncertainty may not be erased through reading, comparison, derivation, or export.
- `promote-inferred-to-observed`: `not-allowed` - inferred content may not be elevated to observed authority without accepted evidence.

## Profile: Fixture Reader

### Shared Across All Four Canonical Fixture Types

- `compare-canonical-id`: `not-allowed` - comparison is not the reader's approved role.
- `compare-section-presence`: `not-allowed` - section comparison belongs to comparator or diagnostics use.
- `compare-provenance-language`: `not-allowed` - provenance comparison exceeds bounded reader behavior.
- `compare-boundary-language`: `not-allowed` - boundary comparison exceeds bounded reader behavior.
- `derive-diagnostics`: `not-allowed` - the reader does not derive diagnostics.
- `derive-drift-notes`: `not-allowed` - the reader does not produce drift notes.
- `export-summary`: `not-allowed` - export belongs to the export consumer.
- `export-diagnostics`: `not-allowed` - diagnostics export belongs to the export consumer.

### Promoted Archive Record

- `read-canonical-id`: `allowed` - the record ID may be read as immutable source text.
- `read-provenance-language`: `allowed-with-stop-rules` - provenance may be read, but Water Mill must remain design-asset-derived context only.
- `read-fact-status-language`: `allowed-with-stop-rules` - fact-status wording may be read, but inferred or deferred content must not be inflated.
- `read-boundary-language`: `allowed-with-stop-rules` - boundary wording may be read, but design-asset limits must remain explicit.

### Promoted Derived Packet

- `read-canonical-id`: `allowed` - the packet ID may be read as immutable source text.
- `read-provenance-language`: `allowed-with-stop-rules` - packet lineage may be read, but single-dossier continuity must remain explicit.
- `read-fact-status-language`: `allowed-with-stop-rules` - world-fact status wording may be read, but uncertainty must remain preserved.
- `read-boundary-language`: `allowed-with-stop-rules` - packet boundary wording may be read, but the packet must never be treated as runtime world state.

### Promoted Agent Brief

- `read-canonical-id`: `allowed` - the brief ID may be read as immutable source text.
- `read-provenance-language`: `allowed` - brief linkage and supporting references may be read as preserved downstream lineage.
- `read-fact-status-language`: `allowed-with-stop-rules` - approved-fact wording may be read, but prompts and uncertainties must not be upgraded to truth claims.
- `read-boundary-language`: `allowed-with-stop-rules` - brief boundary wording may be read, but the brief must remain non-executing.

### Promoted Review Decision

- `read-canonical-id`: `allowed` - the decision ID may be read as immutable source text.
- `read-provenance-language`: `allowed` - dossier lineage and subject linkage may be read as preserved review context.
- `read-fact-status-language`: `not-allowed` - fact-status language is not a primary review-decision capability context in this v1 crosswalk.
- `read-boundary-language`: `allowed-with-stop-rules` - approval-boundary wording may be read, but `approved_for_next_gate` must never become execution approval.

## Profile: Fixture Comparator

### Shared Across All Four Canonical Fixture Types

- `derive-diagnostics`: `not-allowed` - diagnostics derivation belongs to the diagnostics consumer.
- `export-summary`: `not-allowed` - export belongs to the export consumer.
- `export-diagnostics`: `not-allowed` - diagnostics export belongs to the export consumer.

### Promoted Archive Record

- `read-canonical-id`: `allowed` - comparison requires reading the frozen record identity.
- `read-provenance-language`: `allowed-with-stop-rules` - comparison requires reading provenance, but Water Mill may not be elevated to observed built authority.
- `read-fact-status-language`: `allowed-with-stop-rules` - fact-status wording may be read to support comparison, but uncertainty must remain intact.
- `read-boundary-language`: `allowed-with-stop-rules` - boundary wording may be read to support comparison, but design-asset limits must remain explicit.
- `compare-canonical-id`: `allowed-with-stop-rules` - ID comparison is approved, but canonical drift triggers stop-and-report.
- `compare-section-presence`: `allowed` - section presence may be compared as schema-aligned, read-only structure.
- `compare-provenance-language`: `allowed-with-stop-rules` - provenance comparison is approved, but weakening or authority inflation triggers stop.
- `compare-boundary-language`: `allowed-with-stop-rules` - boundary comparison is approved, but Water Mill boundary weakening triggers stop.
- `derive-drift-notes`: `allowed-with-stop-rules` - drift notes may be derived, but they remain non-authoritative and non-replacing.

### Promoted Derived Packet

- `read-canonical-id`: `allowed` - comparison requires reading the frozen packet identity.
- `read-provenance-language`: `allowed-with-stop-rules` - provenance lineage may be read, but single-dossier continuity must remain intact.
- `read-fact-status-language`: `allowed-with-stop-rules` - packet status wording may be read, but uncertainty must not be collapsed.
- `read-boundary-language`: `allowed-with-stop-rules` - boundary wording may be read, but packet/world-state separation must remain explicit.
- `compare-canonical-id`: `allowed-with-stop-rules` - ID comparison is approved, but packet drift triggers stop-and-report.
- `compare-section-presence`: `allowed` - section presence may be compared as read-only packet structure.
- `compare-provenance-language`: `allowed-with-stop-rules` - provenance comparison is approved, but lineage weakening triggers stop.
- `compare-boundary-language`: `allowed-with-stop-rules` - boundary comparison is approved, but runtime-world-state drift triggers stop.
- `derive-drift-notes`: `allowed-with-stop-rules` - drift notes may be derived, but they may not replace packet source text.

### Promoted Agent Brief

- `read-canonical-id`: `allowed` - comparison requires reading the frozen brief identity.
- `read-provenance-language`: `allowed` - brief linkage may be read as preserved downstream lineage.
- `read-fact-status-language`: `allowed-with-stop-rules` - approved-fact wording may be read, but prompts must not be turned into observed truth.
- `read-boundary-language`: `allowed-with-stop-rules` - boundary wording may be read, but the brief must remain non-executing.
- `compare-canonical-id`: `allowed-with-stop-rules` - ID comparison is approved, but brief drift triggers stop-and-report.
- `compare-section-presence`: `allowed` - section presence may be compared as brief-shape preservation.
- `compare-provenance-language`: `allowed-with-stop-rules` - provenance comparison is approved, but linkage weakening triggers stop.
- `compare-boundary-language`: `allowed-with-stop-rules` - boundary comparison is approved, but execution-authority drift triggers stop.
- `derive-drift-notes`: `allowed` - drift notes may be derived as non-authoritative comparison output.

### Promoted Review Decision

- `read-canonical-id`: `allowed` - comparison requires reading the frozen decision identity.
- `read-provenance-language`: `allowed` - dossier and subject linkage may be read as preserved review lineage.
- `read-fact-status-language`: `not-allowed` - fact-status comparison is not a primary review-decision capability context in this v1 crosswalk.
- `read-boundary-language`: `allowed-with-stop-rules` - approval-boundary wording may be read, but execution drift triggers stop.
- `compare-canonical-id`: `allowed-with-stop-rules` - ID comparison is approved, but drift triggers stop-and-report.
- `compare-section-presence`: `allowed` - section presence may be compared as review-decision structure preservation.
- `compare-provenance-language`: `allowed-with-stop-rules` - provenance comparison is approved, but subject-lineage weakening triggers stop.
- `compare-boundary-language`: `allowed-with-stop-rules` - approval-boundary comparison is approved, but `approved_for_next_gate` may not be recast as execution approval.
- `derive-drift-notes`: `allowed-with-stop-rules` - drift notes may be derived, but they remain external and non-authoritative.

## Profile: Fixture Diagnostics Consumer

### Shared Across All Four Canonical Fixture Types

- `export-summary`: `not-allowed` - summary export belongs to the export consumer.
- `export-diagnostics`: `not-allowed` - diagnostics export belongs to the export consumer.

### Promoted Archive Record

- `read-canonical-id`: `allowed` - diagnostics require reading the frozen record identity.
- `read-provenance-language`: `allowed-with-stop-rules` - provenance diagnostics are approved, but Water Mill must remain design-asset-derived context only.
- `read-fact-status-language`: `allowed-with-stop-rules` - fact-status diagnostics are approved, but uncertainty must remain explicit.
- `read-boundary-language`: `allowed-with-stop-rules` - boundary diagnostics are approved, but design-asset limits must remain intact.
- `compare-canonical-id`: `allowed-with-stop-rules` - ID drift detection is approved, but drift triggers stop-and-report.
- `compare-section-presence`: `allowed` - section-presence checking is approved as read-only diagnostics input.
- `compare-provenance-language`: `allowed-with-stop-rules` - provenance weakening detection is approved, but may not invent missing evidence.
- `compare-boundary-language`: `allowed-with-stop-rules` - boundary weakening detection is approved, but Water Mill inflation triggers stop.
- `derive-diagnostics`: `allowed-with-stop-rules` - diagnostics may be derived, but they remain external and non-replacing.
- `derive-drift-notes`: `allowed-with-stop-rules` - drift notes may be derived, but may not supersede canonical source text.

### Promoted Derived Packet

- `read-canonical-id`: `allowed` - diagnostics require reading the frozen packet identity.
- `read-provenance-language`: `allowed-with-stop-rules` - lineage diagnostics are approved, but single-dossier continuity must remain explicit.
- `read-fact-status-language`: `allowed-with-stop-rules` - packet-status diagnostics are approved, but uncertainty must remain preserved.
- `read-boundary-language`: `allowed-with-stop-rules` - packet boundary diagnostics are approved, but runtime-world-state drift triggers stop.
- `compare-canonical-id`: `allowed-with-stop-rules` - ID drift detection is approved, but drift triggers stop-and-report.
- `compare-section-presence`: `allowed` - section-presence checking is approved as read-only diagnostics input.
- `compare-provenance-language`: `allowed-with-stop-rules` - provenance weakening detection is approved, but may not infer away gaps.
- `compare-boundary-language`: `allowed-with-stop-rules` - packet/world-state weakening detection is approved, but may not recast packet text as runtime state.
- `derive-diagnostics`: `allowed-with-stop-rules` - diagnostics may be derived, but they remain external and non-replacing.
- `derive-drift-notes`: `allowed-with-stop-rules` - drift notes may be derived, but may not replace packet source text.

### Promoted Agent Brief

- `read-canonical-id`: `allowed` - diagnostics require reading the frozen brief identity.
- `read-provenance-language`: `allowed` - linkage diagnostics may read preserved downstream provenance.
- `read-fact-status-language`: `allowed-with-stop-rules` - approved-fact diagnostics are approved, but prompts may not become truth claims.
- `read-boundary-language`: `allowed-with-stop-rules` - boundary diagnostics are approved, but the brief must remain non-executing.
- `compare-canonical-id`: `allowed-with-stop-rules` - ID drift detection is approved, but drift triggers stop-and-report.
- `compare-section-presence`: `allowed` - section-presence checking is approved as read-only diagnostics input.
- `compare-provenance-language`: `allowed-with-stop-rules` - provenance weakening detection is approved, but may not invent missing support.
- `compare-boundary-language`: `allowed-with-stop-rules` - execution-boundary weakening detection is approved, but may not reframe the brief as authority.
- `derive-diagnostics`: `allowed-with-stop-rules` - diagnostics may be derived, but they remain external and non-authoritative.
- `derive-drift-notes`: `allowed` - drift notes may be derived as downstream-only diagnostics.

### Promoted Review Decision

- `read-canonical-id`: `allowed` - diagnostics require reading the frozen decision identity.
- `read-provenance-language`: `allowed` - linkage diagnostics may read preserved review provenance.
- `read-fact-status-language`: `not-allowed` - fact-status diagnostics are not a primary review-decision capability context in this v1 crosswalk.
- `read-boundary-language`: `allowed-with-stop-rules` - approval-boundary diagnostics are approved, but execution drift triggers stop.
- `compare-canonical-id`: `allowed-with-stop-rules` - ID drift detection is approved, but drift triggers stop-and-report.
- `compare-section-presence`: `allowed` - section-presence checking is approved as read-only diagnostics input.
- `compare-provenance-language`: `allowed-with-stop-rules` - provenance weakening detection is approved, but may not weaken subject linkage.
- `compare-boundary-language`: `allowed-with-stop-rules` - approval-boundary weakening detection is approved, but `approved_for_next_gate` may not be recast as execution approval.
- `derive-diagnostics`: `allowed-with-stop-rules` - diagnostics may be derived, but they remain external and non-replacing.
- `derive-drift-notes`: `allowed-with-stop-rules` - drift notes may be derived, but may not become substitute canonical truth.

## Profile: Fixture Export Consumer

### Shared Across All Four Canonical Fixture Types

- `compare-canonical-id`: `not-allowed` - comparison belongs to comparator or diagnostics use.
- `compare-section-presence`: `not-allowed` - section comparison exceeds bounded export behavior.
- `compare-provenance-language`: `not-allowed` - provenance comparison exceeds bounded export behavior.
- `compare-boundary-language`: `not-allowed` - boundary comparison exceeds bounded export behavior.
- `derive-diagnostics`: `not-allowed` - diagnostics derivation belongs to the diagnostics consumer.
- `derive-drift-notes`: `not-allowed` - drift-note derivation belongs to comparator or diagnostics use.

### Promoted Archive Record

- `read-canonical-id`: `allowed` - export requires reading the frozen record identity before producing downstream summaries.
- `read-provenance-language`: `allowed-with-stop-rules` - export may read provenance, but Water Mill must remain design-asset-derived context only.
- `read-fact-status-language`: `allowed-with-stop-rules` - export may read fact-status wording, but uncertainty must not be flattened.
- `read-boundary-language`: `allowed-with-stop-rules` - export may read boundary wording, but design-asset limits must remain explicit.
- `export-summary`: `allowed-with-stop-rules` - summaries may be exported, but may not replace the canonical record or imply new authority.
- `export-diagnostics`: `allowed-with-stop-rules` - diagnostics may be exported, but they remain downstream only and non-replacing.

### Promoted Derived Packet

- `read-canonical-id`: `allowed` - export requires reading the frozen packet identity before producing downstream summaries.
- `read-provenance-language`: `allowed-with-stop-rules` - export may read lineage, but single-dossier continuity must remain explicit.
- `read-fact-status-language`: `allowed-with-stop-rules` - export may read packet status wording, but uncertainty must not be collapsed.
- `read-boundary-language`: `allowed-with-stop-rules` - export may read packet boundary wording, but packet/world-state separation must remain explicit.
- `export-summary`: `allowed-with-stop-rules` - summaries may be exported, but may not imply world-building or runtime-state authority.
- `export-diagnostics`: `allowed-with-stop-rules` - diagnostics may be exported, but they may not replace packet source text.

### Promoted Agent Brief

- `read-canonical-id`: `allowed` - export requires reading the frozen brief identity before producing downstream summaries.
- `read-provenance-language`: `allowed` - export may read preserved downstream linkage.
- `read-fact-status-language`: `allowed-with-stop-rules` - export may read approved-fact wording, but prompts may not become settled truth claims.
- `read-boundary-language`: `allowed-with-stop-rules` - export may read boundary wording, but the brief must remain non-executing.
- `export-summary`: `allowed-with-stop-rules` - summaries may be exported, but may not imply automatic approval or canonical replacement.
- `export-diagnostics`: `allowed-with-stop-rules` - diagnostics may be exported, but they remain downstream only and non-replacing.

### Promoted Review Decision

- `read-canonical-id`: `allowed` - export requires reading the frozen decision identity before producing downstream summaries.
- `read-provenance-language`: `allowed` - export may read preserved review linkage.
- `read-fact-status-language`: `not-allowed` - fact-status export is not a primary review-decision capability context in this v1 crosswalk.
- `read-boundary-language`: `allowed-with-stop-rules` - export may read approval-boundary wording, but execution drift triggers stop.
- `export-summary`: `allowed-with-stop-rules` - summaries may be exported, but may not imply runtime authorization or replace source text.
- `export-diagnostics`: `allowed-with-stop-rules` - diagnostics may be exported, but they remain downstream only and non-replacing.

## Crosswalk Preservation Rules

Every crosswalk classification must preserve:
- canonical fixture immutability
- canonical paths as source of truth
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, not runtime world state
- single-dossier continuity
- derived outputs as non-authoritative and non-replacing
