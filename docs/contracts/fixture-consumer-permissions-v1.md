# Fixture Consumer Permissions v1

## Purpose

This note defines the allowed and forbidden capabilities for future consumer profiles that operate on immutable canonical fixtures.

Capabilities in this note are contractual boundaries only. They are not implementation instructions.

The canonical fixture files at their canonical paths remain the source of truth for all allowed consumer behavior in this package.

## Allowed Capabilities

### Read

Consumers may:
- read canonical fixture files
- read canonical IDs
- read provenance notes
- read boundary language
- read schema-aligned field labels preserved in the promoted specimens

### Validate

Consumers may:
- confirm fixture existence
- confirm canonical ID identity
- confirm presence of schema-aligned sections
- confirm provenance wording is preserved
- confirm fact-status wording is preserved
- confirm packet/world-state separation is preserved
- confirm Water Mill design-asset boundary is preserved
- confirm single-dossier continuity is preserved

### Compare

Consumers may:
- compare canonical IDs
- compare fixed canonical paths
- compare field or section presence
- compare provenance wording
- compare fact-status wording
- compare boundary wording

### Summarize

Consumers may:
- summarize fixture scope
- summarize provenance continuity
- summarize preserved uncertainty
- summarize packet/world-state boundary language
- summarize Water Mill design-asset handling rules

### Export Diagnostics

Consumers may:
- export derived diagnostics outside canonical fixture files
- export warnings about weakened provenance
- export warnings about weakened packet/world-state separation
- export warnings about weakened single-dossier continuity
- export warnings about weakened Water Mill design-asset boundaries

Exported diagnostics remain non-authoritative relative to the frozen canonical fixtures.

No derived output, summary, comparison note, or diagnostics export may replace a canonical fixture as source of truth.

## Forbidden Capabilities

Consumers may not:
- mutate fixture files
- infer away missing evidence
- promote inferred content to observed
- treat a packet as runtime world state
- treat Water Mill design assets as real-world authority
- couple fixtures to runtime
- silently merge a second dossier
- rewrite canonical wording in place
- auto-repair missing sections
- replace fixtures with generated or normalized mirrors

## Boundary Clarifications

### Evidence Boundary

Consumers must preserve the distinction between:
- observed content
- inferred content
- open-question content
- deferred content

No missing evidence may be papered over through normalization or summarization.

### Runtime Boundary

No consumer may:
- bind fixture content to live Smallville runtime state
- treat a review decision as execution approval
- convert descriptive spatial language into geometry, runtime map layers, or renderable scene authority

### Water Mill Boundary

Water Mill content remains:
- design-asset-derived context only
- non-authoritative for observed real-world status
- non-executing

### Single-Dossier Boundary

Consumers must remain inside the canonical single-dossier chain.

`tiny-world` remains control context only and may not be promoted into a second operational dossier.

## Explicit Stop Conditions

Stop consumer-profile use if any proposed action:
- mutates a canonical fixture
- promotes inferred content to observed without accepted evidence
- treats packet text as runtime world state
- treats Water Mill content as real-world authority
- couples fixtures to runtime
- silently merges a second dossier
