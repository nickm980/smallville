# Fixture Consumer Capability Crosswalk Examples v1

## Purpose

This note gives concrete examples of valid, boundary-edge, stop-and-report, and clearly non-conformant capability use under the v1 capability crosswalk.

These examples are descriptive only and do not authorize implementation.

## Valid Capability Use

### Example 1

- Consumer profile: `Fixture Reader`
- Fixture: [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- Capability: `read-canonical-id`
- Behavior: reads `rec-water-mill-design-asset-pack` from the frozen archive record and reports it as the preserved canonical ID.
- Why valid: the capability is read-only, keeps the canonical path as source of truth, and does not imply any new authority.

### Example 2

- Consumer profile: `Fixture Comparator`
- Fixture: [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- Capability: `compare-boundary-language`
- Behavior: compares packet/world-state boundary wording against the frozen packet expectation and emits a mismatch note only if the wording weakens.
- Why valid: the capability is allowed with stop rules and stays inside read-only packet-boundary comparison.

### Example 3

- Consumer profile: `Fixture Export Consumer`
- Fixture: [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)
- Capability: `export-summary`
- Behavior: exports a non-authoritative summary noting that the review decision remains review-only and does not imply execution approval.
- Why valid: the summary remains downstream only and preserves canonical-source, packet-boundary, and approval-boundary discipline.

## Boundary-Edge But Still Conformant Use

### Example 4

- Consumer profile: `Fixture Diagnostics Consumer`
- Fixture: [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- Capability: `derive-diagnostics`
- Behavior: emits an external warning that a copied provenance paragraph now sounds stronger than the frozen Water Mill design-asset boundary allows.
- Why still conformant: diagnostics remain non-authoritative, do not rewrite the fixture, and explicitly preserve Water Mill as design-asset-derived context only.

### Example 5

- Consumer profile: `Fixture Reader`
- Fixture: [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)
- Capability: `read-boundary-language`
- Behavior: reads `approved_for_next_gate` together with surrounding boundary text to confirm it remains review-only.
- Why still conformant: the capability stays read-only and stops short of turning review approval into runtime authority.

## Stop-And-Report Cases

### Example 6

- Consumer profile: `Fixture Comparator`
- Fixture: [`promoted-agent-brief-v1.md`](C:/SmallVille/docs/examples/promoted-agent-brief-v1.md)
- Capability: `compare-canonical-id`
- Trigger: the brief ID no longer matches `brief-review-analyst-bootstrap-v1`.
- Required response: stop and report canonical ID drift.
- Why stop is required: immutable fixture identity has been weakened.

### Example 7

- Consumer profile: `Fixture Export Consumer`
- Fixture: [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- Capability: `export-summary`
- Trigger: the exported summary starts describing the packet as runtime world state.
- Required response: stop and report packet/world-state confusion.
- Why stop is required: a derived output is trying to override the packet boundary.

### Example 8

- Consumer profile: `Fixture Diagnostics Consumer`
- Fixture: [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- Capability: `derive-diagnostics`
- Trigger: diagnostics language starts treating Water Mill imagery as observed built authority rather than design-asset-derived context.
- Required response: stop and report Water Mill authority inflation.
- Why stop is required: design-asset boundaries have been broken.

### Example 9

- Consumer profile: `Fixture Export Consumer`
- Fixture: [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)
- Capability: `export-diagnostics`
- Trigger: an exported diagnostic note is presented as the new canonical source for downstream consumers.
- Required response: stop and report derived-output replacement.
- Why stop is required: canonical paths must remain the sole source of truth.

## Clearly Non-Conformant Capability Use

### Example 10

- Proposed capability: `bind-to-runtime`
- Fixture: [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- Why non-conformant: runtime coupling is forbidden and packet/world-state separation is broken.

### Example 11

- Proposed capability: `mutate-fixture`
- Fixture: [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- Why non-conformant: immutable canonical fixtures may not be rewritten, repaired, or normalized in place.

### Example 12

- Proposed capability: `replace-canonical-source`
- Fixture: [`promoted-agent-brief-v1.md`](C:/SmallVille/docs/examples/promoted-agent-brief-v1.md)
- Why non-conformant: a mirror, summary, or derived note may not replace the canonical fixture at its canonical path.

### Example 13

- Proposed capability: `promote-inferred-to-observed`
- Fixture: [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- Why non-conformant: Water Mill design-asset-derived context may not be promoted into observed built authority without accepted evidence.

### Example 14

- Proposed capability context: any approved capability used with a second dossier added to the chain
- Why non-conformant: single-dossier continuity is broken once a second dossier is silently introduced.
