# SmallVille UEIA Control Model Org Chart v2

**Timestamp:** 2026-03-22 GMT

## 1. Purpose

This file gives the control hierarchy for the current SmallVille / UEIA / CTO working model.

It is an authority-map file, not a code or feature roadmap.

## 2. Top control hierarchy

### Level 0 — Human operator gate
- **Callum**
- explicit pause / hold authority
- may stop or redirect active work at the management layer

### Level 1 — CTO Command Interface
- sole top command-and-control model layer inside the AI stack
- responsible for authority loading, lane selection, drift control, and bounded handoff generation
- no subordinate role outranks this layer

### Level 1 alias — Northbridge
- `Northbridge` is the team-facing alias for the same `CTO Command Interface` layer
- it is not a new authority tier
- it does not create a second command surface
- it exists only to give the control layer a clearer team name

## 3. Explorer hierarchy

### Explorer lead
- **Carson** = main Explorer
- default repo-mapping and discovery specialist under the CTO layer

### Wider team, non-command
- **Russell** = valid wider-team role only
- not Explorer lead
- not a command layer
- not autonomous seam authority

## 4. Specialist roles under the CTO layer

These roles remain valid but subordinate:
- Executive Controller
- Archivist
- Data Normalizer
- Scenario Packet Compiler
- Review Analyst
- Simulation Briefing Agent
- Fixture Reader
- Fixture Comparator
- Fixture Diagnostics Consumer
- Fixture Export Consumer
- Operator Surface v1 as a non-authority artifact only

## 5. External review surfaces

These may support review, but do not become control layers:
- Codex app as primary repo execution surface
- VBC as oversight-only surface
- Copilot Pro as inline assist surface
- Claude as periodic creative review surface
- Grok as adversarial bias and alignment review surface
- Ollama as retained local ecosystem participant

## 6. Authority discipline

No sub-agent protocol, specialist role, operator surface, external model, fixture-consumer profile, or UI surface has:
- command-interface status
- final approval authority
- autonomous seam-reopen authority

## 7. Operating rule

Use this file to clarify who can:
- interpret authority
- select the next bounded lane
- reopen or keep closed a seam
- overrule subordinate drift

Default rule:
- Callum is the human gate
- CTO Command Interface / Northbridge is the active AI control layer
- all other roles are subordinate execution or interpretation surfaces

## 8. Archive note

v1 remains useful lineage.
v2 should replace it in the active default source rotation.
