# SmallVille / UEIA / CTO Master State — Northbridge Update

**Timestamp:** 2026-04-03 GMT (refreshed from 2026-03-22 original)  
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
- Current carried `HEAD`: `8f92ba2` (`docs(control-room): align reactivation protocol with Northbridge format`)
- Latest landed implementation commits:
  - `5f4e405` — `feat(sidecar): add sidecar-packet-assembler-v1`
  - `104bd46` — `feat: cherry-pick Codex KEEP-group`
- Tracked tree: clean
- `git diff --stat`: empty (docs-only commits on top)

### March 22 residue — now resolved
All five items have been dispositioned:
- `docs/contracts/smallville-memory-triplet-schema-v1.md` — ACTIVE AUTHORITY (tracked contract)
- `docs/contracts/smallville-reflection-and-retrieval-v1.md` — ACTIVE AUTHORITY (tracked contract)
- `output/` — gitignored
- `tmp/` — gitignored
- `twohousev.1` — absent from filesystem

See `docs/handover/residue-disposition-checkpoint-2026-04-03.md` for full disposition table.

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
- `.codex-logs` exists as an empty non-authority workspace-local directory (gitignored)
- the tracked repo is intact

### Residue disposition (April 3 update)
All five March 22 ambiguity items have been resolved:
- `docs/contracts/smallville-memory-triplet-schema-v1.md` — ACTIVE AUTHORITY (tracked, Java-referenced)
- `docs/contracts/smallville-reflection-and-retrieval-v1.md` — ACTIVE AUTHORITY (tracked, Java-referenced)
- `output/` — gitignored
- `tmp/` — gitignored
- `twohousev.1` — absent from filesystem, no action needed

No remaining workspace hygiene items requiring disposition.

## 9. Current drift risks

- older package files remaining in active rotation after this update
- confusing visible files with active authority
- letting interface names create fake authority tiers
- widening from the frozen seam baselines into mutation, memory, retrieval, or toolchain redesign without an explicit lane
- treating the KEEP-group cherry-pick as authorization for broader Codex-style changes without a dedicated lane

## 10. Current recommended next lane

### Recommended lane
`bounded implementation: AffectState reconciliation or next implementation seam`

### Why this is next
Residue disposition is complete and source-pack prose is current.
The repo is clean, authority map is unambiguous, and workspace hygiene is resolved.
The next productive work is a bounded implementation lane.

### Candidate lanes
- AffectState reconciliation (align Java affect model with current schema contracts)
- sidecar integration review (verify sidecar-packet-assembler-v1 wiring against live organism)
- test coverage expansion for newly landed KEEP-group classes

### What the next lane is not
- not a mutation lane
- not a bridge widening lane
- not a memory-lattice lane
- not a retrieval lane
- not a schema lane
- not a docs churn lane

## 11. Current recommended lane prompt

```text
LANE TYPE: bounded implementation

OBJECTIVE
Choose one bounded implementation lane and execute it.

PLAN MODE FIRST.

CANDIDATE LANES (pick one)
1. AffectState reconciliation — align Java affect model with current schema contracts
2. Sidecar integration review — verify sidecar-packet-assembler-v1 wiring against live organism
3. Test coverage expansion — add tests for newly landed KEEP-group classes

HARD BOUNDARIES
- One lane only
- Do not widen into memory-lattice, retrieval, bridge architecture, mutation, or general toolchain redesign
- Do not reopen closed seams
- Repo truth outranks prose
- Test before commit

STOP AFTER THE BOUNDED LANE OUTPUT.
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
