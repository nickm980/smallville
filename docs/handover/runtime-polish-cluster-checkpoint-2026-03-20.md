# Runtime Polish Cluster Checkpoint

Date: 2026-03-20

## 1. Checkpoint Purpose

This checkpoint freezes the bounded runtime-polish cluster after the proposal seam.

It records only the completed runtime-polish closeout and the parked status of the `Garden: on` object-state family.

It does not reopen proposal routing, exception wiring, prompt redesign, world-state repair, or broader runtime redesign.

## 2. Repo Reality At Freeze

Branch at freeze:
- `main`

HEAD at freeze:
- `d1578ad662163c50e9e00a75eabcef758c0b089a`

Working tree at freeze:
- clean tracked tree
- parked untracked files remained untouched

Parked untracked files:
- `docs/contracts/smallville-memory-triplet-schema-v1.md`
- `docs/contracts/smallville-reflection-and-retrieval-v1.md`
- `twohousev.1`

Repo-grounded Russell and subagents note:
- verified: no `Russell` or `russell` references were found in the workspace at freeze
- verified: no subagents were used in this checkpoint lane
- missing: any repo-grounded authority source, runtime role, or artifact family named `Russell`
- decision: this checkpoint does not treat Russell or subagents as an authority layer

## 3. Completed Runtime-Polish Seams

### A. Current-Activity Separator Seam

Commit:
- `f407a9b68e5c94f2ddde1d4eb8263174ed857544`
- `fix(runtime): normalize current activity log formatting`

Bounded defect:
- current-activity log output glued activity and location together with no separator
- example family: `brewing teaBlue House: Kitchen`

Bounded fix:
- `ChatService#getCurrentActivity` now logs activity and location with a separator only
- no prompt contract, DTO, snapshot, API, or state path was changed in this seam

Tests that lock it now:
- `ChatServiceTest.test_get_current_activity_logs_activity_and_location_with_separator`

### B. Reflection Question Trimming Seam

Commit:
- `4e9d686437b9b9b5c6671ce80677a82afd4942d5`
- `fix(runtime): normalize reflection question trimming`

Bounded defect:
- reflection question handling over-trimmed the final query line before relevant-memory lookup
- carried malformed family included leading-fragment output such as `50 am) and seem to be active around that time`

Bounded fix:
- `ChatService#createReflectionFor` now trims the bullet prefix once and passes the once-trimmed query forward unchanged
- no prompt text, proposal behavior, activity logging, snapshot contract, or retrieval architecture was changed in this seam

Tests that lock it now:
- `ChatServiceTest.test_create_reflection_for_uses_once_trimmed_question_for_relevant_memories`

### C. Duplicate Activity-Location Normalization Seam

Commit:
- `d1578ad662163c50e9e00a75eabcef758c0b089a`
- `fix(runtime): normalize duplicate activity location wording`

Bounded defect:
- parsed current-activity text could redundantly retain the same trailing location already carried separately
- example family: `checking on seedlings at Blue House: Kitchen` with separate location `Blue House: Kitchen`

Bounded fix:
- `ChatService#getCurrentActivity` now strips only the exact trailing ` at <location>` duplicate when the parsed location already matches
- meal-language normalization remained intact
- logging separator behavior remained intact
- no prompt text, DTO structure, world state, snapshot shape, or API contract was changed in this seam

Tests that lock it now:
- `ChatServiceTest.test_get_current_activity_strips_exact_duplicate_location_suffix`
- `ChatServiceTest.test_get_current_activity_preserves_non_duplicate_location_wording`
- `ChatServiceTest.test_get_current_activity_logs_activity_and_location_with_separator`

## 4. What Was Explicitly Not Changed

The runtime-polish cluster did not change:
- proposal-family behavior
- `ExceptionRoutes` or global exception routing
- `prompts.yaml`
- world-state mutation wiring
- `SimulationService` API contracts
- memory or retrieval architecture

The cluster also did not change:
- update-loop scope outside the narrow ChatService runtime-polish family
- object-state proposal semantics
- seed loading rules
- parked untracked files

## 5. Parked Object-State Finding

Carried artifact family:
- `Garden: on`
- related object-state anomaly concerns around object-change parsing and storage

Exact verified findings:
- parser hazard exists in `ChatService#getObjectsChangedBy`
- the parser still uses `line.split(":")` for `Object: State` lines
- `UpdateLocations` is not wired into the active `UpdateService` current-HEAD path
- automatic object-state mutation is dormant on current HEAD

Exact parked decision:
- leave parked for now

Why it remains parked:
- the parser hazard is real source debt
- active current-HEAD automatic update wiring does not currently invoke that path
- opening a repair seam now would fix dormant debt by momentum or widen into live manual/proposal state policy without proof

Exact missing evidence needed before reopening:
- one raw current-HEAD evidence dump that ties `Garden: on` to a live path
- acceptable examples: prompt/result trace, action-log trace, request trace, or runtime log showing whether the value came from automatic update, explicit state set, or proposal apply

## 6. Authority Note

This checkpoint governs the runtime-polish cluster only.

It is authoritative for:
- current-activity separator normalization
- reflection question trimming normalization
- duplicate activity-location normalization
- the parked status of the `Garden: on` object-state family on current HEAD

It is not authoritative for:
- proposal invalid-ID behavior
- proposal review/apply behavior
- broader runtime or architecture work

The proposal-runtime checkpoint remains the separate authority for proposal invalid-ID behavior and the broader proposal-runtime closeout.

## 7. Next Recommended Lane

Recommended next lane:
- a new review-only simulation-coherence lane

Boundary for that lane:
- keep it simulation-first
- do not define or implement the lane here
- exclude parked object-state debt unless new live evidence appears
- do not reopen the closed ChatService runtime-polish cluster without fresh conflicting evidence
