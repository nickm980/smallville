# Local Ledger Export Contract v1

## Endpoint

```
GET /world/ledger/export
```

## Purpose

Returns a comprehensive, read-only snapshot of the entire simulation state suitable for offline analysis, governance audit, and replay bundle generation. This endpoint consolidates world state, governance history, and per-agent memory indexes into a single response.

## Response Shape

| Field | Type | Description |
|---|---|---|
| `generatedAtUtc` | string (ISO-8601) | UTC timestamp of export generation |
| `scenario` | string | Name of the loaded scenario configuration |
| `bootstrap` | object | Scenario bootstrap config (locations, world building rules, daily rhythm) |
| `world` | WorldSnapshotResponse | Current world snapshot (same shape as `GET /world`) |
| `governanceLedger` | array | Full unwindowed proposal history — includes all proposals regardless of tick window |
| `proposalHistoryFull` | array | Complete proposal history with review decisions |
| `memoryIndex` | object | Per-agent memory ledger index keyed by agent name |
| `offlinePolicy` | object | Offline mode and loopback isolation policy summary |
| `governancePolicy` | object | Governance policy summary |

## Governance Semantics

- `governanceLedger` is **unwindowed** — it includes every proposal ever submitted, not just those within the current tick window. This ensures governance audit completeness across restart boundaries.
- `proposalHistoryFull` mirrors governanceLedger but may include additional metadata about review decisions.
- Proposal ordering is preserved: entries appear in submission order.

## Memory Index

Each entry in `memoryIndex` is a `MemoryLedgerIndex`:

| Field | Type | Description |
|---|---|---|
| `agentName` | string | Agent identifier |
| `totalEvents` | integer | Total NDJSON journal entries for this agent |
| `hotCount` | integer | Memories in the hot/recent set after last dream cycle |
| `archivedCount` | integer | Memories compressed into dream packs |
| `dreamPackCount` | integer | Number of dream pack files |
| `lastDreamTick` | integer | Tick of the most recent dream cycle |
| `lastRecallTick` | integer | Tick of the most recent recall operation |
| `lastAffect` | AffectState | Agent's affect state after the last dream cycle |

## Schema

Validated by [`schemas/ledger-export.schema.yaml`](../../schemas/ledger-export.schema.yaml).

## Usage in Harness

The harness captures this endpoint at two points:
1. **Cold start** → `endpoint_ledger_export_cold.json`
2. **After final tick** → `endpoint_ledger_export_final.json`

Comparing cold vs. final exports reveals governance ledger growth, dream cycle activity, and affect state evolution.

## Offline Policy

When `offlineMode: true` is set in config, the export includes policy confirmation:
- `offlineMode: true` — no external API calls permitted
- `loopbackOnly: true` — only loopback API paths accepted

## Version

v1 — introduced with Local Ledger Export + Dreaming Memory Governance.
