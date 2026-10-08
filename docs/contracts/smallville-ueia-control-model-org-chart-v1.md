# SmallVille / UEIA Control Model Org Chart v1

## Operational Read

- Status: `accepted`
- Scope: `docs-only control-model formalization`
- Top control surface: `CTO Command Interface`
- Human gate: `Callum`
- Explorer lead: `Carson`
- Explorer-team continuity: `Russell remains part of the wider team`

## Purpose

This note formalizes the current SmallVille / UEIA control-model organization chart in committed repo docs.

It exists to make the following explicit:
- the CTO Command Interface is the sole top command-and-control layer in this working model
- Callum is the human operator gate with explicit pause / hold authority
- Carson is the main Explorer
- Russell remains part of the wider team
- no sub-agent protocol receives command-interface status, final authority, or seam-reopen authority
- existing repo roles remain valid only as subordinate or parallel specialist functions under the top control model

This note does not authorize:
- runtime mutation
- engine mutation
- prompt/runtime seam changes
- proposal/runtime seam reopening
- ChatService runtime-polish reopening
- `Garden: on` / object-state reopening
- toolchain mutation
- retrieval or memory-lattice redesign
- UI, workflow, or routing redesign

## Top Control Rule

- `CTO Command Interface` is the sole top command-and-control layer in this working model.
- `Callum` is the human operator gate in this working model.
- `Callum` may pause or hold active work at the management layer.
- Final control remains above all sub-agent protocols, specialist roles, read-only surfaces, and artifact consumers.
- No subordinate role, no support surface, and no protocol may present itself as the control room, command surface, or final authority.

## Explorer Rule

- `Carson` is the main Explorer.
- `Explorer` is a lead specialist role only.
- `Explorer` is not a command role.
- `Russell` remains part of the wider team as an explorer-team member within the wider specialist team.
- `Russell` does not hold explorer-lead status in this working model.

## Sub-Agent Restriction Rule

- No sub-agent protocol has command-interface status.
- No sub-agent may approve its own widening.
- No sub-agent may reopen blocked seams on its own.
- No sub-agent may override the human operator gate.
- No sub-agent may grant itself final approval authority.
- No sub-agent may be described as top control, command-and-control, the control room, or final authority.

## Existing Repo Role Placement

The following repo-grounded roles and surfaces remain valid, but only under the top control model defined above:

- `Executive Controller`
- `Archivist`
- `Data Normalizer`
- `Scenario Packet Compiler`
- `Review Analyst`
- `Simulation Briefing Agent`
- `Operator Surface v1`
- `Fixture Reader`
- `Fixture Comparator`
- `Fixture Diagnostics Consumer`
- `Fixture Export Consumer`

These remain subordinate or parallel specialist functions only.
They do not supersede the CTO Command Interface or the human operator gate.

## Role Classification Table

| Role or surface | Classification | Working-model placement |
| --- | --- | --- |
| CTO Command Interface | `top control authority` | Sole top command-and-control layer |
| Callum | `human gate authority` | Human operator gate with pause / hold authority |
| Carson | `lead specialist` | Main Explorer |
| Russell | `specialist team member` | Wider team member; non-command explorer-team continuity |
| Executive Controller | `lead specialist` | UEIA workflow lead role under top control |
| Archivist | `specialist team member` | Source and provenance specialist |
| Data Normalizer | `specialist team member` | Structured evidence specialist |
| Scenario Packet Compiler | `specialist team member` | Packet-compilation specialist |
| Review Analyst | `read-only support role` | Review-only evidence and compliance role |
| Simulation Briefing Agent | `specialist team member` | Brief-preparation specialist |
| Operator Surface v1 | `non-authority artifact or surface` | Read-only evidence surface above the engine |
| Fixture Reader | `subordinate protocol` | Read-only canonical fixture consumer profile |
| Fixture Comparator | `subordinate protocol` | Read-only canonical fixture comparison profile |
| Fixture Diagnostics Consumer | `subordinate protocol` | Read-only boundary-warning profile |
| Fixture Export Consumer | `subordinate protocol` | Read-only export profile |

## Compact Org Chart

```text
CTO Command Interface
  -> Callum (human operator gate; pause/hold authority)
  -> Carson (Explorer lead)
  -> Russell (wider team; non-command)
  -> Executive Controller (lead specialist)
  -> Archivist (specialist team member)
  -> Data Normalizer (specialist team member)
  -> Scenario Packet Compiler (specialist team member)
  -> Review Analyst (read-only support role)
  -> Simulation Briefing Agent (specialist team member)
  -> Operator Surface v1 (non-authority artifact or surface)
  -> Fixture Reader / Comparator / Diagnostics Consumer / Export Consumer (subordinate protocols)
```

## Compact Interaction Map

```text
CTO Command Interface -> sets top control boundary
Callum -> may pause/hold active work
Carson -> leads explorer work as a specialist, not as command authority
Russell -> contributes within the wider team, not as explorer lead or command authority
UEIA specialist roles -> perform bounded specialist work only
Operator Surface v1 -> inspects and exports evidence only
Fixture-consumer profiles -> consume canonical fixtures only
No sub-agent protocol -> may self-authorize widening, reopening, or final approval
```

## Boundary Preservation Rule

- This note preserves the UEIA role matrix, autonomy gates, human-review stop rules, and Operator Surface v1 read-only boundary.
- This note does not convert Operator Surface v1 into command-and-control.
- This note does not convert any specialist role into final authority.
- This note does not change runtime, engine, or proposal control behavior.

## Explicit Stop Conditions

Stop if any later pass tries to:
- give a sub-agent protocol command-interface status
- give a sub-agent protocol final approval authority
- give a sub-agent protocol seam-reopen authority
- move Operator Surface v1 into command-and-control
- remove Callum's human gate status without explicit replacement authority
- describe Russell as explorer lead in this working model
- weaken the distinction between top control, human gate, lead specialist, specialist team member, subordinate protocol, and non-authority surface
