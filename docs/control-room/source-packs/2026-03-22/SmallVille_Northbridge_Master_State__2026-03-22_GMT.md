# SmallVille / UEIA / CTO Master State — Northbridge Update

**Timestamp:** 2026-03-22 GMT  
**Status:** Current management-layer master state companion  
**Purpose:** Human-readable master continuity record for the current source-package refresh

## 1. What this file is for

This file is the single best plain-language summary of where the project stands after the ask-shadow bridge seam freeze and the later workspace residue audit.

It is not the first authority file to load in a fresh session.

It is the easiest file to read when you want the current operating picture without reconstructing it from multiple checkpoints, prompts, and copied action logs.

## 2. Current carried project position

SmallVille-as-lived-world remains the product.

UEIA remains the governance, audit, and control scaffold around the product.

The two-house seed remains the live default organism unless live repo evidence later proves otherwise.

Java remains the authoritative world executor.

Python remains a sidecar reasoning, evaluation, and agentic development surface.

Bridge outputs remain non-authoritative unless a later explicitly authorized lane changes that.

## 3. Current carried repo state

### Verified from latest carried repo report
- Branch: `main`
- Current carried `HEAD`: `b2aef007c6c5f4273bac26bf7fff0fe57c16d635`
- Latest landed seam commit: `feat(ask-shadow): add bounded shadow bridge seam`
- Tracked tree: clean
- `git diff --stat`: empty

### Current non-ignored residue still present
These are **not active authority** merely because they are visible:

- `docs/contracts/smallville-memory-triplet-schema-v1.md`
- `docs/contracts/smallville-reflection-and-retrieval-v1.md`
- `output/pdf/smallville-app-summary.pdf`
- `tmp/pdfs`
- `twohousev.1`

## 4. What is now actually frozen

### Already frozen before this update
- proposal invalid-ID runtime seam
- runtime-polish ChatService checkpointed cluster
- March 21 `/models` ask-shadow runtime-surface checkpoint

### Newly frozen in repo code
The bounded ask-shadow bridge seam is now committed in repo history.

Frozen implementation shape:
- native Java ask answer remains caller-visible
- bounded shadow sidecar call runs after the native ask answer path
- bridge is shadow-only
- bridge remains host-authoritative
- bridge does not mutate the world
- `/models` exposes effective bridge runtime state
- runtime defaults and timeout normalization are test-backed

## 5. Current architecture stance

### Host-side truth
- SmallVille remains the authoritative world
- Java remains the final host authority
- world mutation is not delegated to Python

### Sidecar stance
- Python remains the neural and persona sidecar
- localhost HTTP/JSON remains the accepted bootstrap transport
- advisory growth is possible later
- live mutation remains blocked
- remote escalation remains blocked
- broader bridge widening remains parked

### Naming
`Northbridge` is the team-facing name for the CTO command layer.

It is an alias for `CTO Command Interface`, not a new authority tier.

## 6. Current control model

### Top control
- Callum = human hold / release gate
- CTO Command Interface = sole top AI control layer
- Northbridge = team-facing alias for that same top AI control layer

### Explorer layer
- Carson = main Explorer
- Russell = wider-team only, not Explorer lead and not a command layer

### Default narrow-lane team pattern
- Carson
- Review Analyst
- Archivist
- Simulation Briefing Agent
- Northbridge / CTO Command Interface for synthesis and final lane judgment

## 7. Current interface split

### Primary live execution surface
Codex app is now the preferred main CLI / coding interface.

### Oversight surface
VBC remains open for oversight and selective comparison only.

### Optional supporting review surfaces
- GitHub Copilot Pro for cheap local assist and inline completion
- Claude for periodic creative checkpoint review
- Grok for adversarial review, bias checks, and alignment checks
- Ollama remains part of the local ecosystem and should not be displaced by assumption

None of those auxiliary surfaces outrank live repo truth or the Northbridge command layer.

## 8. Workspace residue and sub-agent audit summary

### What the residue audit proved
- no tracked evidence of sub-agent takeover
- no tracked repo-local `.claude`, `.grok`, `agents`, `agentic`, `prompt-packet`, `ledger`, or `scratch` control surfaces
- no hits for `Plato`, `Meitner`, `Carver`, `Mendel`, or `Rowen` as authority artifacts
- `.codex-logs` exists as an empty non-authority workspace-local directory
- the tracked repo is intact
- the main hygiene risk is untracked material sitting in authority-looking paths

### Highest-priority ambiguity items
- `docs/contracts/smallville-memory-triplet-schema-v1.md`
- `docs/contracts/smallville-reflection-and-retrieval-v1.md`
- `twohousev.1`

### Lower-priority noise
- `output/`
- `tmp/`
- local export artifacts

## 9. Current drift risks

- stale March 20 source files still carrying the older management `HEAD`
- older package files remaining in active rotation after this update
- confusing visible files with active authority
- treating untracked contract drafts as active contract authority
- letting interface names create fake authority tiers
- widening from the frozen ask-shadow seam into mutation, memory, retrieval, or toolchain redesign without an explicit lane

## 10. Current recommended next lane

### Recommended lane
`review-only: residue disposition and ignore-policy classification`

### Why this is next
The code seam is already frozen.

The remaining management ambiguity is not implementation correctness.
It is workspace hygiene and authority clarity.

The next lane should decide:
- whether the two untracked contract drafts remain parked, get archived more explicitly, or later become a dedicated lane
- whether `output/`, `tmp/`, and optionally `.codex-logs/` should become explicit ignore-policy surfaces
- whether `twohousev.1` should remain parked as lineage noise or be explicitly classified for later cleanup

### What this lane is not
- not a mutation lane
- not a bridge widening lane
- not a memory-lattice lane
- not a retrieval lane
- not a schema lane
- not a docs churn lane beyond classification

## 11. Current recommended lane prompt

```text
LANE TYPE: review-only

OBJECTIVE
Classify the remaining untracked residue and decide the correct disposition for each item without editing, deleting, staging, or committing anything.

PLAN MODE FIRST.

HARD BOUNDARIES
- No code edits
- No doc edits
- No YAML edits
- No schema edits
- No staging
- No commit
- No cleanup actions
- No ignore-file edits in this lane
- Do not widen into memory, retrieval, bridge architecture, mutation, or general toolchain redesign

TARGET ITEMS
1. docs/contracts/smallville-memory-triplet-schema-v1.md
2. docs/contracts/smallville-reflection-and-retrieval-v1.md
3. twohousev.1
4. output/
5. tmp/
6. .codex-logs/ if present

REQUIRED OUTCOME
For each target item:
- classify origin as verified / inferred / unknown
- classify authority status
- classify whether it should stay parked, become ignore-policy candidate, or later become its own dedicated lane
- explain risk level
- recommend exact next disposition lane, if any

STOP AFTER THE REPORT.
```

## 12. What becomes lineage after this package lands

When this package is installed, earlier March 20 package files and older pre-package sources remain useful, but they should become **lineage-only** unless specifically reopened for historical continuity.

That includes:
- v1.1 / v1.2 autoload profiles
- v1 / older control-room files
- older bootstrap files
- old reactivation text files
- older handover lineage not needed for active command

## 13. Final operating rule

Keep this package lean.

Keep authority explicit.

Keep live repo truth above prose.

Keep one bounded lane at a time.
