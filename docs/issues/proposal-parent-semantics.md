# Proposal Parent Semantics

Status: reviewed / deferred

## Stable Loop Reference
- Baseline tag: `smallville-stable-loop-01`
- Run id: `20260313-142225-tiny-world-llama3.2-3b-16k`
- Review source: `runs/20260313-142225-tiny-world-llama3.2-3b-16k/proposal_review.md`

## Observed Proposal Example

During Stable Loop 01 tick 3, Jamie produced the following pending proposal:

```text
add_location
parent = Green House: Water Barrel
name = Green House: Rain Barrel
state = half full
reason = to maintain a consistent watering schedule for Alex's plants.
```

## Current Behavior

The proposal system can currently surface `add_location` requests whose `parentLocation` resolves to a very narrow object-like place name rather than a broader spatial container. That allows proposals that are understandable in intent but awkward in structure, such as adding a new location under `Green House: Water Barrel`.

## Expected Parent-Location Contract For `add_location`

For reviewed scenario work, `add_location` should target an existing spatial container that can naturally contain another place. Practical examples include `Blue House`, `Green House`, `Garden`, or another room-like or area-like location that behaves as a container in the world map.

Object-level leaves such as `Water Barrel`, `Bench`, `Gate`, trays, tools, or other item-like nodes should not be treated as valid parents for `add_location`. If the requested change is really about a fixture, container, object, or state adjustment inside an existing place, it should be represented through a better-matched proposal shape in a later engine pass rather than forced into an `add_location` parent slot.

## Why This Was Deferred From Stable Loop 01

Stable Loop 01 was intentionally limited to parser repair, parser-focused tests, and host-native tiny-world validation. Proposal-parent semantics were observed during runtime review, but they were not blocking the approved parser checkpoint and were explicitly kept out of scope to avoid reopening engine work during the freeze.

## Acceptance Criteria For A Later Engine Fix

1. `add_location` proposals are rejected, downgraded, or redirected when the parent target is object-like rather than container-like.
2. Calm domestic and garden proposals that belong under `Blue House`, `Green House`, `Garden`, or other container locations remain possible.
3. Review artifacts still capture the original proposal intent clearly enough for auditing.
4. Tiny-world and later two-house-garden runs do not regress in action-log readability or proposal visibility.
5. The later fix includes tests covering valid container parents and invalid object-like parents.

## Non-Scope Note

No fix is applied here. This file is issue capture only and does not change engine behavior.
