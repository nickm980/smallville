# Fixture Consumer Capability Stop Overlays v1

## Purpose

This note maps the stop-and-report overlays from the frozen conformance layer onto capability contexts in the v1 capability crosswalk.

These overlays are descriptive only. They do not implement enforcement behavior.

## Overlay 1: Canonical ID Drift

- Capability contexts:
  - `Fixture Comparator` + any canonical fixture + `compare-canonical-id`
  - `Fixture Diagnostics Consumer` + any canonical fixture + `compare-canonical-id`
  - `Fixture Comparator` or `Fixture Diagnostics Consumer` + any canonical fixture + `derive-drift-notes`
- Trigger summary: the preserved canonical ID no longer matches the frozen promoted identity.
- Required response: stop and report canonical ID drift.

## Overlay 2: Provenance Weakening

- Capability contexts:
  - any profile with `read-provenance-language`
  - `Fixture Comparator` or `Fixture Diagnostics Consumer` + any canonical fixture + `compare-provenance-language`
  - `Fixture Export Consumer` + any canonical fixture + `export-summary` or `export-diagnostics`
- Trigger summary: provenance wording is removed, softened, detached from the promoted chain, or reframed as stronger authority than the frozen source supports.
- Required response: stop and report provenance weakening.

## Overlay 3: Packet/World-State Confusion

- Capability contexts:
  - any profile + promoted derived packet + `read-boundary-language`
  - `Fixture Comparator` or `Fixture Diagnostics Consumer` + promoted derived packet + `compare-boundary-language`
  - `Fixture Export Consumer` + promoted derived packet + `export-summary` or `export-diagnostics`
- Trigger summary: packet wording is treated as runtime world state, world-building authority, or scene mutation authority.
- Required response: stop and report packet/world-state boundary failure.

## Overlay 4: Water Mill Authority Inflation

- Capability contexts:
  - any profile + promoted archive record + `read-provenance-language`
  - any profile + promoted archive record + `read-boundary-language`
  - `Fixture Comparator` or `Fixture Diagnostics Consumer` + promoted archive record + `compare-provenance-language` or `compare-boundary-language`
  - `Fixture Export Consumer` + promoted archive record + `export-summary` or `export-diagnostics`
- Trigger summary: Water Mill design-asset-derived context is promoted to observed built authority without accepted evidence.
- Required response: stop and report design-asset boundary failure.

## Overlay 5: Inferred-To-Observed Promotion

- Capability contexts:
  - any profile + any fixture with `read-fact-status-language`
  - `Fixture Comparator` or `Fixture Diagnostics Consumer` + applicable fixtures + `compare-boundary-language`
  - `Fixture Diagnostics Consumer` + applicable fixtures + `derive-diagnostics` or `derive-drift-notes`
  - `Fixture Export Consumer` + applicable fixtures + `export-summary`
- Trigger summary: inferred, open-question, or deferred content is flattened into observed or otherwise inflated certainty.
- Required response: stop and report fact-status failure.

## Overlay 6: Second-Dossier Introduction

- Capability contexts:
  - any profile + any fixture + `read-provenance-language`
  - `Fixture Comparator` or `Fixture Diagnostics Consumer` + any fixture + `compare-provenance-language`
  - `Fixture Export Consumer` + any fixture + `export-summary` or `export-diagnostics`
- Trigger summary: a second dossier, alternate operational dossier, or extra canonical source is introduced into the approved chain.
- Required response: stop and report single-dossier boundary failure.

## Overlay 7: Derived-Output Replacement

- Capability contexts:
  - `Fixture Comparator` or `Fixture Diagnostics Consumer` + any fixture + `derive-drift-notes` or `derive-diagnostics`
  - `Fixture Export Consumer` + any fixture + `export-summary` or `export-diagnostics`
- Trigger summary: a derived note, summary, or diagnostics export is presented as a canonical replacement.
- Required response: stop and report source-of-truth replacement failure.

## Overlay 8: Rewrite-Disguised-As-Normalization

- Capability contexts:
  - any profile + any fixture + any approved capability when the proposed use writes back transformed content
- Trigger summary: reading, comparison, derivation, or export behavior rewrites source wording, repairs missing sections, or writes back normalized content to the canonical fixture.
- Required response: stop and report hidden mutation failure.

## Overlay 9: Runtime Coupling

- Capability contexts:
  - any profile + any fixture + any approved capability when the proposed use binds to runtime state or runtime-facing ingestion
- Trigger summary: canonical fixtures or derived outputs are coupled to runtime state, runtime harness inputs, or runtime-facing ingestion.
- Required response: stop and report runtime-coupling failure.

## Overlay 10: Review Decision Treated As Execution Approval

- Capability contexts:
  - any profile + promoted review decision + `read-boundary-language`
  - `Fixture Comparator` or `Fixture Diagnostics Consumer` + promoted review decision + `compare-boundary-language`
  - `Fixture Export Consumer` + promoted review decision + `export-summary` or `export-diagnostics`
- Trigger summary: `approved_for_next_gate` or related approval language is interpreted as runtime, implementation, or execution approval.
- Required response: stop and report approval-boundary failure.

## Overlay Discipline

Stop-overlay use may:
- identify the triggering condition
- identify the consumer profile, fixture type, and capability involved
- identify the weakened boundary
- recommend halting downstream consumption

Stop-overlay use may not:
- rewrite canonical fixtures
- repair missing evidence
- replace canonical source text
- authorize continued use after a hard boundary failure
