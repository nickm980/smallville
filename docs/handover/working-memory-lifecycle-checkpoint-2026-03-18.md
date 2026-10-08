# Working Memory Lifecycle Checkpoint

Date: 2026-03-18

## Scope

This checkpoint freezes the next bounded SmallVille memory seam only.

Included:
- lightweight working-memory lifecycle policy inside the existing `MemoryStream` working-memory channel
- ordinary update-path refresh through `UpdateCurrentActivity`
- focused tests covering deduplication, bounded trimming, and activity-update-driven refresh behavior

Explicitly excluded:
- retrieval redesign or ranking changes
- broader working-memory writer expansion beyond the current activity-update surface
- reflection or semantic-memory promotion
- emotional-memory mechanics
- persistence or restart continuity
- triplet-memory architecture
- startup, launcher, or scenario work

## Accepted Baseline

Runtime lifecycle seam commit:
- `99c6444` `feat(memory): add working-memory lifecycle policy`

This checkpoint records a narrow lifecycle rule for active continuity. It does not freeze a full working-memory management system.

## Runtime Lifecycle Rule

### MemoryStream policy

File:
- `smallville/src/main/java/io/github/nickm980/smallville/memory/MemoryStream.java`

Implemented rule:
- working memories are added through the explicit working-memory channel
- duplicate entries are deduplicated by description
- a repeated entry refreshes the channel by replacing the older matching entry
- the working-memory channel is bounded to a small maximum size
- stale entries are cleared by trimming the oldest entry when that bound is exceeded

This is a deterministic, bounded lifecycle rule. It is not a semantic, emotional, or retrieval-driven policy.

### Current writer surface in scope

File:
- `smallville/src/main/java/io/github/nickm980/smallville/update/UpdateCurrentActivity.java`

Current in-scope writer behavior:
- `lastActivity` continues to be written into observations
- `lastActivity` is also written into working memory during ordinary activity updates

This makes the current lifecycle seam primarily activity-update-driven.

## Focused Verification

Test files:
- `smallville/src/test/java/io/github/nickm980/smallville/MemoryStreamTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`

Coverage added or updated:
- proves working memory deduplicates refreshed entries by description
- proves the bounded channel trims stale oldest entries when full
- proves ordinary `UpdateCurrentActivity` refresh behavior updates working memory over time without collapsing observations into the working-memory channel

Focused verification result observed for this seam:
- `MemoryStreamTest` and `SimulationServiceTest`: 24 tests passed, 0 failed in the focused run

## What Is Frozen

The working-memory lifecycle seam is now frozen at two layers:
1. bounded runtime lifecycle rule inside `MemoryStream`
2. current writer surface in scope through `UpdateCurrentActivity`

Together these freeze the current lifecycle seam without widening into retrieval, semantic promotion, emotional mechanics, or broader writer policy.

## Remaining Out Of Scope

Still out of scope after this checkpoint:
- retrieval tuning or redesign
- broader writer expansion beyond `UpdateCurrentActivity`
- reflection or semantic-memory promotion
- emotional-memory mechanics
- persistence or restart continuity implementation
- triplet-memory architecture
- any broader memory-management redesign