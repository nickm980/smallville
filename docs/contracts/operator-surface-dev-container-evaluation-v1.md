# Operator Surface Dev-Container Evaluation v1

## Operational Read

- Status: `accepted` as an evaluation note only
- Current default: `host-native`
- Dev-container support: `deferred` and only worth considering later as an optional tooling aid
- Containerized operator-surface delivery: `deferred`
- Smallville simulation-runtime containerization: `deferred`

This note does not authorize any container implementation.

## Recommendation

Recommended default:
- keep operator-surface v1 host-native first
- treat dev-container support as optional for reproducible local build and testing later
- keep Smallville runtime host-native

This is best at the current stage because host-native matches the accepted harness baseline and keeps operator evidence paths easy to inspect directly on disk.

## Alternatives Considered

### Container-First Operator Surface

- Status: `not approved in this phase`
- Reason: it adds packaging complexity before the host-native operating model needs it

### Mandatory Dev Container For All Operator Work

- Status: `not approved in this phase`
- Reason: it would weaken the accepted host-native default without solving a current blocker

### Runtime Containerization Alongside Operator Surface

- Status: `deferred`
- Reason: it would blur tooling evaluation with simulation substrate decisions

## What A Future Dev Container Could Help With

Potential later benefits are `observed as plausible`, not accepted implementation:
- reproducible PowerShell/module environment
- consistent proof-script execution
- repeatable local build/testing for the read-only operator stack
- easier onboarding for contributors who only need the operator tooling layer

## What Must Remain Outside Any Future Dev Container

The following must remain outside any future dev container in this phase:
- Smallville runtime
- Ollama
- canonical [`runs`](C:/SmallVille/runs)
- canonical [`baselines`](C:/SmallVille/baselines)
- simulation launch paths

## Why Host-Native Remains The Default

Host-native remains `accepted` as the default because:
- it matches the accepted runtime and evidence baseline
- it keeps provenance and file locations obvious
- it avoids introducing container orchestration complexity into a completed read-only stack
- it preserves the clearest boundary between operator tooling and simulation runtime

## Explicitly Not Approved In This Phase

The following are `not approved in this phase`:
- Compose plans
- runtime-packaging plans
- moving live evidence roots into container space
- treating a dev container as required for operator-surface use

## Explicit Stop Conditions

Stop if any future container note starts to:
- authorize Smallville runtime containerization
- move canonical evidence roots into a container-managed location
- describe Compose or runtime packaging as current work
- weaken host-native as the default

