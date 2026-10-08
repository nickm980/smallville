# Fixture Consumer Diagnostic Output Examples v1

## Purpose

These examples illustrate approved read-only diagnostic output families against the frozen canonical fixtures. They are illustrative only. They are not tooling design, validator design, file-format design, runtime behavior, or canonical replacement behavior.

## Valid `summary`

```text
output_type: summary
consumer_profile: Fixture Reader
fixture_id: pkt-dossier-bootstrap-water-mill-v1
fixture_type: promoted derived packet
canonical_path: docs/examples/promoted-derived-packet-v1.md
derived_from_capability: read-boundary-language
status: boundary-preserved
severity: low
observed_issue: no immediate boundary break observed in current packet reading
boundary_at_risk: packet/world-state separation
provenance_note: summary derived from the canonical packet at its canonical path; canonical text remains the source of truth
fact_status_note: summary preserves the packet's existing uncertainty and does not promote inferred content to observed
packet_boundary_note: the packet remains an interpretive artifact only and is not runtime world state
water_mill_boundary_note: Water Mill context remains design-asset-derived only
dossier_boundary_note: no second dossier introduced
recommended_action: continue human review against canonical text if downstream questions arise
replacement_forbidden: true
runtime_binding_forbidden: true
mutation_forbidden: true
```

## Valid `drift-note`

```text
output_type: drift-note
consumer_profile: Fixture Comparator
fixture_id: rec-water-mill-design-asset-pack
fixture_type: promoted archive record
canonical_path: docs/examples/promoted-archive-record-v1.md
derived_from_capability: compare-provenance-language
status: drift-detected
severity: medium
observed_issue: a downstream paraphrase omits the design-asset-derived qualifier from Water Mill provenance wording
boundary_at_risk: Water Mill design-asset boundary
stop_rule_reference: water-mill-authority-inflation
provenance_note: canonical record preserves Water Mill as design-asset-derived context only; downstream wording must not weaken that qualifier
fact_status_note: no fact-status promotion is permitted through paraphrase
packet_boundary_note: not applicable to this archive-record-focused drift note
water_mill_boundary_note: Water Mill may not be restated as observed built authority without accepted evidence
dossier_boundary_note: comparison remains within one dossier chain only
recommended_action: restore the missing provenance qualifier in the downstream derived wording
replacement_forbidden: true
runtime_binding_forbidden: true
mutation_forbidden: true
```

## Valid `stop-report`

```text
output_type: stop-report
consumer_profile: Fixture Diagnostics Consumer
fixture_id: rev-brief-review-analyst-v1
fixture_type: promoted review decision
canonical_path: docs/examples/promoted-review-decision-v1.md
derived_from_capability: derive-diagnostics
status: stop-triggered
severity: high
observed_issue: a downstream note attempts to read review approval as execution approval
boundary_at_risk: review-decision execution boundary
stop_rule_reference: review-decision-treated-as-execution-approval
provenance_note: canonical review decision remains part of the dossier's approval boundary and does not authorize execution
fact_status_note: no fact-status authority is expanded by this stop report
packet_boundary_note: any linked packet remains interpretive artifact only
water_mill_boundary_note: any Water Mill context remains design-asset-derived only
dossier_boundary_note: no second dossier may be introduced while resolving this stop condition
recommended_action: stop downstream use and return to canonical review-decision language for human review
replacement_forbidden: true
runtime_binding_forbidden: true
mutation_forbidden: true
```

## Valid `diagnostics-bundle`

```text
output_type: diagnostics-bundle
consumer_profile: Fixture Diagnostics Consumer
fixture_id: brief-review-analyst-bootstrap-v1
fixture_type: promoted agent brief
canonical_path: docs/examples/promoted-agent-brief-v1.md
derived_from_capability: derive-diagnostics
status: grouped-review-ready
severity: medium
observed_issue: grouped output combines one summary and one drift-note for audit-oriented review
boundary_at_risk: canonical source-of-truth discipline
stop_rule_reference: none-triggered
provenance_note: grouped output remains derived only and each included note points back to canonical source text
fact_status_note: grouped output preserves existing uncertainty and does not collapse fact-status distinctions
packet_boundary_note: any packet references remain interpretive only and not runtime state
water_mill_boundary_note: Water Mill references remain design-asset-derived context only
dossier_boundary_note: grouped output remains within the same dossier chain
recommended_action: review grouped notes alongside the canonical brief text without treating the group as a replacement artifact
replacement_forbidden: true
runtime_binding_forbidden: true
mutation_forbidden: true
```

