# Review Surface Drill Outcome Consumption Examples v1

## Purpose

These examples illustrate bounded read-only consumption of already-derived drill outcomes. They are illustrative only. They are not tooling behavior, validator behavior, schema examples, workflow-engine behavior, runtime behavior, routing behavior, UI design, or canonical replacement behavior.

Drill outcome consumption may read, preserve, and interpret already-derived drill outcomes only and may not derive a new drill outcome family.

The hard distinctions remain explicit throughout:

- drill outcome = pre-decision artifact
- drill outcome consumption = bounded read-only downstream use of a pre-decision artifact
- review decision = approval-boundary artifact

## Valid Consumed `drill-summary`

- Consumer profile: `Fixture Reader`
- Consumed outcome family: `drill-summary`
- Canonical fixture in view: `docs/examples/promoted-archive-record-v1.md`
- Consumed meaning: the `drill-summary` is used as a review aid only to preserve post-drill summary context before the canonical archive-record text is read at its canonical path.
- Required preservation:
  - canonical path remains visible and remains the source of truth
  - the consumed outcome remains pre-decision only
  - Water Mill remains design-asset-derived context only
  - single-dossier continuity remains explicit
- Forbidden interpretation:
  - treating the `drill-summary` as completed review, approval, or decision
  - treating the consumed outcome as canonical replacement

## Valid Consumed `drill-pause-note`

- Consumer profile: `Fixture Comparator`
- Consumed outcome family: `drill-pause-note`
- Canonical fixture in view: `docs/examples/promoted-archive-record-v1.md`
- Consumed meaning: the `drill-pause-note` is used to preserve drift-pause context around Water Mill boundary wording without converting that pause into queue, backlog, or deferred workflow semantics.
- Required preservation:
  - the consumed outcome remains non-authoritative and pre-decision only
  - review attention increases, but no queued work or correction authority is created
  - Water Mill remains design-asset-derived context only
  - canonical archive-record text remains source of truth
- Forbidden interpretation:
  - treating the `drill-pause-note` as workflow state
  - treating the `drill-pause-note` as approved correction

## Valid Consumed `drill-stop-outcome`

- Consumer profile: `Fixture Diagnostics Consumer`
- Consumed outcome family: `drill-stop-outcome`
- Canonical fixture in view: `docs/examples/promoted-review-decision-v1.md`
- Consumed meaning: the `drill-stop-outcome` preserves that a bounded drill encountered an unresolved hard stop around reading review approval as execution approval.
- Required preservation:
  - the unresolved hard stop remains explicit
  - the hard stop remains unresolved unless human review separately resolves it
  - When a consumed `drill-stop-outcome` is in scope, downstream consumption may continue only for the purpose of preserving halt-aware review context and may not continue on the flagged subject as though the hard stop were resolved.
  - the canonical review decision remains an approval-boundary artifact only
  - Reference to a promoted review decision fixture as subject matter does not authorize paraphrase as substitute approval text.
  - downstream reading does not simulate review-decision replacement
- Forbidden interpretation:
  - treating the `drill-stop-outcome` as advisory only
  - treating the `drill-stop-outcome` as resolved, cleared, or execution-ready

## Valid Consumed `drill-handoff-note`

- Consumer profile: `Fixture Export Consumer`
- Consumed outcome family: `drill-handoff-note`
- Canonical fixture in view: `docs/examples/promoted-agent-brief-v1.md`
- Consumed meaning: the `drill-handoff-note` is consumed as human handoff context only so a later reader can preserve the pre-decision review framing of the bounded drill.
- Required preservation:
  - canonical path remains visible and remains the source of truth
  - handoff remains human handoff only
  - packet-linked context remains interpretive only and not runtime world state
  - single-dossier continuity remains explicit
- Forbidden interpretation:
  - treating the `drill-handoff-note` as execution handoff
  - treating the `drill-handoff-note` as workflow routing, transfer state, or downstream clearance

## Boundary-Edge But Still Acceptable Example

- Consumer profile: `Fixture Export Consumer`
- Consumed outcome family: `drill-handoff-note`
- Canonical fixture in view: `docs/examples/promoted-derived-packet-v1.md`
- Situation: the consumed `drill-handoff-note` preserves human handoff context while also preserving unresolved hard-stop wording tied to packet/world-state confusion.
- Why still acceptable:
  - the outcome remains pre-decision only
  - the unresolved hard stop remains explicit and is not softened
  - packet content remains interpretive artifact only and not runtime world state
  - canonical packet text remains source of truth at its canonical path
- Immediate requirement:
  - downstream reading may preserve the human handoff only while still treating the hard stop as unresolved unless human review separately resolves it

## Clearly Non-Conformant Examples

### Non-conformant Consumed `drill-summary`

- Consumer profile: `Fixture Reader`
- Consumed outcome family: `drill-summary`
- Failure: the reader treats the consumed `drill-summary` as proof that review is complete and approval is ready.
- Why non-conformant:
  - collapses a pre-decision artifact into approval handling
  - weakens the drill-outcome versus review-decision distinction
  - implies approval inflation

### Non-conformant Consumed `drill-pause-note`

- Consumer profile: `Fixture Comparator`
- Consumed outcome family: `drill-pause-note`
- Failure: the comparator records the consumed pause as queued review work waiting in a later backlog.
- Why non-conformant:
  - turns preserved pause context into workflow state
  - introduces queue and backlog semantics
  - exceeds bounded read-only consumption

### Non-conformant Consumed `drill-stop-outcome`

- Consumer profile: `Fixture Diagnostics Consumer`
- Consumed outcome family: `drill-stop-outcome`
- Failure: the consumer reads the preserved hard stop but still describes the flagged subject as cleared for downstream use.
- Why non-conformant:
  - softens unresolved hard-stop meaning
  - treats the consumed outcome as downstream clearance
  - risks execution-authority drift

### Non-conformant Consumed `drill-handoff-note`

- Consumer profile: `Fixture Export Consumer`
- Consumed outcome family: `drill-handoff-note`
- Failure: the consumer describes the handoff as a transfer into the next execution lane and marks it ready for routing.
- Why non-conformant:
  - turns human handoff into execution handoff
  - introduces routing and transfer semantics
  - exceeds pre-decision boundaries

### Non-conformant Boundary Handling

- Consumer profile: `Fixture Reader`
- Consumed outcome family: `drill-summary`
- Failure: the consumed outcome hides the canonical path, restates Water Mill as settled built fact, treats packet language as operational state, and pulls in a second dossier for convenience.
- Why non-conformant:
  - weakens canonical source-of-truth discipline
  - inflates Water Mill beyond design-asset-derived context
  - breaks packet/world-state separation
  - introduces second-dossier expansion
