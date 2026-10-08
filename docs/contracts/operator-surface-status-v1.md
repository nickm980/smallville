# Operator Surface Status v1

## Operational Read

- Status: `accepted`
- Scope: `governance/documentation only`
- Active accepted checkpoint tag: `smallville-post-proposal-parent-fix`
- Active accepted checkpoint commit: `150e2073fdb608efb4e76ad05cdacbfdb7119222`
- Provenance pack commit: `ab89743f19077d0e23c9a6270658a511f653c55e`
- Slice 1 commit: `cd909761ff817e4d085de4ceb0283a059dc6fc83`
- Slice 2 commit: `9e80c8e07c51e84e8628cbc2344b5102b751d48f`
- Slice 3 commit: `12614e1445de82b28f77ed970e1b13369e9c65d9`
- Slice 4 commit: `efe34db4b858533720fc73925e20a7feecbaa5b3`

Status vocabulary used in this note:
- `observed`: directly seen in repo files or accepted run evidence
- `accepted`: approved operating rule or capability
- `frozen`: approved and committed checkpoint or boundary
- `deferred`: intentionally postponed for a later phase
- `not approved in this phase`: explicitly outside the allowed boundary now

## Accepted Capabilities

Operator-surface v1 is `accepted` as a read-only evidence stack with these capabilities:
- Run-bundle summary
- Proposal review
- Checklist status
- Run comparison
- CSV export

These capabilities are implemented by the frozen host-native stack under [`control/vb`](C:/SmallVille/control/vb).

## Read-Only Boundary

Operator-surface v1 is `accepted` as read-only only.

It may:
- summarize evidence
- compare evidence
- filter evidence
- export evidence
- surface warnings

It must not:
- launch runtime
- mutate simulation state
- approve or reject proposals
- rewrite bundle artifacts
- infer missing evidence as if present
- become a hidden control surface

## Source-Of-Truth Rule

Source-of-truth is `accepted` as:
- on-disk bundle evidence is authoritative

That includes:
- bundle files under [`runs`](C:/SmallVille/runs)
- accepted baseline records under [`baselines`](C:/SmallVille/baselines)
- frozen checklist source under [`docs/evals`](C:/SmallVille/docs/evals)

Operator-surface views and exports are `accepted` only as read-only derivations of those files.

## Warning-Preservation Rule

Warning preservation is `accepted` as:
- warnings are surfaced, never repaired

If a bundle is missing, blank, or inconsistent, the operator surface must:
- show the warning
- preserve the source value as-is
- avoid silent repair

## Known Evidence-Gap Handling Rule

Known evidence-gap handling is `accepted` as follows:
- `observed` accepted gap: [`operator_notes.md`](C:/SmallVille/runs/20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k/operator_notes.md) is missing from run `20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k`
- this remains a known accepted evidence defect in that historical reviewed bundle
- future reviewed runs must include `operator_notes.md`
- operator-surface v1 must surface this as missing evidence, not repair it

## Harness Context Boundary

The following is `accepted`:
- `tiny-world` remains the canonical control harness
- `two-house-garden-v1` remains the formal protocol harness

The following is also `accepted`:
- both harnesses remain external runtime context
- they are not UI-managed runtime inside operator-surface v1

## Support Matrix

### Supported

- Host-native read-only bundle inspection
- Run-bundle summary view
- Proposal review view
- Checklist status view
- Run comparison view
- CSV-first export helpers
- Warning visibility for missing, blank, or inconsistent evidence

### Unsupported

- Runtime launch from operator surface
- Proposal approval or rejection controls
- Simulation mutation
- Hidden state repair
- Dashboard replacement
- Live sensor or SEPA integration
- Engine-coupled control logic

### Intentionally Deferred

- Container implementation
- JSON expansion
- XLSX expansion
- UI polish
- Runtime work
- New engine work
- New scenario work
- Executable VB tooling

## Deferred Roadmap Note

The following remain `deferred`:
- container implementation
- JSON and XLSX expansion
- UI polish
- runtime work
- new engine or scenario work
- executable VB tooling

Operator-surface v1 should be treated as complete for its current purpose.

## Future Expansion Baseline

Future expansion is `accepted` only if it begins from:
- active accepted checkpoint tag `smallville-post-proposal-parent-fix`
- active accepted checkpoint commit `150e2073fdb608efb4e76ad05cdacbfdb7119222`
- accepted provenance pack commit `ab89743f19077d0e23c9a6270658a511f653c55e`
- frozen slice 1 commit `cd909761ff817e4d085de4ceb0283a059dc6fc83`
- frozen slice 2 commit `9e80c8e07c51e84e8628cbc2344b5102b751d48f`
- frozen slice 3 commit `12614e1445de82b28f77ed970e1b13369e9c65d9`
- frozen slice 4 commit `efe34db4b858533720fc73925e20a7feecbaa5b3`
- this consolidation freeze

No later pass should bypass those references or reopen already frozen layers without a specific observed defect.

## Ownership Matrix

### Runtime Harness

Owns:
- run creation
- evidence bundle completeness
- startup/shutdown checks
- presence of required artifacts such as `operator_notes.md`

Does not own:
- engine semantics
- operator-surface repair behavior

### Writer Layer

Owns:
- proposal review document generation
- stable `proposal_count` and note rendering

Does not own:
- engine proposal semantics
- operator review decisions

### Operator Surface

Owns:
- read-only summary
- comparison
- warning display
- CSV export derived from frozen views

Does not own:
- runtime launch
- proposal controls
- source artifact repair

### Engine Layer

Owns:
- simulation semantics
- proposal validation rules
- parser/runtime behavior inside Smallville

Does not own:
- bundle completeness policy
- operator review UI behavior

### Scenario Layer

Owns:
- scenario content
- harness identity
- review context for `tiny-world` and `two-house-garden-v1`

Does not own:
- operator-surface feature behavior
- engine semantics

## Explicit Stop Conditions

Stop if any future operator-surface pass tries to:
- launch runtime
- mutate bundles
- add proposal controls
- weaken host-native as the default
- present deferred features as accepted
