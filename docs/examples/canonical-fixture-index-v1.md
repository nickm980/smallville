# Canonical Fixture Index v1

## Purpose

This index records the immutable canonical fixture set and the allowed versus forbidden downstream uses for each promoted specimen.

## Fixture 1

- Canonical path: [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- Canonical ID: `rec-water-mill-design-asset-pack`
- Artifact type: `archive-record`
- Provenance note: `Anchored to Water Mill pack inventory, manifests, notes, and image-backed observations with explicit design-asset boundary language.`
- Allowed downstream uses:
  - read as provenance anchor
  - check canonical ID and section presence
  - compare provenance wording across fixture revisions
  - derive non-canonical diagnostics about design-asset boundary preservation
- Forbidden downstream uses:
  - mutate the file
  - treat Water Mill as observed real-world authority
  - use the fixture as implementation instruction
  - erase deferred or open-question content

## Fixture 2

- Canonical path: [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- Canonical ID: `pkt-dossier-bootstrap-water-mill-v1`
- Artifact type: `derived-packet`
- Provenance note: `Anchored to the promoted archive record plus reviewed-run and control-context lineage, with explicit packet/world-state separation.`
- Allowed downstream uses:
  - read as the canonical interpretive packet specimen
  - validate baseline and packet identity fields
  - compare packet constraints and open-question wording
  - export diagnostics about provenance linkage and packet boundary language
- Forbidden downstream uses:
  - treat packet content as runtime world state
  - derive scene/world mutation authority from packet text
  - mutate packet wording in place
  - convert descriptive spatial language into geometry or map-layer authority

## Fixture 3

- Canonical path: [`promoted-agent-brief-v1.md`](C:/SmallVille/docs/examples/promoted-agent-brief-v1.md)
- Canonical ID: `brief-review-analyst-bootstrap-v1`
- Artifact type: `agent-brief`
- Provenance note: `Anchored to the promoted packet and the Water Mill design-asset record, with review-oriented downstream use only.`
- Allowed downstream uses:
  - read as the canonical brief specimen
  - validate brief identity and packet linkage
  - compare approved-fact and forbidden-assumption wording
  - export diagnostics about non-executing brief discipline
- Forbidden downstream uses:
  - treat the brief as execution authority
  - use the brief as a prompt-binding runtime instruction
  - promote review prompts into settled truth claims
  - mutate the brief file

## Fixture 4

- Canonical path: [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)
- Canonical ID: `rev-brief-review-analyst-v1`
- Artifact type: `review-decision`
- Provenance note: `Anchored to the promoted brief and preserves the approval boundary without execution authority.`
- Allowed downstream uses:
  - read as the canonical review-decision specimen
  - validate decision identity, subject linkage, and gate-level fields
  - compare rationale and bias-review preservation
  - export diagnostics about approval-boundary integrity
- Forbidden downstream uses:
  - treat the decision as runtime authorization
  - treat approval for next gate as execution approval
  - rewrite rationale or required changes in place
  - couple the decision to runtime state
