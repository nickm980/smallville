# Human Review Intake Consumption Matrix v1

## Purpose

This note defines which approved consumer profiles may consume which approved human-review intake families inside the frozen UEIA governance ladder.

## Status Vocabulary

Only these classifications are permitted in this matrix:

- `allowed`
- `allowed-with-stop-rules`
- `not-allowed`

## Consumption Matrix

| Consumer profile | `review-intake-summary` | `review-intake-pause-note` | `review-intake-stop-note` | `review-intake-handoff-note` |
| --- | --- | --- | --- | --- |
| `Fixture Reader` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `Fixture Comparator` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `Fixture Diagnostics Consumer` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `Fixture Export Consumer` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |

## Shared Matrix Boundaries

All `allowed-with-stop-rules` classifications remain subordinate to the frozen:

- canonical fixture authority
- review-decision approval boundaries
- Water Mill design-asset-derived handling
- packet/world-state separation
- single-dossier continuity
- unresolved hard-stop preservation

All matrix classifications remain:

- pre-decision only
- read-only only
- non-authoritative
- non-replacing

No matrix classification authorizes:

- review-decision handling
- approval handling
- workflow handling
- execution handling
- control handling
- canonical replacement

## Shared Stop-Rule Reading

Any consumer profile marked `allowed-with-stop-rules` must still preserve:

- that a consumed `review-intake-stop-note` remains unresolved unless human review separately resolves it
- that downstream consumption may continue only for the purpose of preserving halt-aware human review context and may not continue on the flagged subject as though the hard stop were resolved
- that a consumed `review-intake-handoff-note` may not become execution handoff, routing logic, assignment state, transfer state, downstream clearance, or review-decision proxy

## Explicit Stop Conditions

Stop if matrix drafting:

- widens scope beyond the four approved consumer profiles
- widens scope beyond the four approved intake families
- introduces a classification other than `allowed`, `allowed-with-stop-rules`, or `not-allowed`
- grants `allowed` where stop preservation is still required
- weakens pre-decision versus review-decision separation
- weakens unresolved hard-stop semantics
- implies runtime authority, mutation authority, execution authority, approval inflation, canonical replacement, or workflow-state inflation
