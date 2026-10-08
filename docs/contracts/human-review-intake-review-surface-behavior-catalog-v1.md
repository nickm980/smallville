# Human Review Intake Review Surface Behavior Catalog v1

## Purpose

This catalog defines the approved presentation-only behavior vocabulary for bounded review-surface presentation of already-consumed human-review intake artifacts. It is descriptive only and may not be read as tooling logic, runtime behavior, workflow logic, routing logic, or UI design.

## `group-for-review`

- Meaning:
  - arrange already-consumed human-review intake artifacts together for bounded human review context only
- Allowed use:
  - present related consumed intake artifacts together while keeping canonical-path visibility, provenance, and preserved boundary context explicit
- Forbidden use:
  - turn grouped presentation into package semantics, dashboard control, routing logic, or a new intake family
- Boundary note:
  - grouping remains descriptive review context only and may not create review-decision handling, workflow state, execution state, or canonical replacement

## `sort-by-existing-field`

- Meaning:
  - order already-consumed human-review intake artifacts by preserved existing field vocabulary only
- Allowed use:
  - arrange presented consumed intake artifacts using existing preserved meaning such as family-local severity context or canonical-path reference order
- Forbidden use:
  - introduce scoring, ranking, prioritization, hidden authority selection, or new presentation-local authority vocabulary
- Boundary note:
  - sorting may not suppress hard-stop visibility, canonical-path visibility, provenance notes, or boundary notes

## `filter-by-existing-field`

- Meaning:
  - narrow already-consumed human-review intake artifacts using preserved existing field vocabulary only
- Allowed use:
  - limit visible review context using existing preserved artifact-local vocabulary while keeping stop visibility and boundary visibility intact
- Forbidden use:
  - hide unresolved hard stops, hide canonical-path visibility, create workflow triage, or create approval-handling meaning
- Boundary note:
  - filtering remains a review aid only and may not become routing logic, workflow logic, or control behavior

## `highlight-severity`

- Meaning:
  - make preserved intake-local severity context more visible for bounded human review
- Allowed use:
  - emphasize preserved severity context without changing authority or directing action
- Forbidden use:
  - recast severity as scoring, ranking, prioritization, urgency modeling, or execution guidance
- Boundary note:
  - highlighted severity remains review-only context and may not become approval handling, workflow handling, or execution handling

## `highlight-stop-condition`

- Meaning:
  - make an already-preserved unresolved hard stop more visible during presentation
- Allowed use:
  - emphasize preserved halt-aware review context so the unresolved hard stop remains explicit
- Forbidden use:
  - soften, resolve, reroute around, or downgrade the hard stop into advisory-only wording
- Boundary note:
  - highlighted stop meaning remains unresolved unless human review separately resolves it and may not become downstream clearance or control behavior

## `show-canonical-path-reference`

- Meaning:
  - keep the canonical path visible wherever a consumed human-review intake artifact is presented
- Allowed use:
  - show canonical-path references as source-of-truth visibility for the underlying fixture
- Forbidden use:
  - hide canonical-path references, treat surfaced paths as replacement mirrors, or treat surfaced paths as mutable targets
- Boundary note:
  - canonical-path visibility is required wherever an intake artifact is presented

## `show-provenance-note`

- Meaning:
  - show preserved provenance context that already travels with the consumed human-review intake artifact
- Allowed use:
  - expose provenance notes so bounded human review can read preserved source lineage without changing authority
- Forbidden use:
  - strengthen provenance notes into approval text, execution guidance, or replacement authority
- Boundary note:
  - shown provenance remains preserved context only and may not create new authority

## `show-boundary-notes`

- Meaning:
  - show preserved boundary context such as Water Mill, packet, dossier, and review-decision separation notes
- Allowed use:
  - expose preserved boundary notes so human review can see what remains bounded, unresolved, and non-authoritative
- Forbidden use:
  - restate Water Mill as observed built authority, restate packet content as runtime world state, or weaken single-dossier continuity
- Boundary note:
  - shown boundary notes remain preserved context only and may not become review-decision handling, workflow logic, execution handling, or control behavior
