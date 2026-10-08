# Fixture Consumer Output Types v1

## Purpose

This document defines the approved output families for read-only diagnostic outputs derived from canonical fixtures by approved consumer profiles. These output families are descriptive only and do not define formats, schemas, packaging, transport, or implementation behavior.

## Output Families In Scope

- `summary`
- `drift-note`
- `stop-report`
- `diagnostics-bundle`
- `export-summary`

`summary` is a derived read-only output family and is not inherently an exported artifact.

`export-summary` is a derived read-only output family intended for downstream export or handoff, but remains non-authoritative and non-replacing.

`drift-note` reports drift or weakening only.

`stop-report` reports a triggered hard boundary and may not authorize continuation.

`diagnostics-bundle` is a descriptive grouped-output family only and is not a packaging, archive, file-format, or transport specification.

## `summary`

**Purpose**

Provide a concise derived read-only account of a canonical fixture as read through an approved consumer profile and approved capability context.

**Allowed inputs**

- One in-scope canonical fixture at its canonical path
- One in-scope consumer profile
- One or more allowed read-only capabilities from the frozen capability layer

**Allowed contents**

- Canonical fixture identification
- Provenance-aware summary language
- Boundary-preserving notes
- Fact-status-sensitive notes

**Forbidden contents**

- Rewrite of canonical fixture text
- Runtime instructions
- Mutation instructions
- Execution approval language
- Packaging or transport instructions

**Authority level**

Derived only, non-authoritative, review-oriented

**Replacement boundary**

May not replace canonical source text or function as a canonical mirror

**Runtime boundary**

May not imply runtime state, runtime action, or runtime approval

**Mutation boundary**

May not authorize mutation, rewrite, or inferred-to-observed promotion

## `drift-note`

**Purpose**

Record observed drift, weakened wording, boundary slippage, or provenance erosion relative to canonical fixture text and frozen governance layers.

**Allowed inputs**

- One or more in-scope canonical fixtures at canonical paths
- One in-scope consumer profile
- Allowed read-only comparison or diagnostic capabilities

**Allowed contents**

- Drift description
- Boundary-at-risk description
- Provenance weakening notes
- Stop-rule references where applicable

**Forbidden contents**

- Canonical correction by replacement
- Repair-through-rewrite behavior
- Runtime or execution guidance
- Authority inflation

**Authority level**

Derived only, non-authoritative, review-oriented

**Replacement boundary**

May not replace the canonical fixture or silently normalize it

**Runtime boundary**

May not imply runtime response or runtime state

**Mutation boundary**

May not authorize mutation, rewrite, or silent repair

## `stop-report`

**Purpose**

Record a triggered hard boundary that requires stop-and-report behavior under the frozen conformance and stop-rule layers.

**Allowed inputs**

- One or more in-scope canonical fixtures at canonical paths
- One in-scope consumer profile
- Allowed read-only capabilities where a stop condition was encountered

**Allowed contents**

- Triggered boundary statement
- Stop-rule reference
- Observed issue description
- Review-only follow-up guidance

**Forbidden contents**

- Continuation approval
- Execution approval
- Runtime instructions
- Canonical rewrite
- Boundary softening language

**Authority level**

Derived only, non-authoritative, stop-and-report oriented

**Replacement boundary**

May not replace canonical text or create a substitute decision layer

**Runtime boundary**

May not imply runtime enforcement, runtime control, or runtime world state

**Mutation boundary**

May not authorize fixture mutation or downstream rewrite

## `diagnostics-bundle`

**Purpose**

Group multiple read-only diagnostic outputs into one descriptive diagnostic family for review and audit context.

**Allowed inputs**

- One or more in-scope canonical fixtures at canonical paths
- One in-scope consumer profile
- One or more allowed output families produced under approved read-only capabilities

**Allowed contents**

- Ordered collection of summaries, drift-notes, or stop-reports
- Shared provenance framing
- Shared boundary framing
- Cross-reference notes that remain non-authoritative

**Forbidden contents**

- Packaging instructions
- Archive specification
- File-format specification
- Transport specification
- Canonical replacement language
- Runtime control or execution language

**Authority level**

Derived only, non-authoritative, grouped for review only

**Replacement boundary**

May not replace canonical fixtures or operate as an authoritative mirror set

**Runtime boundary**

May not imply runtime coupling, runtime state, or runtime action

**Mutation boundary**

May not authorize mutation, rewrite, or canonical consolidation

## `export-summary`

**Purpose**

Provide a bounded derived read-only summary intended for downstream export or handoff while preserving the same non-authoritative and non-replacing boundaries as all other output families.

**Allowed inputs**

- One in-scope canonical fixture at its canonical path
- One in-scope consumer profile
- Allowed read-only capabilities including export-oriented capability use from the frozen capability layer

**Allowed contents**

- Canonical fixture identification
- Provenance-aware summary language
- Boundary notes needed for safe downstream reading
- Review-oriented caveats

**Forbidden contents**

- Canonical substitute text
- Runtime-binding language
- Mutation instructions
- Execution instructions
- Authority-softening language

**Authority level**

Derived only, non-authoritative, export-oriented, review-oriented

**Replacement boundary**

May not replace canonical source text or become a downstream canonical mirror

**Runtime boundary**

May not imply runtime ingestion, runtime state, runtime action, or runtime approval

**Mutation boundary**

May not authorize mutation, rewrite, or inferred-to-observed promotion
