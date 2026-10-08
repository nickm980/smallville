# Conformance Validator Result Semantics v1

## Operational Read

- Status: `documentation-only`
- Authority: none granted; this document records existing semantics, not new policy
- Frozen baseline HEAD: `419952b`
- Validator source: `FixtureConformanceValidator.java`
- Result type: `ConformanceValidationResult.java`
- Classification enum: `FixtureConformanceClassification.java`

## Purpose

This document defines the operational semantics of `ConformanceValidationResult` for consumers of the validation API. It documents the meaning of each result state including `Optional.empty()`, the three frozen classifications, and the behavioral contracts of the convenience methods.

This document does not change validator behavior, expand vocabulary, or authorize implementation of consuming logic.

## Result Structure

`ConformanceValidationResult` is a Java record with four fields:

| Field | Type | Semantics |
|-------|------|-----------|
| `profile` | `FixtureConsumerProfile` | The consumer profile being validated. Non-null. |
| `fixtureRole` | `CanonicalFixtures.FixtureRole` | The fixture role being consumed. Non-null. |
| `classification` | `Optional<FixtureConformanceClassification>` | The frozen classification, if any. Non-null Optional. |
| `frozenSourceReference` | `String` | Human-readable reference to the frozen source of the rule. Non-null. |

All four fields are validated non-null at construction time.

## Classification Semantics

### `Optional.empty()` — No Frozen Rule

- **Meaning**: No explicitly frozen conformance rule exists for this profile-fixture combination in `FixtureConformanceValidator.initializeRules()`.
- **What it is NOT**: Not a hidden fourth classification. Not an inferred permission. Not an inferred prohibition. Not a deferral. Not a "soft allow." Not a "pending review."
- **Validator source reference**: When no rule is found, `frozenSourceReference` is set to `"No explicitly frozen rule"`.
- **Consumer guidance**: Consumers receiving `Optional.empty()` must not infer any conformance outcome. The interaction has no frozen policy. Any consumer behavior against an unresolved cell is outside the scope of frozen governance.
- **Governance position**: Per `ueia-unresolved-cell-strategy-evaluation-v1.md`, unresolved cells remain blocked by authority absence and require explicit new frozen authority before any classification can be assigned.

### `ALLOWED` — Unconditional Conformance

- **Meaning**: Consumer behavior against this fixture is allowed without constraints.
- **Enum label**: `"allowed"`
- **Current scope**: Applies to exactly 1 matrix cell: FIXTURE_COMPARATOR + AGENT_BRIEF (Example 2).
- **Operational semantics**: The consumer may exercise its profile-defined capabilities against this fixture without stop-and-report triggers.
- **Frozen rationale (Example 2)**: "comparison stays inside canonical text, preserves single-dossier continuity, does not mutate"
- **Boundaries preserved**: Single-dossier continuity, no mutation, comparison bounded to canonical text.

### `ALLOWED_WITH_STOP_RULES` — Conditional Conformance

- **Meaning**: Consumer behavior against this fixture is allowed, but explicit stop-and-report rules apply. The consumer must halt and report if specific boundary conditions are triggered.
- **Enum label**: `"allowed-with-stop-rules"`
- **Current scope**: Applies to 7 matrix cells (Examples 1, 3, 4, 5, 6, 7, 8).
- **Operational semantics**: The consumer may exercise its profile-defined capabilities against this fixture, but must stop and report when the frozen stop condition for that cell is triggered.
- **Stop conditions by example**:
  - Ex1 (Reader + Archive Record): reader remains read-only, preserves Water Mill as design-asset-derived context only
  - Ex3 (Export Consumer + Review Decision): export remains downstream only, preserves approval boundary
  - Ex4 (Diagnostics Consumer + Derived Packet): diagnostics remain external and non-authoritative, consumer stops short of mutation
  - Ex5 (Reader + Review Decision): reader preserves approval boundary instead of inflating it into execution authority
  - Ex6 (Comparator + Archive Record): stop-and-report required if canonical ID or provenance link is changed/dropped
  - Ex7 (Export Consumer + Derived Packet): stop-and-report required if export frames packet as runtime world state
  - Ex8 (Diagnostics Consumer + Agent Brief): stop-and-report required if diagnostics flatten open-question/inferred content into observed

### `NOT_ALLOWED` — Prohibited Interaction

- **Meaning**: Consumer behavior is not allowed under any circumstances for this interaction pattern.
- **Enum label**: `"not-allowed"`
- **Current scope**: Established by boundary Examples 9–12 (not as matrix cells but as cross-cutting prohibitions).
- **Operational semantics**: The consumer must not perform the described behavior. There is no stop-and-report; the behavior is categorically forbidden.
- **Prohibited patterns (from examples)**:
  - Ex9: Binding DerivedPacket to live runtime state (runtime coupling)
  - Ex10: Replacing ArchiveRecord with mirror and treating as canonical (canonical replacement)
  - Ex11: Using Water Mill imagery as direct proof of observed built reality (design-asset boundary violation)
  - Ex12: Merging second dossier into export summaries (single-dossier continuity violation)

## Convenience Method Semantics

### `hasFrozenRule()`

- Returns `true` if `classification.isPresent()`
- Returns `false` if `classification.isEmpty()` (no frozen rule)

### `isAllowed()`

- Returns `true` if classification is `ALLOWED` or `ALLOWED_WITH_STOP_RULES`
- Returns `false` if classification is `NOT_ALLOWED`
- Returns `false` if classification is `Optional.empty()` (absence of rule is not permission)

### `isNotAllowed()`

- Returns `true` if classification is `NOT_ALLOWED`
- Returns `false` if classification is `ALLOWED` or `ALLOWED_WITH_STOP_RULES`
- Returns `false` if classification is `Optional.empty()` (absence of rule is not prohibition)

### Interpretation Table

| `classification` | `hasFrozenRule()` | `isAllowed()` | `isNotAllowed()` | `requiresStopRules()` |
|---|---|---|---|---|
| `Optional.of(ALLOWED)` | true | true | false | false |
| `Optional.of(ALLOWED_WITH_STOP_RULES)` | true | true | false | true |
| `Optional.of(NOT_ALLOWED)` | true | false | true | false |
| `Optional.empty()` | false | false | false | false |

Note: `requiresStopRules()` returns `true` only for `ALLOWED_WITH_STOP_RULES`. It is derived from `classification.map(c -> c == ALLOWED_WITH_STOP_RULES).orElse(false)`.

## What This Document Does Not Authorize

- No validator code changes
- No new classifications or vocabulary expansion
- No consuming-seam implementation
- No runtime or integration behavior
- No inference of what unresolved cells should classify as
