# Fixture Consumer Stop Rules v1

## Purpose

This note defines the stop-and-report triggers that govern consumer-profile interaction with canonical fixtures.

These rules are descriptive only. They do not implement enforcement behavior.

## Stop Rule 1: Canonical ID Drift

- Trigger: any consumer detects that a canonical fixture no longer preserves its promoted canonical ID.
- Required response: stop and report canonical ID drift.
- Why it matters: immutable fixture identity has been weakened.

## Stop Rule 2: Missing Provenance Or Weakened Provenance Wording

- Trigger: provenance notes are removed, softened, or detached from the promoted single-dossier chain.
- Required response: stop and report provenance weakening.
- Why it matters: downstream consumers lose the accepted source trail and evidence boundary.

## Stop Rule 3: Packet Treated As Runtime World State

- Trigger: packet language is read, exported, compared, or summarized as though it were runtime world state or scene/world mutation authority.
- Required response: stop and report packet/world-state boundary failure.
- Why it matters: the derived packet is interpretive artifact only.

## Stop Rule 4: Water Mill Treated As Real-World Authority Without Accepted Evidence

- Trigger: Water Mill design-asset-derived context is promoted to observed real-world built authority without accepted supporting evidence.
- Required response: stop and report design-asset boundary failure.
- Why it matters: designed structures remain non-authoritative for observed built status unless separately evidenced.

## Stop Rule 5: Inferred Content Promoted To Observed

- Trigger: inferred, open-question, or deferred content is silently reframed as observed during reading, comparison, diagnostics, export, or normalization.
- Required response: stop and report fact-status failure.
- Why it matters: uncertainty has been erased and evidence discipline is no longer preserved.

## Stop Rule 6: Second Dossier Introduced

- Trigger: any consumer introduces a second dossier, alternative operational dossier, or extra canonical source into the approved chain.
- Required response: stop and report single-dossier boundary failure.
- Why it matters: conformance scope widens beyond the approved canonical chain.

## Stop Rule 7: Derived Output Presented As Canonical Replacement

- Trigger: a summary, diagnostics export, comparison note, or other derived output is presented as a replacement for the canonical fixture at its canonical path.
- Required response: stop and report source-of-truth replacement failure.
- Why it matters: canonical fixtures must remain the sole source of truth.

## Stop Rule 8: Mutation Or Rewrite Behavior Disguised As Normalization

- Trigger: normalization, diagnostics, comparison, or export behavior rewrites source wording, repairs missing sections automatically, or writes back transformed content to canonical fixture files.
- Required response: stop and report hidden mutation failure.
- Why it matters: read-only consumer profiles must not gain mutation authority through indirect behavior.

## Stop Rule 9: Review Decision Treated As Execution Approval

- Trigger: review-decision wording such as `approved_for_next_gate` is interpreted as runtime approval, implementation approval, or execution permission.
- Required response: stop and report approval-boundary failure.
- Why it matters: review decisions remain review-only artifacts inside the canonical chain.

## Stop Rule 10: Runtime Coupling Introduced

- Trigger: any consumer binds canonical fixtures or derived outputs to live runtime state, runtime harness inputs, or runtime-facing ingestion.
- Required response: stop and report runtime-coupling failure.
- Why it matters: the conformance layer is non-executing and read-only.

## Stop-And-Report Output Discipline

Stop reports may:
- identify the triggering condition
- identify the fixture and consumer profile involved
- identify the weakened boundary
- recommend halting downstream consumption

Stop reports may not:
- repair the fixture
- rewrite the fixture
- replace missing evidence
- authorize continued downstream use after a hard boundary failure
