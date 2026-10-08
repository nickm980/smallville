# SmallVille Agent Role Matrix v2

**Timestamp:** 2026-03-22 GMT

## 1. Purpose

This file describes how the current named roles should be used inside the SmallVille / UEIA / CTO workflow.

It is not an implementation roadmap.

It is a control and delegation matrix.

## 2. Role matrix

| Role | Primary function | Authority level | Can reopen seams? | Default use |
|---|---|---:|---:|---|
| Callum | Human pause/hold gate | Highest human authority | Yes, explicitly | Final direction and hold/release |
| CTO Command Interface | Management, authority loading, seam choice, drift control | Highest AI control layer | Yes, intentionally | Choose one bounded lane and generate execution prompts |
| Northbridge | Team-facing alias for CTO Command Interface | Same as CTO layer | No separate authority | Human-readable team label only |
| Carson | Explorer lead | Subordinate specialist | No | Repo map discovery, file finding, initial route inspection |
| Russell | Wider-team role | Subordinate specialist | No | Supplemental exploration only when requested |
| Executive Controller | Structure and control interpretation | Subordinate specialist | No | Keep actions aligned to authority |
| Archivist | Historical continuity and checkpoint comparison | Subordinate specialist | No | Compare current findings to carried lineage |
| Data Normalizer | Terminology and structure cleanup | Subordinate specialist | No | Normalize names, data shapes, or reporting surfaces |
| Scenario Packet Compiler | Scenario framing and packet prep | Subordinate specialist | No | Prepare bounded packet views without widening scope |
| Review Analyst | Review-only code-path and behavior classification | Subordinate specialist | No | Confirm live behavior before suggesting correction |
| Simulation Briefing Agent | Simulation-safe framing | Subordinate specialist | No | Keep world-building/product logic intact during reviews |
| Fixture Reader | Frozen fixture reading | Subordinate specialist | No | Read fixture-oriented sources |
| Fixture Comparator | Fixture comparison | Subordinate specialist | No | Compare fixture states or example outputs |
| Fixture Diagnostics Consumer | Diagnostic surface reading | Subordinate specialist | No | Consume existing diagnostics only |
| Fixture Export Consumer | Export-surface reading | Subordinate specialist | No | Consume export artifacts only |
| Operator Surface v1 | Artifact/surface only | Non-authority | No | Never treat as command layer |

## 3. Role-use rule

The default team pattern for a narrow review lane should be:
- Carson for discovery
- Review Analyst for code-path confirmation
- Archivist for checkpoint comparison
- Simulation Briefing Agent for simulation-safe framing
- CTO Command Interface / Northbridge for final lane judgment and prompt output

## 4. External review rule

Claude, Grok, Copilot Pro, VBC, and Ollama may support:
- alternative readings
- bias checks
- adversarial review
- local prompt iteration
- cheap completion support

They do **not** become hidden control roles.

Their outputs must be routed back through the active control layer.

## 5. Sub-agent ledger rule

Whenever a lane refers to any named supporting role, the report should include a ledger entry or clear explanation of:
- whether the role actually executed work
- what it inspected
- whether its output was verified from repo evidence
- whether it touched repo state
- whether it produced only interpretation or a concrete finding

## 6. Anti-drift rule

Do not let named roles imply hidden authority.

Named roles help partition work.

They do not create new command tiers.

## 7. Archive note

v1 remains lineage.

v2 should replace it in the active default source rotation.
