# Fixture Consumer Conformance Examples v1

## Purpose

This note gives concrete examples of conformant, boundary-edge, stop-and-report, and non-conformant behavior under the first consumer conformance matrix.

These examples are descriptive only and do not authorize implementation.

## Valid Conformant Behavior

### Example 1

- Consumer profile: `Fixture Reader`
- Fixture: [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- Behavior: reads the canonical `record_id`, provenance wording, and Water Mill design-asset boundary without altering the record or elevating it to observed built authority.
- Conformance result: `allowed-with-stop-rules`
- Why conformant: the consumer remains read-only and preserves Water Mill as design-asset-derived context only.

### Example 2

- Consumer profile: `Fixture Comparator`
- Fixture: [`promoted-agent-brief-v1.md`](C:/SmallVille/docs/examples/promoted-agent-brief-v1.md)
- Behavior: compares `brief_id`, `packet_id`, and forbidden-assumption wording against the frozen expected values and emits a drift note only if any wording changes.
- Conformance result: `allowed`
- Why conformant: the comparison stays inside the canonical text, preserves single-dossier continuity, and does not mutate the fixture.

### Example 3

- Consumer profile: `Fixture Export Consumer`
- Fixture: [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)
- Behavior: exports a non-authoritative summary stating that the decision remains review-only and not execution approval.
- Conformance result: `allowed-with-stop-rules`
- Why conformant: the export remains downstream only and preserves the approval boundary.

## Boundary-Edge But Still Acceptable Behavior

### Example 4

- Consumer profile: `Fixture Diagnostics Consumer`
- Fixture: [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- Behavior: emits a warning that a copied packet summary has weakened packet/world-state separation wording, but does not rewrite the packet or treat the warning as replacement truth.
- Conformance result: `allowed-with-stop-rules`
- Why still acceptable: diagnostics remain external and non-authoritative, and the consumer stops short of mutation.

### Example 5

- Consumer profile: `Fixture Reader`
- Fixture: [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)
- Behavior: reads `approved_for_next_gate` together with surrounding boundary text to confirm that it remains review-only.
- Conformance result: `allowed-with-stop-rules`
- Why still acceptable: the consumer preserves the approval boundary instead of inflating it into execution authority.

## Stop-And-Report Cases

### Example 6

- Consumer profile: `Fixture Comparator`
- Fixture: [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- Trigger: a later copy changes the canonical ID or drops the provenance link to the Water Mill pack notes.
- Required response: stop and report canonical ID or provenance failure.
- Why stop is required: immutable fixture identity and source trail have been weakened.

### Example 7

- Consumer profile: `Fixture Export Consumer`
- Fixture: [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- Trigger: an exported summary frames the packet as runtime world state or world-building instruction.
- Required response: stop and report packet/world-state boundary failure.
- Why stop is required: derived output is attempting to replace the packet boundary with runtime authority.

### Example 8

- Consumer profile: `Fixture Diagnostics Consumer`
- Fixture: [`promoted-agent-brief-v1.md`](C:/SmallVille/docs/examples/promoted-agent-brief-v1.md)
- Trigger: diagnostics flatten `open-question` or inferred content into observed claims to make the brief easier to consume.
- Required response: stop and report fact-status failure.
- Why stop is required: the consumer has started to infer away missing evidence.

## Clearly Non-Conformant Behavior

### Example 9

- Proposed behavior: bind [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md) to live runtime state so it can act as a runtime map or world-state seed.
- Why non-conformant: runtime coupling is forbidden and packet/world-state separation is broken.

### Example 10

- Proposed behavior: generate a replacement mirror for [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md) and treat that mirror as the new canonical source.
- Why non-conformant: derived outputs may not replace the canonical fixture at its canonical path.

### Example 11

- Proposed behavior: use Water Mill imagery in [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md) as direct proof of observed built reality.
- Why non-conformant: Water Mill remains design-asset-derived context only unless accepted evidence separately establishes observed built authority.

### Example 12

- Proposed behavior: merge a second dossier into export summaries so the review decision looks more complete.
- Why non-conformant: the single-dossier chain has been widened beyond approved scope.
