# Human Review Intake Review Surface Drill Matrix v1

## Purpose

This matrix defines which approved drill behaviors may be applied to which approved presented consumed human-review intake families. It remains bounded to descriptive drill governance only and may not be read as tooling logic, validator logic, workflow logic, routing logic, runtime behavior, or UI design.

## Matrix

| Drill behavior | `review-intake-summary` | `review-intake-pause-note` | `review-intake-stop-note` | `review-intake-handoff-note` |
| --- | --- | --- | --- | --- |
| `read-presented-intake` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `preserve-presented-order` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `preserve-grouped-review-context` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `pause-on-drift` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `not-allowed` | `allowed-with-stop-rules` |
| `stop-on-hard-condition` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `preserve-canonical-path-visibility` | `allowed` | `allowed` | `allowed` | `allowed` |
| `preserve-provenance-note-visibility` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `preserve-boundary-note-visibility` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |
| `preserve-severity-visibility` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` | `allowed-with-stop-rules` |

## Matrix Notes

- `preserve-canonical-path-visibility` is required wherever a presented consumed human-review intake artifact is referenced in the drill.
- `pause-on-drift` is `not-allowed` for `review-intake-stop-note` because unresolved hard-stop meaning may not be reframed as drift-pause behavior.
- `stop-on-hard-condition` becomes effectively mandatory whenever a presented consumed `review-intake-stop-note` or any surfaced unresolved hard stop is in scope.
- `allowed-with-stop-rules` means the drill remains bounded by unresolved hard-stop preservation, canonical-path visibility, review-decision separation, and all frozen Water Mill, packet, and single-dossier boundaries.
