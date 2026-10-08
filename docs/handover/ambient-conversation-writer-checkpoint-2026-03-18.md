# Ambient Conversation Writer Checkpoint

Date: 2026-03-18

## Scope

This checkpoint freezes the next bounded SmallVille memory seam only.

Included:
- one additional working-memory writer surface through `UpdateService.startAmbientConversation(...)`
- anti-spam handling that keeps the full ambient conversation transcript in observations while only adding the latest ambient dialog line to working memory
- focused verification covering the bounded ambient-conversation continuity behavior

Explicitly excluded:
- broader writer expansion beyond `UpdateCurrentActivity`, `UpdateConversation`, and `UpdateService.startAmbientConversation(...)`
- retrieval redesign or tuning
- reflection or semantic-memory promotion
- emotional-memory mechanics
- persistence or restart continuity
- triplet-memory architecture
- startup, launcher, or scenario work

## Accepted Baseline

Runtime writer-expansion seam commit:
- `0036781` `feat(memory): evaluate ambient conversation writer scope`

This checkpoint records one additional bounded writer expansion. It does not freeze a full writer-management system.

## Writer Scope Now In Force

The accepted working-memory writer scope is now:
1. `UpdateCurrentActivity`
2. `UpdateConversation`
3. `UpdateService.startAmbientConversation(...)`

### Activity-update-driven continuity

File:
- `smallville/src/main/java/io/github/nickm980/smallville/update/UpdateCurrentActivity.java`

Behavior already in force:
- `lastActivity` remains written into observations
- `lastActivity` is also written into working memory during ordinary activity updates

### Conversation-update-driven continuity

File:
- `smallville/src/main/java/io/github/nickm980/smallville/update/UpdateConversation.java`

Behavior already in force:
- full dialog history remains written into observations for both conversation participants
- only the latest dialog line from the current conversation update is added to working memory for both participants

### Ambient-conversation continuity

File:
- `smallville/src/main/java/io/github/nickm980/smallville/update/UpdateService.java`

Accepted behavior:
- full ambient conversation transcript remains written into observations for both participants
- only the latest ambient dialog line is added to working memory for both participants
- the existing working-memory dedupe and bounded-retention lifecycle remains responsible for refresh and trimming

This is the accepted anti-spam rule for the ambient conversation seam:
- full ambient dialog remains in observations
- only the latest ambient dialog line is added to working memory

## Focused Verification

Test files:
- `smallville/src/test/java/io/github/nickm980/smallville/UpdateServiceTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/MemoryStreamTest.java`

Coverage added or updated:
- proves ambient conversation still creates a conversation record and preserves the full transcript in observations
- proves ambient conversation adds only one latest-line continuity item to working memory for each participant
- proves the ambient-conversation writer seam composes with the existing dedupe and bounded-retention lifecycle rules

Focused verification result observed for this seam:
- `UpdateServiceTest`, `SimulationServiceTest`, and `MemoryStreamTest`: 27 tests passed, 0 failed in the focused run

## What Is Frozen

The accepted working-memory writer scope is now frozen at three bounded update surfaces:
1. activity-update-driven continuity through `UpdateCurrentActivity`
2. conversation-update-driven continuity through `UpdateConversation`
3. ambient-conversation continuity through `UpdateService.startAmbientConversation(...)`

Together these freeze the currently accepted writer scope without widening into retrieval, semantic promotion, emotional mechanics, persistence, or broader writer orchestration.

## Remaining Out Of Scope

Still out of scope after this checkpoint:
- broader writer expansion beyond the three accepted writer surfaces
- retrieval redesign or tuning
- reflection or semantic-memory promotion
- emotional-memory mechanics
- persistence or restart continuity implementation
- triplet-memory architecture
- any broader writer-management redesign