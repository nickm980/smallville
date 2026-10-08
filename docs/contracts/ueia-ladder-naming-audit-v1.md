# UEIA Ladder Naming Audit v1

## Purpose

This note audits the current UEIA ladder naming pattern so the frozen scaffold remains discussable and resumable without changing canonical filenames, canonical paths, or authority.

## Current Naming Pattern

The ladder currently follows a stable naming pattern built from stacked noun phrases:

- domain anchor: `UEIA`
- seam subject: `diagnostic output`, `human review intake`
- presentation or downstream layer: `review surface`, `drill`, `outcome`, `consumption`
- document role: `spec`, `types`, `fields`, `boundaries`, `matrix`, `examples`, `note`, `brief`
- version suffix: `v1`

This pattern remains coherent because each filename still describes:

- the subject being governed
- the seam position in the ladder
- the document role inside the seam
- the frozen version

## Where Naming Remains Coherent

Naming remains strong in these areas:

- early ladder foundations such as canonical fixture, consumer profile, conformance, and capability crosswalk docs
- diagnostic-output layers where subject, seam, and document role remain compact
- the human-review-intake branch where the branch anchor remains consistent across intake, consumption, review surface, drill, outcome, and outcome consumption

## Where Names Are Becoming Too Long

The longest names now cluster in the deepest human-review-intake branch:

- `ueia-human-review-intake-review-surface-drill-outcome-consumption-spec-v1.md`
- `human-review-intake-review-surface-drill-outcome-consumption-boundaries-v1.md`
- related examples and interpretation files in the same branch

The naming is still accurate, but the cost is increasing in:

- conversational readability
- quick human scanning
- handover summaries
- prompt brevity for fresh sessions

## Safe Shorthand Recommendations For Discussion Only

These shorthand forms are safe for discussion only:

- `HRI` = `Human Review Intake`
- `RS` = `Review Surface`
- `Drill Outcome` = `Drill Outcome`
- `Outcome Cons.` = `Outcome Consumption`
- `HRI RS Drill Outcome Cons.` = `Human Review Intake Review Surface Drill Outcome Consumption`
- `Latest HRI seam` = the latest completed human-review-intake branch seam when the exact canonical filename is also named nearby

Safe use rules:

- bind shorthand to the canonical filename the first time it appears in a note, prompt, or handover
- use shorthand in prose only after the canonical form is already established
- never use shorthand as a filename
- never use shorthand as a canonical path token
- never let shorthand replace commit references, canonical file references, or seam titles in freeze records

## Canonical Filename And Path Preservation Rules

- Canonical filenames remain the source of truth for frozen repo artifacts.
- Canonical paths remain the source of truth for locating the frozen artifact set.
- Safe shorthand may assist discussion, but it may not replace canonical filenames in repo docs, commit records, or freeze references.
- If shorthand and canonical naming ever conflict, canonical filenames and canonical paths win immediately.
- This audit does not authorize renames, path changes, or filename shortening inside the repo.

## Naming Assessment

The current naming system is still coherent enough to continue safely.

The immediate risk is not broken naming logic; it is rising traversal cost. That cost is best managed by:

- a ladder phase map
- a handover bootstrap
- discussion-only shorthand
- continued canonical-path discipline

## Boundary Note

This naming audit does not create a new authority seam. It is descriptive only and may not authorize file mutation, renaming, runtime behavior, workflow behavior, routing behavior, UI behavior, or tooling behavior.
