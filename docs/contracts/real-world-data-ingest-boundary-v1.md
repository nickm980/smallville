# Real-World Data Ingest Boundary v1

## Purpose

This note defines the intake boundary for UEIA Autonomous Ops Trial Phase 1.

It limits source intake to accepted local and already-approved snapshot material, preserves provenance, and prevents live ingestion or uncontrolled synthesis.

## In-Scope Source Classes

Phase 1 may use:
- accepted run bundles
- local notes
- structured local observations
- approved public datasets already collected or licensed
- historical or archive materials already in project scope
- approved static design assets such as hand sketches, annotated layouts, spatial concept drawings, static model exports, and locally prepared reference diagrams

## Out-Of-Scope Source Classes

Phase 1 must not use:
- live connectors
- unsanctioned scraping
- direct runtime coupling
- unreviewed external feeds
- live sensor streams
- automatically refreshed public endpoints

## Single-Dossier Intake Rule

Phase 1 must ingest one constrained dossier only.

It must not:
- merge multiple dossiers
- synthesize across dossiers unless separately approved

## Provenance Requirements

Each intake item should preserve, where relevant:
- source identifier
- source class
- source title
- source path or approved URI
- collection date
- snapshot date
- license or scope note
- provenance status
- owner layer
- fact status when facts are derived from the source

## Snapshot Vs Live-Data Rules

Phase 1 is snapshot-only.

Allowed:
- fixed local files
- already-collected approved datasets
- accepted bundle evidence
- approved static design assets used as reference material

Not allowed:
- live polling
- feed subscriptions
- runtime hooks
- connector-driven updates

## Design-Asset Snapshot Rule

Approved static design assets are accepted only as snapshot or reference sources.

They must not be treated as:
- automatically observed fact
- direct runtime geometry
- runtime world-state authority
- implementation instructions

## Fact-Status And Structure-Confidence Rules

No derived fact may appear without `fact_status`.

Designed or reconstructed structures are not equivalent to directly evidenced structures.

No designed or reconstructed structure may be treated as `observed` unless anchored by accepted source evidence.

## Ownership And Redaction Expectations

Each intake item should preserve:
- evidence ownership
- scope or license expectations
- redaction status where relevant

Local notes and design assets should be treated as supporting context aids unless accepted source evidence anchors them as stronger fact.

## Explicit No-Live-Connectors Rule

Live connectors are not approved in this phase.

Any later need for connectors, scraping, or direct runtime coupling requires a separate phase decision outside this note.
