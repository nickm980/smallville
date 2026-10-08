# Human Review Intake Consumption Examples v1

## Purpose

These examples illustrate bounded read-only consumption of already-derived human-review intake artifacts. They are illustrative only. They are not tooling behavior, validator behavior, schema examples, workflow-engine behavior, runtime behavior, routing logic, UI design, or canonical replacement behavior.

Consuming a human-review intake artifact means bounded read-only downstream use of an already-derived pre-decision human-review-intake artifact.

Human-review-intake consumption remains pre-decision only and may not become review decision handling, approval handling, workflow handling, execution handling, or control handling.

Human-review-intake consumption may read, preserve, and interpret already-derived human-review intake artifacts only and may not derive a new intake family.

A consumed human-review intake artifact may preserve review-only context, drift-pause context, unresolved hard-stop context, or human-attention handoff context only.

The hard distinctions remain explicit throughout:

- drill outcome = pre-decision artifact
- drill outcome consumption = bounded read-only downstream use of a pre-decision artifact
- human review intake = bounded pre-decision handoff for human attention only
- review decision = approval-boundary artifact

## Valid Consumed `review-intake-summary`

- Consumer profile: `Fixture Reader`
- Consumed intake family: `review-intake-summary`
- Canonical fixture in view: `docs/examples/promoted-archive-record-v1.md`
- Consumed meaning: the `review-intake-summary` is used as a review aid only to preserve bounded human-attention summary context before the canonical archive-record text is read at its canonical path.
- Required preservation:
  - canonical path remains visible and remains the source of truth
  - the consumed intake artifact remains pre-decision only
  - Water Mill remains design-asset-derived context only
  - single-dossier continuity remains explicit
- Forbidden interpretation:
  - treating the `review-intake-summary` as completed review, approval readiness, or decision readiness
  - treating the consumed intake artifact as canonical replacement

## Valid Consumed `review-intake-pause-note`

- Consumer profile: `Fixture Comparator`
- Consumed intake family: `review-intake-pause-note`
- Canonical fixture in view: `docs/examples/promoted-archive-record-v1.md`
- Consumed meaning: the `review-intake-pause-note` is used to preserve drift-pause context around Water Mill boundary wording without converting that pause into queue semantics, backlog semantics, deferred workflow handling, or correction authority.
- Required preservation:
  - the consumed intake artifact remains non-authoritative and pre-decision only
  - human review attention increases, but no queue semantics or correction authority are created
  - Water Mill remains design-asset-derived context only
  - canonical archive-record text remains source of truth
- Forbidden interpretation:
  - treating the `review-intake-pause-note` as workflow state
  - treating the `review-intake-pause-note` as approved correction

## Valid Consumed `review-intake-stop-note`

- Consumer profile: `Fixture Diagnostics Consumer`
- Consumed intake family: `review-intake-stop-note`
- Canonical fixture in view: `docs/examples/promoted-review-decision-v1.md`
- Consumed meaning: the `review-intake-stop-note` preserves that a human-review intake artifact carried an unresolved hard stop around reading review approval as execution approval.
- Required preservation:
  - the unresolved hard stop remains explicit
  - the hard stop remains unresolved unless human review separately resolves it
  - When a consumed `review-intake-stop-note` is in scope, downstream consumption may continue only for the purpose of preserving halt-aware human review context and may not continue on the flagged subject as though the hard stop were resolved.
  - the canonical review decision remains an approval-boundary artifact only
  - a consumed human-review intake artifact does not simulate review-decision replacement
- Forbidden interpretation:
  - treating the `review-intake-stop-note` as advisory only
  - treating the `review-intake-stop-note` as resolved, cleared, or execution-ready

## Valid Consumed `review-intake-handoff-note`

- Consumer profile: `Fixture Export Consumer`
- Consumed intake family: `review-intake-handoff-note`
- Canonical fixture in view: `docs/examples/promoted-agent-brief-v1.md`
- Consumed meaning: the `review-intake-handoff-note` is consumed as human-attention handoff context only so a later reader can preserve the pre-decision review framing of the bounded intake artifact.
- Required preservation:
  - canonical path remains visible and remains the source of truth
  - handoff remains human-attention handoff only
  - packet-linked context remains interpretive only and not runtime world state
  - single-dossier continuity remains explicit
- Forbidden interpretation:
  - treating the `review-intake-handoff-note` as execution handoff
  - treating the `review-intake-handoff-note` as routing logic, assignment state, transfer state, downstream clearance, or review-decision proxy

## Boundary-Edge But Still Acceptable Example

- Consumer profile: `Fixture Export Consumer`
- Consumed intake family: `review-intake-handoff-note`
- Canonical fixture in view: `docs/examples/promoted-derived-packet-v1.md`
- Situation: the consumed `review-intake-handoff-note` preserves human-attention handoff context while also preserving unresolved hard-stop wording tied to packet/world-state confusion.
- Why still acceptable:
  - the consumed intake artifact remains pre-decision only
  - the unresolved hard stop remains explicit and is not softened
  - packet content remains interpretive artifact only and not runtime world state
  - canonical packet text remains source of truth at its canonical path
- Immediate requirement:
  - downstream reading may preserve the human-attention handoff only while still treating the hard stop as unresolved unless human review separately resolves it

## Clearly Non-Conformant Examples

### Non-conformant Consumed `review-intake-summary`

- Consumer profile: `Fixture Reader`
- Consumed intake family: `review-intake-summary`
- Failure: the reader treats the consumed `review-intake-summary` as proof that review is complete and approval is ready.
- Why non-conformant:
  - collapses a pre-decision artifact into approval handling
  - weakens the intake-consumption versus review-decision distinction
  - implies approval inflation

### Non-conformant Consumed `review-intake-pause-note`

- Consumer profile: `Fixture Comparator`
- Consumed intake family: `review-intake-pause-note`
- Failure: the comparator records the consumed pause as queued review work waiting in a later backlog.
- Why non-conformant:
  - turns preserved pause context into workflow state
  - introduces queue semantics and backlog semantics
  - exceeds bounded read-only intake consumption

### Non-conformant Consumed `review-intake-stop-note`

- Consumer profile: `Fixture Diagnostics Consumer`
- Consumed intake family: `review-intake-stop-note`
- Failure: the consumer reads the preserved hard stop but still describes the flagged subject as cleared for downstream use.
- Why non-conformant:
  - softens unresolved hard-stop meaning
  - treats the consumed intake artifact as downstream clearance
  - risks execution-authority drift

### Non-conformant Consumed `review-intake-handoff-note`

- Consumer profile: `Fixture Export Consumer`
- Consumed intake family: `review-intake-handoff-note`
- Failure: the consumer describes the handoff as a transfer into the next execution lane and marks it ready for routing.
- Why non-conformant:
  - turns human-attention handoff into execution handoff
  - introduces transfer semantics and routing logic
  - exceeds pre-decision boundaries

### Non-conformant Boundary Handling

- Consumer profile: `Fixture Reader`
- Consumed intake family: `review-intake-summary`
- Failure: the consumed intake artifact hides the canonical path, restates Water Mill as settled built fact, treats packet language as operational state, and pulls in a second dossier for convenience.
- Why non-conformant:
  - weakens canonical source-of-truth discipline
  - inflates Water Mill beyond design-asset-derived context
  - breaks packet/world-state separation
  - introduces second-dossier expansion
