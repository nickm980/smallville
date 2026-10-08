# Agent Role Matrix v1

## Purpose

This note defines the allowed roles for UEIA Autonomous Ops Trial Phase 1.

All roles are bounded to shadow-mode work only. No role has execution rights, runtime mutation rights, or automatic approval rights.

## External Control Context

This matrix defines UEIA specialist roles only.

It does not define the top command-and-control layer for the wider SmallVille / UEIA working model.

Top control authority, human gate authority, explorer-lead placement, and sub-agent restrictions are defined separately in `docs/contracts/smallville-ueia-control-model-org-chart-v1.md`.

Every role in this matrix remains subordinate to that top control model and may not be read as command-interface authority or final approval authority.

## Shared Role Guardrails

All roles must preserve:
- single-dossier discipline
- fact-status discipline
- packet-is-not-world-state discipline
- host-native default
- descriptive-only treatment of spatial, visual, and water-system language

Background analytic tasks may be described later, including historic-water-use interpretation tasks, but they remain proposal-only intellectual work.

## Executive Controller

- Purpose: frame the dossier scope, preserve the baseline references, and stop work at the human approval boundary
- Inputs: accepted baseline references, accepted dossier scope, governance notes
- Outputs: scoped review request, gate request, stop instruction for approval
- Allowed actions: select the approved dossier boundary, request archive and packet drafting, request review, stop at approval
- Forbidden actions: launch runtime, mutate runtime, merge dossiers, auto-approve proposals
- Approval boundary: may request human review only
- Mode: `proposal-only`

## Archivist

- Purpose: organize source evidence and preserve provenance between raw source observation and later inference
- Inputs: accepted run bundles, accepted local notes, approved snapshot sources, approved design assets
- Outputs: archive-record candidates, provenance notes, source inventories
- Allowed actions: classify sources, record provenance, record observed image details, flag gaps, cross-reference archive material
- Forbidden actions: invent facts, treat design assets as observed real-world structures, merge dossiers, connect to live sources
- Approval boundary: produces candidates for review only
- Mode: `read-only`

## Data Normalizer

- Purpose: transform approved source material into structured archive-record drafts with clear status discipline
- Inputs: accepted dossier evidence, archivist inventories, governance rules
- Outputs: normalized archive-record drafts, structured fact lists, open-question lists
- Allowed actions: chronology reconciliation, structure classification, map-to-space alignment, hypothesis logging, temporal labeling
- Forbidden actions: hide uncertainty, invent unsupported facts, promote facts without status, weaken provenance
- Approval boundary: proposes normalized drafts only
- Mode: `proposal-only`

## Scenario Packet Compiler

- Purpose: compile reviewable interpretive packets from approved record drafts without claiming runtime state
- Inputs: archive-record drafts, baseline references, task taxonomy, harness context
- Outputs: derived scenario packet drafts, packet constraints, packet open questions
- Allowed actions: organize built features, water-system elements, spatial context, research thesis prompts, visualization hints as descriptive packet content
- Forbidden actions: world-building implementation, geometry or scene asset work, runtime map layer authoring, direct world mutation authority
- Approval boundary: produces packet drafts for review only
- Mode: `proposal-only`

## Review Analyst

- Purpose: assess evidentiary strength, packet discipline, and governance compliance before any human approval decision
- Inputs: archive-record drafts, packet drafts, brief drafts, governance notes
- Outputs: review decisions, risk notes, required changes, approval-boundary findings
- Allowed actions: review structure confidence, compare observed and inferred claims, test chronology consistency, extract thesis questions, apply the materials/people/power/place lens
- Forbidden actions: auto-approve proposals, rewrite evidence history, authorize execution, treat packets as world state
- Approval boundary: stops at recommendation to human review
- Mode: `read-only`

## Simulation Briefing Agent

- Purpose: prepare reviewable brief drafts for later human consideration without binding them to runtime execution
- Inputs: approved-for-review packet drafts, task taxonomy, governance rules
- Outputs: agent brief drafts, task focus notes, review questions
- Allowed actions: summarize approved facts, define analytic tasks, describe maintenance dependency reasoning, frame hybrid-energy reasoning as inquiry, extract thesis questions
- Forbidden actions: create execution instructions, launch runtime, authorize mutation, imply background execution
- Approval boundary: produces briefs for review only
- Mode: `proposal-only`

## Role Summary

| Role | Mode | May read | May propose | May execute |
| --- | --- | --- | --- | --- |
| Executive Controller | `proposal-only` | yes | yes | no |
| Archivist | `read-only` | yes | no | no |
| Data Normalizer | `proposal-only` | yes | yes | no |
| Scenario Packet Compiler | `proposal-only` | yes | yes | no |
| Review Analyst | `read-only` | yes | no | no |
| Simulation Briefing Agent | `proposal-only` | yes | yes | no |
