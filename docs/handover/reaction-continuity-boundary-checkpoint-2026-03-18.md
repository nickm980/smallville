# Reaction Continuity Boundary Checkpoint

Date: 2026-03-18

## Scope

This checkpoint freezes the next bounded SmallVille memory seam only.

Included:
- one bounded reaction prompt boundary correction
- focused verification covering the rendered reaction prompt surface

Explicitly excluded:
- broader prompt redesign
- broader writer expansion
- retrieval redesign or tuning
- planning-system redesign
- reflection or semantic-memory promotion
- emotional-memory mechanics
- persistence or restart continuity
- startup, launcher, or scenario work

## Accepted Baseline

Runtime seam commit:
- `6c497bd` `feat(memory): refine reaction continuity boundary`

This checkpoint records one bounded reaction prompt boundary rule. It does not freeze a broader prompt redesign, retrieval redesign, or writer-management redesign.

## Exact Runtime/Prompt Rule Now In Force

Files:
- `smallville/src/main/resources/prompts.yaml`
- `smallville/src/test/java/io/github/nickm980/smallville/PromptBuilderTest.java`

Accepted reaction boundary behavior:
- `agent.summary` continues to carry active continuity
- the reaction prompt now consumes `memories.relevant` as the string shape already built by `PromptBuilder`
- the raw `observation` remains present in the reaction prompt
- plan-change and conversation gating remain unchanged

This seam affects only the reaction prompt boundary. It does not widen the prompt surface beyond the accepted active-continuity model.

## Focused Verification

Test files:
- `smallville/src/test/java/io/github/nickm980/smallville/PromptBuilderTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`

Coverage added or confirmed:
- proves the rendered reaction prompt includes active continuity through `agent.summary`
- proves the rendered reaction prompt includes the relevant-memory text supplied through `memories.relevant`
- proves the rendered reaction prompt still includes the raw observation
- proves the adjacent simulation seam remains intact in the focused run

Focused verification result observed for this seam:
- `PromptBuilderTest`: 4 tests passed, 0 failed in the focused run
- `SimulationServiceTest`: 20 tests passed, 0 failed in the focused run

## What Is Frozen

The reaction continuity boundary is now frozen as a narrow prompt seam:
1. active continuity remains supplied through `agent.summary`
2. reaction relevant memories are rendered using the string shape already produced by `PromptBuilder`
3. raw observation input remains present
4. plan-change and conversation gating remain unchanged

Together these freeze the reaction prompt boundary without widening into broader prompt redesign, broader writer expansion, retrieval changes, planning redesign, or semantic promotion.

## Remaining Out Of Scope

Still out of scope after this checkpoint:
- broader prompt redesign
- broader writer expansion
- retrieval redesign or tuning
- planning-system redesign
- reflection or semantic-memory promotion
- emotional-memory mechanics
- persistence or restart continuity implementation
- startup, launcher, or scenario work