# VB Operator Surface Scope

## Purpose

This directory is reserved for read-only Microsoft-native operator tooling that sits above the Smallville engine. In this phase, VB-side work should help operators inspect runs, review logs, and export evidence without mutating simulation logic or runtime state.

## In Scope

- Run-bundle viewer surfaces
- Log review surfaces
- CSV and Excel export helpers
- Operator status surfaces that summarize scenario, model, run id, and capture availability

## Out Of Scope

- Simulation logic
- Agent logic
- Proposal mutation or approval logic
- Engine configuration mutation
- Prompt editing
- Dashboard replacement
- Runtime packaging changes
- Executable VB tooling in this pass

## Operating Rule

VB-side work stays read-only until later approval. It may organize, display, filter, and export approved run evidence, but it must not become a second simulation layer.
