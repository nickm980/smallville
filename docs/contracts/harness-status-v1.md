# Harness Status v1

## Promotion Freeze

This document records the current harness status after acceptance of the first reviewed `two-house-garden-v1` host-native runtime wave.

## Canonical Control Harness

- `tiny-world` remains the canonical control harness.
- Its role is to verify that the parser-frozen base loop still loads, starts, ticks, and shuts down cleanly before broader scenario review.
- `tiny-world` is not to be altered as part of protocol-harness promotion work unless separately approved.

## First Formal Protocol Harness

- `two-house-garden-v1` is promoted to the first formal protocol harness.
- The promotion is based on the accepted reviewed run bundle:
  - `runs/20260313-173414-two-house-garden-v1-reviewed-llama3.2-3b-16k`
- `two-house-garden-v1` is now the primary reviewed scenario for calm horticultural protocol work above the engine layer.

## Manual Proposal Review Requirement

- Proposal review remains manual.
- No automatic proposal approvals are allowed for `tiny-world` or `two-house-garden-v1`.
- Proposal-parent semantics remain an open engine issue and must stay under manual review until separately fixed and revalidated.

## Parser Freeze

- Parser scope remains frozen at tag `smallville-stable-loop-01`.
- Parser work must not be reopened as part of harness-promotion work.
- Future work on protocol harnesses, operator surfaces, or documentation must not broaden into parser changes unless separately approved.

## Runtime Baselines

- Stable Loop 01 baseline index:
  - `baselines/stable-loop-01/manifest.yaml`
- Accepted tiny-world control smoke:
  - `runs/20260313-172513-tiny-world-smoke-control-llama3.2-3b-16k`
- Accepted first reviewed `two-house-garden-v1` run:
  - `runs/20260313-173414-two-house-garden-v1-reviewed-llama3.2-3b-16k`

## Current Guardrails

- `tiny-world` stays unchanged as the parser-frozen control harness.
- `two-house-garden-v1` is the reviewed protocol harness.
- Proposal review remains manual.
- Docker remains deferred.
- All next operator work stays above the engine and read-only unless separately approved.
