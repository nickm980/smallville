# Autonomy Gates v1

## Purpose

This note defines the gate model for UEIA Autonomous Ops Trial Phase 1.

It keeps proposal work and approval work separate and preserves the rule that execution is not approved in this phase.

## Gate Model

| Gate | Name | Phase 1 status | Purpose |
| --- | --- | --- | --- |
| L0 | Observe | `accepted in scope` | Review one constrained dossier and record source evidence without mutation |
| L1 | Normalize | `accepted in scope` | Convert source evidence into archive-record drafts with explicit fact status |
| L2 | Package | `accepted in scope` | Compile interpretive packet and brief drafts from approved record candidates |
| L3 | Propose | `accepted in scope` | Assemble proposal and review packs for human review |
| L4 | Human Approval | `mandatory` | Human decision point for any later action outside Phase 1 |
| L5 | Execution | `deferred` | Out of scope for this phase |

## Phase 1 Scope Rule

Only L0 through L3 are potentially in scope during Phase 1.

L4 remains mandatory.

L5 is not approved in this phase.

## Gate Descriptions

### L0 Observe

- Inputs: accepted single dossier, accepted snapshot sources
- Outputs: source inventory, provenance notes, observed fact candidates
- Rights: read only
- Not allowed: runtime launch, runtime mutation, live connectors, dossier merge

### L1 Normalize

- Inputs: observed source inventory, provenance notes
- Outputs: archive-record drafts, structured fact lists, open-question lists
- Rights: proposal drafting only
- Not allowed: silent repair, unsupported fact promotion, weakening provenance

### L2 Package

- Inputs: reviewed record drafts, harness context, task taxonomy
- Outputs: derived scenario packet drafts, agent brief drafts
- Rights: proposal drafting only
- Not allowed: direct world or scene mutation authority, geometry work, world-state claims

### L3 Propose

- Inputs: packet drafts, brief drafts, review findings
- Outputs: proposal pack, review pack, decision-ready submission
- Rights: propose only
- Not allowed: automatic approval, execution handoff without L4

### L4 Human Approval

- Inputs: proposal and review packs
- Outputs: human decision
- Rights: human approval boundary only
- Not allowed: automatic substitution by a Phase 1 role

### L5 Execution

- Inputs: not in scope for this phase
- Outputs: none in Phase 1
- Rights: deferred
- Not allowed: any automated transition from L3

## No Automated Path Rule

There is no approved automated path from L3 to L5.

No gate in Phase 1 authorizes runtime mutation or automatic approval.

## Packet-Is-Not-World-State Rule

Any packet produced at L2 or L3 is a reviewable interpretive artifact only.

It is not runtime world state and does not authorize direct world or scene mutation.
