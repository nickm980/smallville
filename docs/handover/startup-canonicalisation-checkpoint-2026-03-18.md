# Startup Canonicalisation Checkpoint

Date: 2026-03-18

## Scope

This checkpoint freezes the current repo-default startup seam only.

Included:
- repo-default startup canonicalisation to the two-house scenario
- runtime verification evidence for repo-default startup from repo root
- direct path-selection regression protection in `SmallvilleConfigTest`
- downstream bootstrap-outcome regression protection in `SimulationServiceTest`

Explicitly excluded:
- launcher redesign
- packaged fallback redesign
- memory redesign
- ontology or place-semantics redesign
- scenario content changes
- non-repo startup guarantees

## Accepted Baseline

Startup canonicalisation commit:
- `bc7eca8` `feat(startup): prefer two-house scenario for default repo startup`

This commit makes `SmallvilleConfig` prefer repo-local two-house scenario files for `config.yaml` and `simulation.yaml` before falling back to bundled defaults.

## Runtime Verification Evidence

Repo-default launcher path used:
- `scripts/start-server.ps1` from repo root

Observed runtime evidence:
- config resolution landed on `scenarios/two-house-garden-v1/config.yaml`
- Blue House, Green House, and Garden state application appeared in runtime logs
- simulation seed loaded with 12 locations and 2 agents
- server started successfully on the configured port

This evidence verifies repo-default startup behavior in the repository working-tree context. It is not a packaged-jar or end-to-end deployment guarantee.

## Regression Protection

### Path-selection level

File:
- `smallville/src/test/java/io/github/nickm980/smallville/SmallvilleConfigTest.java`

Coverage:
- proves `config.yaml` is inside the two-house scenario preference gate
- proves `simulation.yaml` is inside the two-house scenario preference gate
- proves `prompts.yaml` is not redirected by that gate
- proves repo-default `config.yaml` loads the scenario-backed `simulationFile: true` variant
- proves repo-default `simulation.yaml` loads the two-house seed rather than the older forest seed

Targeted verification result:
- `SmallvilleConfigTest`: 3 tests, 0 failures, 0 errors, 0 skipped

### Bootstrap-outcome level

File:
- `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`

Coverage:
- proves bootstrap lands in the seeded two-house lived world
- asserts 12 locations and 2 agents
- asserts Jamie and Alex are present
- asserts stable two-house state markers and daily rhythm values

Targeted verification result:
- `SimulationServiceTest`: 14 tests, 0 failures, 0 errors, 0 skipped

## What Is Frozen

The repo-default startup seam is now frozen at two layers:
1. direct `SmallvilleConfig` path selection
2. downstream `SimulationService` bootstrap outcome

Together these protect the current repo-default startup contract without expanding into launcher orchestration or packaged fallback work.

## Remaining Out Of Scope

Still out of scope after this checkpoint:
- packaged artifact startup behavior
- startup behavior outside the repo-root/default working-tree assumptions
- broader fallback policy redesign
- any memory-system or ontology work
- any scenario authoring or world-content redesign