## Valid `export-summary`

```text
output_type: export-summary
consumer_profile: Fixture Export Consumer
fixture_id: rec-water-mill-design-asset-pack
fixture_type: promoted archive record
canonical_path: docs/examples/promoted-archive-record-v1.md
derived_from_capability: export-summary
status: export-ready-for-review
severity: low
observed_issue: none; export-oriented summary preserves upstream provenance and boundary language
boundary_at_risk: none-immediate
provenance_note: export summary is derived from the canonical archive record and does not replace it
fact_status_note: export summary preserves uncertainty and does not promote inferred content to observed
packet_boundary_note: not applicable to this archive-record-focused export summary
water_mill_boundary_note: Water Mill remains design-asset-derived context only in exported wording
dossier_boundary_note: export summary remains within a single dossier chain
recommended_action: hand off for bounded review with canonical path retained as source of truth
replacement_forbidden: true
runtime_binding_forbidden: true
mutation_forbidden: true
```

## Boundary-Edge But Still Acceptable Example

```text
output_type: drift-note
consumer_profile: Fixture Comparator
fixture_id: pkt-dossier-bootstrap-water-mill-v1
fixture_type: promoted derived packet
canonical_path: docs/examples/promoted-derived-packet-v1.md
derived_from_capability: compare-boundary-language
status: drift-detected
severity: high
observed_issue: downstream wording nearly treats packet spatial language as if it were world-state authority
boundary_at_risk: packet/world-state separation
stop_rule_reference: packet-world-state-confusion
provenance_note: the canonical packet remains interpretive source material only and must still be read at its canonical path
fact_status_note: no inferred packet content may be upgraded through downstream comparison language
packet_boundary_note: packet is interpretive artifact only, not runtime world state
water_mill_boundary_note: any Water Mill references remain design-asset-derived only
dossier_boundary_note: no second dossier appears in the compared text
recommended_action: tighten the downstream wording before any further read-only use
replacement_forbidden: true
runtime_binding_forbidden: true
mutation_forbidden: true
```

This remains acceptable because it reports boundary risk without turning the drift-note into continuation approval, runtime instruction, or canonical replacement.

## Clearly Non-Conformant Examples

### Non-conformant `summary`

```text
output_type: summary
consumer_profile: Fixture Reader
fixture_id: pkt-dossier-bootstrap-water-mill-v1
fixture_type: promoted derived packet
canonical_path: docs/examples/promoted-derived-packet-v1.md
derived_from_capability: read-boundary-language
status: approved-for-runtime
severity: urgent
observed_issue: none
boundary_at_risk: none
provenance_note: this summary now serves as the working source because it is cleaner than the canonical packet
fact_status_note: inferred packet details are now treated as observed for downstream simplicity
packet_boundary_note: packet can now be used as world-state guidance
water_mill_boundary_note: Water Mill structures are treated here as settled built authority
dossier_boundary_note: additional local dossier fragments may be merged if useful
recommended_action: use this summary in place of the packet and continue execution
replacement_forbidden: false
runtime_binding_forbidden: false
mutation_forbidden: false
```

This is non-conformant because it creates replacement authority, runtime authority, mutation openness, packet/world-state confusion, Water Mill authority inflation, and second-dossier expansion.

### Non-conformant `diagnostics-bundle`

```text
output_type: diagnostics-bundle
consumer_profile: Fixture Export Consumer
fixture_id: brief-review-analyst-bootstrap-v1
fixture_type: promoted agent brief
canonical_path: docs/examples/promoted-agent-brief-v1.md
derived_from_capability: export-diagnostics
status: packaging-complete
severity: medium
observed_issue: this bundle defines the new transport package for downstream systems
boundary_at_risk: none
stop_rule_reference: none
provenance_note: canonical source is mirrored here for downstream substitution
fact_status_note: uncertain content is normalized away for portability
packet_boundary_note: any packet references may be forwarded as operational world state
water_mill_boundary_note: Water Mill references are treated as settled physical truth for downstream use
dossier_boundary_note: additional dossier content can be included in later bundle revisions
recommended_action: distribute as the new authoritative package
replacement_forbidden: false
runtime_binding_forbidden: false
mutation_forbidden: false
```

This is non-conformant because it turns a descriptive grouped-output family into packaging design, creates a canonical mirror, weakens uncertainty, permits packet/world-state confusion, inflates Water Mill authority, and widens dossier scope.
