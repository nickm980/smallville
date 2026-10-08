# Proposal Parent Semantics Execution Spec

## Purpose

This is a narrow execution spec for the already-open proposal-parent semantics issue. It defines the next engine change boundary without applying the fix yet.

Primary issue record:
- `docs/issues/proposal-parent-semantics.md`

Primary code seam:
- `smallville/src/main/java/io/github/nickm980/smallville/api/v1/SimulationService.java`

Primary test layer:
- `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/EndpointsTest.java`

## Current Behavior

- `add_location` proposals are currently accepted as long as the named parent exists.
- The current validation path checks presence of `parentLocation` but does not distinguish container-like locations from object-like leaves.
- This allows proposals such as:
  - `Green House: Water Barrel -> add_location`
  - `Garden: Bench -> add_location`
  - `Green House: Glass Table -> add_location`
- The result is reviewable but structurally awkward world growth.

## Expected Container-Parent Contract

- `add_location` must target an existing location that behaves as a spatial container in the world map.
- Valid examples:
  - `Blue House`
  - `Green House`
  - `Garden`
  - any location that already has child locations and therefore already acts as a container
- Invalid examples:
  - `Green House: Water Barrel`
  - `Garden: Bench`
  - `Green House: Glass Table`
  - other leaf or object-like nodes such as trays, gates, tables, tools, planters, or barrels

## Invalid Parent Detection Strategy

Recommended narrow strategy:

1. Keep all current proposal parsing and proposal DTO shapes unchanged.
2. Add one container-parent helper in `SimulationService` used only by `isProposalValid(...)` for `add_location`.
3. Resolve `proposal.getParentLocation()` to an existing `Location`.
4. Treat a parent as valid for `add_location` only if at least one of these is true:
   - it is a top-level location with no parent
   - it already has at least one direct child location in the world tree
5. Treat a parent as invalid for `add_location` if it resolves to a leaf location with no children.
6. Leave `add_object` and `change_state` validation behavior unchanged in this fix.

This keeps the fix tied to existing hierarchy rather than to new schema or naming rules.

## Reject vs Redirect Options

### Recommended default: reject

- If an `add_location` parent is invalid, reject the proposal before it enters the pending queue.
- Keep the rest of the proposal flow unchanged.
- This is the lowest-blast-radius fix and preserves review clarity.

### Deferred option: redirect

- If a parent is invalid, walk upward to the nearest valid container ancestor and rewrite the proposal parent.
- This is not recommended for the first fix because it changes proposal meaning and complicates auditability.

Decision for the first fix:
- reject invalid `add_location` parents
- do not redirect in the initial patch

## Blast-Radius Analysis

The first fix should be limited to proposal validation only.

In scope:
- `SimulationService.isProposalValid(...)`
- one new helper such as `isValidAddLocationParent(...)` or equivalent local method

Out of scope:
- parser logic
- prompt text
- scenario files
- dashboard
- DTO shape changes
- proposal approval endpoints
- world-creation mechanics beyond validation
- operator tooling

Expected behavior impact:
- fewer pending `add_location` proposals when the parent is object-like
- no change to `add_object`
- no change to `change_state`
- no change to approved manual review flow except that bad `add_location` candidates will stop appearing

## Required Tests

Add or extend `SimulationServiceTest` to cover:

1. valid `add_location` under `Garden` is accepted
2. valid `add_location` under `Green House` is accepted
3. invalid `add_location` under `Green House: Water Barrel` is rejected
4. invalid `add_location` under `Garden: Bench` is rejected
5. invalid `add_location` under `Green House: Glass Table` is rejected
6. `add_object` under a leaf location still behaves exactly as before
7. `change_state` still behaves exactly as before
8. duplicate-pending proposal handling still behaves exactly as before

Optional endpoint-level check:

- extend `EndpointsTest` only if needed to ensure pending proposal serialization still works after validation changes

## Runtime Revalidation Plan After Future Fix

After the future fix is implemented, run this sequence only:

1. targeted Java tests covering the new proposal-parent validation
2. full Java suite
3. short host-native `tiny-world` smoke control run on the frozen model
4. one reviewed `two-house-garden-v1` run with three manual ticks and restart capture

Revalidation checks:

- no parser regressions
- no startup regressions
- no invalid `add_location` proposals with object-like parents in `proposal_review.md`
- proposal review still captures any remaining valid proposals clearly
- action-log readability remains intact
- restart reproducibility remains intact
- manual proposal review remains in force

## Acceptance Condition

This issue is considered execution-ready when the future patch stays inside `SimulationService` proposal validation, adds the required tests, and revalidates both harnesses without reopening parser or scenario work.
