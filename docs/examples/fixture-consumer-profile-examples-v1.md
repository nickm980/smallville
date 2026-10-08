# Fixture Consumer Profile Examples v1

## Purpose

This note gives concrete examples of acceptable and rejected consumer behavior for the approved canonical fixture profiles.

These examples are descriptive only and do not authorize implementation.

## Acceptable Reader Behavior

### Example 1

- Profile: `Fixture Reader`
- Input: [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- Behavior: reads `record_id`, `source_type`, provenance wording, and Water Mill design-asset boundary language.
- Why acceptable: the reader remains read-only, preserves Water Mill as design-asset-derived context only, and does not elevate designed structures to observed real-world authority.

### Example 2

- Profile: `Fixture Reader`
- Input: [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- Behavior: reads `packet_id`, `packet_status`, and boundary wording stating the packet is not runtime world state.
- Why acceptable: the reader checks the preserved packet boundary without coupling the packet to runtime.

## Acceptable Comparison Behavior

### Example 3

- Profile: `Fixture Comparator`
- Inputs:
  - [`promoted-agent-brief-v1.md`](C:/SmallVille/docs/examples/promoted-agent-brief-v1.md)
  - expected canonical brief ID `brief-review-analyst-bootstrap-v1`
- Behavior: compares the preserved `brief_id`, `packet_id`, and non-authority wording against expected frozen values.
- Why acceptable: comparison stays inside the canonical fixture text and produces drift notes only.

### Example 4

- Profile: `Fixture Comparator`
- Inputs:
  - [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)
  - approved approval-boundary expectations
- Behavior: checks whether `approved_for_next_gate` is still framed as next-gate review only and not execution approval.
- Why acceptable: the comparison enforces approval-boundary integrity without changing the source.

## Acceptable Diagnostics And Export Behavior

### Example 5

- Profile: `Fixture Diagnostics Consumer`
- Inputs:
  - the four canonical fixtures
  - approved canonical fixture contract notes
- Behavior: emits a warning if a later copy weakens provenance wording or removes explicit Water Mill design-asset boundary language.
- Why acceptable: diagnostics remain external, non-authoritative outputs and do not rewrite fixture source.

### Example 6

- Profile: `Fixture Export Consumer`
- Inputs:
  - comparison notes for the promoted packet
  - diagnostic notes for the promoted brief
- Behavior: exports a plain-text summary report describing preserved canonical IDs, packet/world-state separation, and single-dossier continuity.
- Why acceptable: the export is downstream only and does not create an authoritative fixture substitute.

## Rejected Consumer Behavior

### Example 7

- Proposed behavior: rewrite [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md) to make packet language easier for runtime ingestion.
- Why rejected: this mutates a canonical fixture and treats the packet as a runtime-facing artifact.

### Example 8

- Proposed behavior: convert Water Mill design-asset observations into observed built-fact statements because the images look detailed enough.
- Why rejected: designed structures remain design-asset-derived context only unless accepted evidence establishes observed real-world authority.

### Example 9

- Proposed behavior: merge a second dossier into comparison summaries so diagnostics can be more comprehensive.
- Why rejected: this breaks single-dossier continuity and widens scope beyond the approved canonical fixture chain.

### Example 10

- Proposed behavior: export a JSON mirror of the promoted fixtures and treat it as the new canonical source for tests.
- Why rejected: canonical fixtures remain the frozen file text at the canonical paths; derived exports cannot replace them.

## Stop-And-Report Cases

### Case 1

- Trigger: canonical ID drift is detected in any promoted specimen.
- Required response: stop and report drift; do not auto-repair the fixture.

### Case 2

- Trigger: packet language is interpreted as runtime world state or world mutation authority.
- Required response: stop and report boundary failure; do not continue downstream consumption.

### Case 3

- Trigger: Water Mill content is treated as real-world built authority without accepted supporting evidence.
- Required response: stop and report evidence-boundary failure.

### Case 4

- Trigger: inferred or open-question content is silently promoted to observed during normalization or summarization.
- Required response: stop and report fact-status failure.

### Case 5

- Trigger: a second dossier is introduced into any consumer profile, comparison baseline, or exported summary.
- Required response: stop and report single-dossier boundary failure.
