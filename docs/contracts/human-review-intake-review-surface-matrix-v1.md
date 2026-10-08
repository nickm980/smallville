# Human Review Intake Review Surface Matrix v1

## Purpose

This matrix defines which approved review-surface behaviors may be applied to which approved consumed human-review intake families. It remains bounded to presentation-only governance and may not be read as tooling logic, workflow logic, routing logic, runtime behavior, or UI design.

## Matrix

| Review-surface behavior | `review-intake-summary` | `review-intake-pause-note` | `review-intake-stop-note` | `review-intake-handoff-note` |
| --- | --- | --- | --- | --- |
| `group-for-review` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `sort-by-existing-field` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `filter-by-existing-field` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `highlight-severity` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `highlight-stop-condition` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `show-canonical-path-reference` | `allowed` | `allowed` | `allowed` | `allowed` |
| `show-provenance-note` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `show-boundary-notes` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |

## Matrix Notes

- `show-canonical-path-reference` is required wherever a consumed human-review intake artifact is presented.
- `highlight-stop-condition` becomes effectively mandatory whenever a presented consumed intake artifact preserves an unresolved hard stop.
- `allowed-with-stop-rules` means presentation remains bounded by unresolved hard-stop preservation, canonical-path visibility, review-decision separation, and all frozen Water Mill, packet, and single-dossier boundaries.
- No matrix cell authorizes workflow handling, execution handling, control behavior, scoring, ranking, prioritization, routing logic, or review-decision replacement.
