# Agent Memory Ledger Contract v1

## Endpoint

```
GET /agents/{name}/memory-ledger
```

## Purpose

Returns a detailed view of a single agent's memory governance state, including persona anchors, current affect, memory layers, dream packs, and recall policy. Designed for per-agent inspection and debugging.

## Path Parameters

| Parameter | Type | Description |
|---|---|---|
| `name` | string | Agent name (case-sensitive, URL-encoded if necessary) |

## Response Shape

| Field | Type | Description |
|---|---|---|
| `agent` | string | Agent name |
| `personaAnchors` | PersonaAnchors | Traits, goals, rituals, persona characteristics, social preference |
| `affect` | AffectState | Current affect state from the last dream cycle |
| `working` | array of string | Current working memory descriptions |
| `recent` | array of string | Hot/recent memory descriptions retained after last dream cycle |
| `archived` | array of string | Descriptions archived into dream packs |
| `dreamPacks` | array of DreamPack | Compressed dream packs with summaries and source hashes |
| `recallPolicy` | object | Recall configuration from the scenario memory seed |

## PersonaAnchors

| Field | Type | Description |
|---|---|---|
| `traits` | string | Comma-separated trait descriptors |
| `goals` | array of string | Agent goals from scenario config |
| `rituals` | array of string | Agent rituals from scenario config |
| `persona` | array of string | Persona characteristic descriptions |
| `socialPreference` | string | Social preference: "solitary", "balanced", or "social" |

## Affect State

See [affect-state.schema.yaml](../../schemas/affect-state.schema.yaml) for the full shape. Key fields:

- `moodLabel` — one of: content, calm, neutral, uneasy, distressed
- `valence` — -1.0 to 1.0 (derived from average importance of hot memories)
- `activation` — 0.0 to 1.0 (derived from importance intensity)
- `socialDrive` — 0.0 to 1.0 (derived from social memory ratio)
- `focusTarget` — description of highest-scored hot memory (nullable)
- `drivers` — list of descriptions that contributed to the affect derivation

## Memory Layers

Memory is organized into layers with distinct retention semantics:

1. **Working** — actively held memories used in current prompts
2. **Recent/Hot** — top-scored memories retained after dream cycle (up to `hotMemoryLimit`)
3. **Archived** — raw descriptions from memories compressed into dream packs

## Error Responses

| Status | Condition |
|---|---|
| 404 | Agent with given name not found |

## Schema

Validated by [`schemas/agent-memory-ledger.schema.yaml`](../../schemas/agent-memory-ledger.schema.yaml).

## Version

v1 — introduced with Local Ledger Export + Dreaming Memory Governance.
