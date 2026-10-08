# Isolated Dialogue Continuity Checkpoint

Date: 2026-03-18

## Scope

This checkpoint freezes the next bounded SmallVille memory seam only.

Included:
- one explicit continuity rule for the subject-null / isolated-dialogue branch inside `UpdateConversation`
- focused verification covering the preserved single-utterance behavior for the speaking agent

Explicitly excluded:
- broader conversation redesign
- broader writer expansion
- retrieval redesign or tuning
- reflection or semantic-memory promotion
- emotional-memory mechanics
- persistence or restart continuity
- triplet-memory architecture
- startup, launcher, or scenario work

## Accepted Baseline

Runtime branch-consistency seam commit:
- `a60b9b2` `feat(memory): define isolated-dialogue continuity rule`

This checkpoint records one bounded rule inside an already accepted writer surface. It does not freeze a broader conversation redesign or writer-management system.

## Exact Runtime Rule Now In Force

File:
- `smallville/src/main/java/io/github/nickm980/smallville/update/UpdateConversation.java`

Accepted behavior for the subject-null / isolated-dialogue branch:
- the returned utterance from `saySomething(...)` is preserved as one observation for the speaking agent
- the same utterance is preserved as one working-memory item for the speaking agent
- no `Conversation` record is created
- no partner memory or partner continuity is written

This seam affects only the isolated-dialogue branch inside `UpdateConversation`.

## Focused Verification

Test files:
- `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/MemoryStreamTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/UpdateServiceTest.java`

Coverage added or updated:
- proves isolated dialogue now preserves one utterance as an observation for the speaking agent
- proves the same utterance is also added as one working-memory item for the speaking agent
- proves no `Conversation` record is created for the isolated-dialogue branch
- proves adjacent direct-conversation, ambient-conversation, and working-memory lifecycle seams remain intact in the focused run

Focused verification result observed for this seam:
- `SimulationServiceTest`, `MemoryStreamTest`, and `UpdateServiceTest`: 28 tests passed, 0 failed in the focused run

## What Is Frozen

The isolated-dialogue continuity seam is now frozen as a narrow branch rule inside `UpdateConversation`:
1. one utterance is preserved in observations for the speaking agent
2. the same utterance is preserved in working memory for the speaking agent
3. no partner continuity or `Conversation` record is created

Together these freeze the isolated-dialogue branch behavior without widening into broader conversation redesign, broader writer expansion, retrieval changes, or semantic promotion.

## Remaining Out Of Scope

Still out of scope after this checkpoint:
- broader conversation redesign
- broader writer expansion beyond the already accepted writer surfaces
- retrieval redesign or tuning
- reflection or semantic-memory promotion
- emotional-memory mechanics
- persistence or restart continuity implementation
- triplet-memory architecture
- any broader writer-management redesign