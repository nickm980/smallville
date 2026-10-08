# Canonical Fixture Failure Cases v1

## Purpose

This note gives example failure cases for canonical fixture consumption and validation.

These examples are descriptive only and must not be treated as repair instructions.

## Failure Case 1: Wrong Canonical ID

- Fixture path: `docs/examples/promoted-archive-record-v1.md`
- Failure: canonical ID does not equal `rec-water-mill-design-asset-pack`
- Why it fails: canonical identity drift breaks immutable-fixture expectations

## Failure Case 2: Missing Provenance Note

- Fixture path: `docs/examples/promoted-derived-packet-v1.md`
- Failure: provenance note or supporting lineage summary is removed
- Why it fails: downstream consumers lose the promoted chain from record to packet

## Failure Case 3: Water Mill Treated As Observed Built Fact

- Fixture path: `docs/examples/promoted-archive-record-v1.md`
- Failure: Water Mill design-asset content is described as direct real-world built authority
- Why it fails: violates the design-asset-derived context rule

## Failure Case 4: Packet Treated As Runtime State

- Fixture path: `docs/examples/promoted-derived-packet-v1.md`
- Failure: packet wording implies runtime world state or scene/world mutation authority
- Why it fails: violates packet/world-state separation

## Failure Case 5: Inferred Fact Promoted Without Status

- Fixture path: any canonical fixture consuming packet or brief content
- Failure: inferred content is presented as settled or observed without preserved status discipline
- Why it fails: erases uncertainty and weakens fixture integrity

## Failure Case 6: Second Dossier Silently Merged

- Fixture path: `docs/contracts/ueia-canonical-fixture-contract-v1.md` or downstream consumer output
- Failure: a second dossier or non-canonical operational source is introduced without explicit approval
- Why it fails: violates single-dossier continuity

## Failure Case 7: Review Decision Treated As Execution Approval

- Fixture path: `docs/examples/promoted-review-decision-v1.md`
- Failure: `approved_for_next_gate` is interpreted as runtime or implementation approval
- Why it fails: approval-boundary discipline is broken
