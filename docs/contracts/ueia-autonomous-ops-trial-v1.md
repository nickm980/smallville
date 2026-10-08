# UEIA Autonomous Ops Trial v1

## Operational Read

- Status: `accepted`
- Phase scope: `shadow-mode only`
- Active accepted checkpoint tag: `smallville-post-proposal-parent-fix`
- Active accepted checkpoint commit: `150e2073fdb608efb4e76ad05cdacbfdb7119222`
- Provenance pack commit: `ab89743f19077d0e23c9a6270658a511f653c55e`
- Operator-surface consolidation freeze commit: `13732640d71a4a842142c37243bc46961f75eb5e`
- Operator-surface v1 status: `accepted` as complete for its current purpose
- Host-native default: `accepted`
- Smallville runtime containerization: `deferred`

Status vocabulary used in this note:
- `observed`: directly supported by accepted source evidence
- `inferred`: reasoned from accepted source evidence but not directly shown as fact
- `open-question`: unresolved and awaiting review
- `approved-for-packet`: explicitly allowed into packet use after review
- `deferred`: intentionally postponed for a later phase

## Purpose

UEIA Autonomous Ops Trial Phase 1 is a governance-first shadow-mode trial for reviewing how one constrained dossier may be observed, normalized, packetized, and proposed without granting execution rights.

This phase exists to define bounded archive and packet work before any implementation or runtime-facing work is considered.

## Shadow-Mode Rule

The accepted Phase 1 flow is:

`observe -> normalize -> package -> propose -> stop`

This phase does not authorize runtime mutation, automatic approval, or direct world or scene mutation.

## Allowed Actions

Phase 1 may:
- review accepted local evidence
- normalize accepted local evidence into archive-record drafts
- compile derived scenario packet drafts
- compile agent brief drafts
- assemble proposal and review packs
- surface open questions, evidence gaps, and review boundaries
- describe spatial context, built features, water-system elements, and visualization hints as descriptive packet content only
- define low-priority or background analytic tasks as proposal-only intellectual work

## Forbidden Actions

Phase 1 must not:
- launch runtime
- mutate runtime or bundle evidence
- approve proposals automatically
- add live connectors
- scrape external sources
- create direct runtime coupling
- weaken host-native as the default
- require container support
- grant world-building authority
- treat packets as runtime world state
- introduce geometry, mesh or model assets, runtime map layers, or renderable scene assets

## Relationship To Harnesses

The following remains accepted:
- `tiny-world` remains the canonical control harness
- `two-house-garden-v1` remains the formal protocol harness

Both remain external harness context and are not UI-managed runtime inside Phase 1 artifacts.

## Relationship To Operator Surface v1

Operator-surface v1 remains the accepted read-only evidence layer for the current baseline.

Phase 1 may read from that accepted baseline context, but it must not reopen operator-surface feature work or convert the read-only layer into a hidden control surface.

## Support Matrix

### Supported In Phase 1

- single-dossier evidence review
- archive-record drafting
- derived scenario packet drafting
- agent brief drafting
- proposal and review pack drafting
- warning and evidence-gap visibility
- descriptive spatial and water-system vocabulary inside non-executing packet content

### Unsupported In Phase 1

- runtime launch
- runtime mutation
- automatic proposal approval
- live connector ingestion
- scraping
- direct runtime coupling
- operator-surface feature expansion
- container-required workflow

### Intentionally Deferred

- implementation of archive or packet tooling
- execution after human approval
- Smallville runtime containerization
- geometry or scene asset work
- world-building implementation
- later multi-dossier work

## Ownership Matrix

| Layer | Owns | Does not own |
| --- | --- | --- |
| Runtime harness | run creation, bundle completeness, reviewed-run evidence capture | packet semantics, archive normalization policy |
| Writer layer | stable generated documents already in accepted baseline | autonomous approval logic |
| Operator surface | read-only evidence views and exports | runtime launch, mutation, or repair |
| Archive/packet layer | descriptive archive, packet, and brief drafting in shadow mode | execution rights or runtime state authority |
| Engine layer | simulation semantics and runtime behavior | dossier review policy or packet governance |
| Scenario layer | scenario content and harness identity | packet approval authority or runtime mutation |

## Baseline Reference Rule

All future work in this phase must cite:
- accepted checkpoint tag `smallville-post-proposal-parent-fix`
- accepted checkpoint commit `150e2073fdb608efb4e76ad05cdacbfdb7119222`
- accepted provenance pack commit `ab89743f19077d0e23c9a6270658a511f653c55e`
- operator-surface consolidation freeze commit `13732640d71a4a842142c37243bc46961f75eb5e`

No later Phase 1 pass should bypass those references or treat a new baseline as accepted without explicit review.

## Dossier-Boundary Rule

Phase 1 is single-dossier only.

It must use:
- one constrained dossier only

It must not use:
- multi-dossier merge
- cross-dossier synthesis unless separately approved

## Fact-Status Discipline

No derived fact may appear without `fact_status`.

No fact may be promoted into packet use without appropriate status discipline.

Facts and claims must remain legible as one of:
- `observed`
- `inferred`
- `open-question`
- `approved-for-packet`
- `deferred`

## Design-Asset Snapshot Rule

Approved static design assets may be used as snapshot or reference sources only.

They must not be treated as:
- observed real-world structures
- runtime geometry
- world-state authority
- implementation instructions

## Structure Evidence Ladder

Phase 1 must keep these interpretation levels distinct:
- directly evidenced structures
- inferred structures
- reconstructed or designed structures

Reconstructed or designed structures are not equivalent to directly evidenced structures without review.

No designed or reconstructed structure may be treated as `observed` unless anchored by accepted source evidence.

## Spatial-Packet Boundary

Phase 1 may describe:
- spatial context
- built features
- water-system elements
- visualization hints

Phase 1 does not authorize:
- geometry
- mesh or model assets
- runtime map layers
- renderable scene assets
- direct world-building implementation

## Packet-Is-Not-World-State Rule

A derived scenario packet is a reviewable interpretive artifact only.

It is not:
- runtime world state
- direct scene mutation authority
- direct world mutation authority
- an execution instruction

## Idle And Background Analytic Task Rule

Later packet work may define low-priority or background analytic tasks, including historic-water-use interpretation tasks.

These remain proposal-only intellectual work. They do not authorize autonomous world expansion, runtime mutation, or background execution.

## Bootstrap Recommendation

Bootstrap is recommendation-only in this phase.

If a later implementation trial is approved, the recommended first dossier is:
- accepted post-fix `two-house-garden-v1` reviewed run

Control context should remain:
- accepted post-fix `tiny-world` smoke

This note does not authorize that later implementation step.

## Materials / People / Power / Place Review Lens

Phase 1 review should explicitly ask:
- Materials: what evidence shows the structure, fabric, or artifact directly, and what remains inferred?
- People: whose labor, maintenance burden, or access pattern is implied by the packet?
- Power: what control assumptions or authority claims are being smuggled in without review?
- Place: what location, adjacency, flow, or environmental context is being described, and how strongly is it evidenced?

## Explicit Stop Conditions

Stop if any Phase 1 pass tries to:
- authorize runtime mutation
- authorize automatic approval
- weaken single-dossier discipline
- treat packets as runtime world state
- treat design assets as direct observed reality without accepted evidence
- introduce connectors, scraping, runtime coupling, or container support
- introduce geometry, scene assets, or world-building authority
