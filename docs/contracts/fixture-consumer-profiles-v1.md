# Fixture Consumer Profiles v1

## Purpose

This note defines the first approved consumer profiles for read-only use of the canonical fixture set.

All profiles remain bounded, non-executing, and single-dossier only.

The frozen canonical fixture text at the canonical paths remains the source of truth for all profiles described here.

## Profile 1: Fixture Reader

- Purpose: read canonical fixtures as immutable source text for provenance, boundary, and schema-aligned content review.
- Allowed inputs:
  - the four canonical fixture files
  - canonical IDs
  - preserved provenance notes
  - preserved boundary language
- Allowed outputs:
  - read summaries
  - coverage notes
  - non-authoritative reading diagnostics
- Allowed actions:
  - open fixture files
  - read canonical IDs
  - inspect provenance wording
  - inspect fact-status wording
  - inspect packet/world-state separation wording
- Forbidden actions:
  - mutate fixture files
  - rewrite or simplify preserved uncertainty
  - treat Water Mill design-asset context as observed real-world authority
  - treat packet content as runtime world state
- Evidence boundary: may read only the frozen canonical fixture text and its preserved source references.
- Runtime boundary: no live runtime binding, no runtime interpretation, no execution signaling.
- Mutation boundary: read-only only; no edits, no rewrites, no auto-repair.
- Source-of-truth note: reader outputs may describe fixture content but may not replace canonical fixture text.

## Profile 2: Fixture Comparator

- Purpose: compare canonical fixture content against expected canonical IDs, preserved boundaries, and schema-aligned sections.
- Allowed inputs:
  - the four canonical fixture files
  - expected canonical IDs
  - approved contract wording from the frozen canonical fixture package
- Allowed outputs:
  - comparison reports
  - drift notes
  - non-authoritative mismatch diagnostics
- Allowed actions:
  - compare canonical IDs
  - compare field or section presence
  - compare provenance wording
  - compare single-dossier boundary wording
  - compare packet/world-state boundary wording
- Forbidden actions:
  - auto-correct drift in source files
  - collapse `open-question` or `deferred` into stronger states
  - turn mismatch findings into rewrite instructions
  - introduce a second dossier as reference baseline
- Evidence boundary: comparison is limited to frozen fixture text and approved contract references already in scope.
- Runtime boundary: may not compare against runtime state or runtime outputs.
- Mutation boundary: may not write back fixes or derived normalizations to canonical fixtures.
- Source-of-truth note: comparison outputs are downstream notes only and may not become substitute canonical sources.

## Profile 3: Fixture Diagnostics Consumer

- Purpose: derive bounded diagnostics about preservation of provenance, fact-status, and boundary discipline.
- Allowed inputs:
  - the four canonical fixture files
  - approved canonical fixture contract notes
  - approved consumer permission rules
- Allowed outputs:
  - pass/fail/warning style diagnostics
  - provenance-preservation notes
  - boundary-drift notes
- Allowed actions:
  - detect missing provenance notes
  - detect canonical ID drift
  - detect weakened Water Mill design-asset boundary language
  - detect weakened packet/world-state separation
  - detect weakened single-dossier continuity
- Forbidden actions:
  - replace missing sections automatically
  - infer away evidence gaps
  - promote inferred content to observed
  - treat diagnostics as new fixture truth
- Evidence boundary: diagnostics remain anchored to the frozen text and may not invent missing evidence.
- Runtime boundary: diagnostics may not query or bind to runtime as a source of truth.
- Mutation boundary: diagnostics are external outputs only and may not alter fixture files.
- Source-of-truth note: diagnostics may report drift or weakness but may not supersede canonical fixture content.

## Profile 4: Fixture Export Consumer

- Purpose: export derived diagnostics or summaries into separate non-canonical outputs for future review use.
- Allowed inputs:
  - the four canonical fixture files
  - diagnostics already derived from approved read-only consumption
- Allowed outputs:
  - summary reports
  - comparison summaries
  - derived diagnostics bundles that remain outside the canonical fixture paths
- Allowed actions:
  - summarize fixture boundaries
  - summarize provenance preservation
  - export non-authoritative diagnostics
  - export read-only comparison results
- Forbidden actions:
  - export rewritten fixture mirrors as replacement sources
  - create JSON fixture mirrors as authoritative substitutes
  - convert descriptive exports into implementation instructions
  - imply runtime or execution approval through exported wording
- Evidence boundary: exported outputs must preserve uncertainty, provenance, and boundary language from source fixtures.
- Runtime boundary: exports may not be coupled to runtime inputs or runtime state.
- Mutation boundary: exports must remain separate artifacts and may not overwrite canonical fixtures.
- Source-of-truth note: exported summaries and diagnostics may not replace the canonical fixtures at their canonical paths.

## Cross-Profile Boundary Rules

All profiles must preserve:
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, not runtime world state
- single-dossier continuity
- non-executing downstream use

No profile may gain:
- mutation authority
- runtime authority
- scene/world mutation authority
- authority to promote inferred content into observed status without accepted evidence
