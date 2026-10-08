# Dreaming Memory Governance v1

## Overview

The dreaming phase is a deterministic, LLM-free memory governance mechanism that runs periodically during the simulation. It scores, partitions, compresses, and archives agent memories while deriving an affect state that reflects the agent's current internal disposition.

## Dream Cycle

### Eligibility

A dream cycle fires for an agent when:
1. The simulation time is within the configured dream window (`dreamWindowStart` .. `dreamWindowEnd`, default 22:00–06:00)
2. At least `dreamIntervalTicks` ticks have elapsed since the agent's last dream cycle

### Process

1. **Persona anchor extraction** — Build a reference text from the agent's traits, persona characteristics, goals, rituals, and social preference.
2. **Memory scoring** — Score all non-working observations and plans against the persona anchor using the existing `Memory.getScore()` mechanism (recency + importance + relevancy via BERT embeddings).
3. **Hot/cold partitioning** — The top N memories by score (up to `hotMemoryLimit`, default 20) become the hot set. The remainder form the cold set.
4. **Dream pack compression** — Cold memories are compressed into a `DreamPack`:
   - Summary text: concatenation of the top 5 cold memory descriptions
   - Source hashes: SHA-256 of each compressed memory's description
   - Retention reasons: mapping from hash to categorized reason (social, routine, observation, plan)
5. **Affect derivation** — Compute `AffectState` from hot memory statistics:
   - Valence: `clamp((avgImportance / 5.0) - 1.0, -1, 1)`
   - Activation: `clamp(avgImportance / 10.0, 0, 1)`
   - Social drive: ratio of hot memories mentioning social words
   - Mood label: valence bucket (content ≥0.5, calm ≥0.1, neutral ≥-0.1, uneasy ≥-0.5, distressed <-0.5)
   - Focus target: description of the highest-scored hot memory
6. **Journal write** — Append a `dream-compress` entry to the NDJSON journal with the dream pack summary
7. **Index update** — Update `MemoryLedgerIndex` with new counts, last dream tick, and last affect

### Configuration (MemorySeed)

| Field | Default | Description |
|---|---|---|
| `dreamIntervalTicks` | 10 | Minimum ticks between dream cycles |
| `dreamWindowStart` | "22:00" | Start of the dream eligibility window |
| `dreamWindowEnd` | "06:00" | End of the dream eligibility window (handles midnight wrap) |
| `hotMemoryLimit` | 20 | Maximum memories retained in the hot set |
| `recallTopK` | 5 | Number of results returned by recall queries |
| `archiveCompression` | true | Whether to write dream packs as gzip JSON |

## Recall Order

The `RecallService` searches memory layers in priority order:

1. **Working memory** — currently active memories
2. **Hot/recent memories** — retained from last dream cycle
3. **Dream pack summaries** — compressed summaries with word-overlap scoring
4. **Archived raw memories** — original descriptions (if still on disk)

The best match is promoted to working memory and a `RecallEvent` is logged to the journal.

## Journal Format

### NDJSON Events

Events are appended to `.smallville-memory/agents/<name>/events.ndjson`, one JSON object per line:

```json
{"id":"<uuid>","agentName":"Jamie","tick":15,"timestamp":"2026-03-31T22:00:00","type":"dream-compress","description":"Dream pack summary...","sourceHash":"<sha256>","importance":0.0,"retentionReason":"dream-cycle-archive"}
```

### Event Types

| Type | Description |
|---|---|
| `observation` | Memory observed from simulation events |
| `plan` | Planned action or intention |
| `reflection` | Synthesized reflection (future) |
| `characteristic` | Persona characteristic loaded from config |
| `recall` | Memory recalled via RecallService |
| `dream-compress` | Dream cycle compression event |
| `ponder` | Ponder nudge reflection during break window |

### Index File

`index.json` in each agent's directory contains `MemoryLedgerIndex` — a summary of event counts, dream state, and last affect. Used for restart reload.

### Dream Packs

Stored under `dream-packs/<pack-id>.json.gz` (or `.json` if compression disabled). Each contains a `DreamPack` object.

## Restart Continuity

On server restart, `SimulationService.restoreMemoryLedgers()`:
1. Loads `index.json` for each agent
2. Restores `lastAffect` to the agent's affect state
3. Loads recent observation entries back into the memory stream
4. Restores `lastDreamTick` to the dreaming service

This ensures dream cycle timing, affect state, and memory governance continue seamlessly across restarts.

## Integration Point

The dream cycle runs in `SimulationService.updateState()` after all agent updates complete for the current tick. Each agent is checked independently; failures are caught and logged without blocking other agents.

## Schemas

- [affect-state.schema.yaml](../../schemas/affect-state.schema.yaml)
- [dream-pack.schema.yaml](../../schemas/dream-pack.schema.yaml)
- [agent-memory-ledger.schema.yaml](../../schemas/agent-memory-ledger.schema.yaml)
- [ledger-export.schema.yaml](../../schemas/ledger-export.schema.yaml)

## Version

v1 — dream-only affect derivation. No real-time nudges from conversation or observation events. Nudge affect links are planned as the next lane after v1.
