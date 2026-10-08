# Active Continuity Memory Checkpoint

Date: 2026-03-18

## Scope

This checkpoint freezes the first bounded memory-deepening seam only.

Included:
- explicit working-memory separation inside the existing `Agent -> MemoryStream` boundary
- bootstrap seeding of working memories into the explicit working-memory channel
- world snapshot separation of working memories from recent observation memories
- prompt-summary integration for active continuity through `agent.workingMemories`
- focused verification for the runtime seam and prompt integration closure

Explicitly excluded:
- working-memory lifecycle policy
- retrieval redesign or tuning
- reflection or semantic-memory deepening
- emotional-memory mechanics
- persistence or restart continuity architecture
- episodic/semantic/emotional triplet implementation
- startup, launcher, or packaged fallback work
- scenario content changes

## Accepted Baseline

Checkpoint commit message:
- `feat(memory): add explicit working-memory continuity channel`

This checkpoint records the first active-continuity seam as an implementation freeze, not as a broader memory-architecture redesign.

## Runtime Changes

### Memory container boundary

File:
- `smallville/src/main/java/io/github/nickm980/smallville/memory/MemoryStream.java`

What changed:
- added an explicit working-memory channel inside `MemoryStream`
- kept `Agent` owning one memory container
- kept generic retrieval compatibility by exposing working memories through the combined memory view

### Bootstrap and snapshot wiring

File:
- `smallville/src/main/java/io/github/nickm980/smallville/api/v1/SimulationService.java`

What changed:
- seeded working memories now enter the working-memory channel rather than the observation stream
- snapshot `workingMemories` now read from the explicit working-memory channel
- snapshot `recentMemories` remain observation-based

### Prompt integration closure

Files:
- `smallville/src/main/java/io/github/nickm980/smallville/prompts/TemplateMapper.java`
- `smallville/src/main/resources/prompts.yaml`

What changed:
- `TemplateMapper.buildAgentSummary(...)` now supplies `agent.workingMemories`
- the agent summary prompt now renders active continuity from that field

This closes the prompt-facing active-continuity gap that remained after the initial runtime seam landed.

## Focused Verification

Test files:
- `smallville/src/test/java/io/github/nickm980/smallville/MemoryStreamTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/PromptBuilderTest.java`

Coverage added or updated:
- proves working memories are distinct from observations while still visible to generic retrieval
- proves persona, working memory, and observations remain structurally distinct
- proves world snapshot exposes genuinely distinct `workingMemories` and `recentMemories`
- proves rendered agent summary includes active continuity when working memory is present

Focused verification results observed during this seam:
- `MemoryStreamTest`: passed in focused run
- `SimulationServiceTest`: passed in focused run
- `PromptBuilderTest`: 3 tests, 0 failures, 0 errors, 0 skipped

## What Is Frozen

The active-continuity seam is now frozen at three layers:
1. runtime storage separation inside `MemoryStream`
2. externally visible snapshot separation in `SimulationService`
3. prompt-summary integration through `TemplateMapper` and `prompts.yaml`

Together these freeze the first memory-deepening seam without widening into lifecycle policy or a broader memory architecture.

## Remaining Out Of Scope

Still out of scope after this checkpoint:
- working-memory lifecycle rules for add/replace/clear behavior
- triplet-aware retrieval or ranking changes
- semantic provenance or reflection-source linkage
- emotional traces, attachment mechanics, or ritual-anchor mechanics
- restart persistence or no-amnesia continuity implementation
- any broader memory-system redesign beyond the active-continuity seam