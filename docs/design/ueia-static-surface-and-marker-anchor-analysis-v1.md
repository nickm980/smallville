# UEIA Static Surface & Marker Anchor Analysis v1

**Date**: 2026-04-01  
**Status**: Design proposal — not implementation authority  
**Authority**: Callum Gate (human hold/release) · CTO Command Interface / Northbridge (sole top AI control layer)  
**Scope**: Review-and-design only. No code edits. No repo mutations.

**Live reference surfaces**:

| Repo | Commit | Purpose |
|------|--------|---------|
| [walz333/smallville-tinyworld](https://github.com/Walz333/smallville-tinyworld) | `ba57ec4` | Primary simulation codebase |
| [walz333/array-concat-test](https://github.com/Walz333/array-concat-test) | `79ac0b0` | Compile-time const-array concatenation reference |

---

## 1. Cross-Repo Context

### array-concat-test

A Rust `#![no_std]` crate (v0.5.5, forked from `inspier/array-concat`) providing three macros for compile-time array manipulation:

- **`concat_arrays!(A, B, ...)`** — Concatenates const arrays into a single const array at compile time. Uses `#[repr(C)]` unions with `ManuallyDrop` to reinterpret memory layout. Zero runtime cost; size correctness is enforced by the compiler.
- **`concat_arrays_size!(A, B, ...)`** — Computes total element count at compile time. Expands to `0 + A.len() + B.len() + ...`.
- **`split_array!(arr, n1, n2, ...)`** — Splits a const array into a tuple of sub-arrays at compile time.

**Key guarantee**: If the composition compiles, the sizes are correct. There is no runtime check, no allocation, and no fallibility. This is the property that makes it interesting as a stability anchor — frozen data that cannot silently drift because the compiler enforces the composition.

### smallville-tinyworld

A Java 17 generative-agent simulation server with supporting layers:

| Language | Share | Role |
|----------|-------|------|
| Java | 68% | Simulation core, REST API, memory ledger, UEIA governance |
| PowerShell | 20% | Harness scripts, operator surface, run orchestration |
| TypeScript | 8% | Next.js dashboard |
| Python | 2% | Contract tests, schema validation |
| JavaScript | 1% | Legacy client SDK |
| Rust | 0% | Not present in repo today |

The architecture is host-authoritative (Java owns truth) with a sidecar validation layer (Python + YAML schemas) and an operator control surface (PowerShell + VB). The UEIA governance scaffold defines 23 frozen seams, each pinned to a specific commit hash.

### The Bridging Question

Where does frozen static composition fit in a system where:
- The primary language (Java) has `Set.of()`, `List.of()`, and enums but no compile-time concatenation primitive?
- The validation layer (YAML schemas) is inherently declarative but not compiled?
- The operator layer (PowerShell) is script-based with no compile step?
- Rust is not yet present in the repository?

This report evaluates compile-time concatenation as **one candidate mechanism** among several (Java immutable collections, annotation-processor generation, schema registries, build-plugin constant generation). Rust is not assumed as the final enforcement layer.

---

## 2. Immediate Anchor Candidates

These surfaces are classified as **good fit** for frozen static composition. Ranked by recommended adoption priority.

### Rank 1: MemoryLedgerEntry.type Vocabulary — CENTRAL REVIEW CASE

| Attribute | Value |
|-----------|-------|
| **Location** | `MemoryLedgerEntry.java` line 19 |
| **Current state** | Bare `String` field. Seven values documented only as a code comment: `observation \| plan \| reflection \| characteristic \| recall \| dream-compress \| ponder` |
| **Duplication** | String literals scattered across `MemoryJournalWriterTest.java`, `SimulationService.java`, and other test/source files |
| **Classification** | **Good fit** |
| **Why** | This is the highest-drift-risk vocabulary in the codebase. There is no enum, no `Set.of()` constant, no schema validation, and no compile-time enforcement. The only documentation is a code comment. A new developer adding a type (or misspelling one) would encounter zero compiler or test resistance. Any centralization mechanism — Java enum, `Set.of()` constant, or generated frozen table — would be a stability improvement. |

### Rank 2: ALLOWED_PROPOSAL_TYPES

| Attribute | Value |
|-----------|-------|
| **Locations** | `SimulationService.java` line 75, `ChatService.java` line 42 |
| **Current state** | `Set.of("add_location", "add_object", "change_state")` — identical in both files |
| **Classification** | **Good fit** |
| **Why** | Actively duplicated across two files with no shared constant. If one file is updated and the other is not, proposal parsing silently diverges. A single source of truth (shared constant, generated table, or schema-derived enum) eliminates this class of drift. |

### Rank 3: OBJECT_LIKE_ADD_LOCATION_PARENT_TOKENS

| Attribute | Value |
|-----------|-------|
| **Locations** | `SimulationService.java` lines 76–92, `WorldModel.java` lines 14–30 |
| **Current state** | `Set.of("bench", "barrel", "table", "tray", "gate", "tool", "tools", "shelf", "shelves", "pot", "pots", "planter", "planters", "rack", "racks")` — identical 15-element set in both files |
| **Classification** | **Good fit** |
| **Why** | The largest duplicated frozen constant in the codebase. Fifteen string tokens that must stay synchronized across two independent files. Scenario expansion (Lane D relationship dynamics, new object types) will likely grow this set, compounding drift risk. |

### Rank 4: Canonical Fixture DEFINITIONS

| Attribute | Value |
|-----------|-------|
| **Location** | `CanonicalFixtures.java` lines 14–20 |
| **Current state** | `Collections.unmodifiableList(List.of(...))` containing 4 `FixtureDefinition` objects mapped to file paths under `docs/examples/`. Inner `FixtureRole` enum: `ARCHIVE_RECORD`, `DERIVED_PACKET`, `AGENT_BRIEF`, `REVIEW_DECISION` |
| **Frozen by** | UEIA Seam 7 |
| **Classification** | **Good fit** |
| **Why** | Already the exemplar of the pattern — immutable list, frozen by governance seam, four well-defined entries. This surface demonstrates that the codebase already practices frozen static composition using Java's `Collections.unmodifiableList()`. The question is whether additional enforcement (generated constants, compile-time verification, or schema-backed validation) adds meaningful protection beyond what the current Java immutable collection provides. |

---

## 3. Secondary / Generated Candidates

These surfaces are classified as **possible fit**. They could benefit from frozen static composition but require additional justification or infrastructure before adoption.

### Rank 5: Schema Enum Vocabularies

| Attribute | Value |
|-----------|-------|
| **Locations** | `schemas/affect-state.schema.yaml` (`moodLabel`: content, calm, neutral, uneasy, distressed), `schemas/review-decision.schema.yaml` (`review_scope`: record, packet, brief, proposal-pack; `gate_level`: L0–L5), `schemas/endpoint-capture.schema.yaml` (`method`: GET, POST, PUT, PATCH, DELETE) |
| **Current state** | YAML `enum` arrays — declarative, not compiled |
| **Classification** | **Possible fit** |
| **Why** | These are frozen vocabularies defined in YAML schemas and separately implemented in Java code (e.g., `AffectState.moodFromValence()` returns the same 5 mood labels). A generated constant table produced at build time from the YAML source could validate schema-vs-code alignment automatically. However, this requires build tooling that does not exist today, and the current vocabularies are small enough that manual review has been sufficient. |

### Rank 6: UEIA Seam Hash Registry

| Attribute | Value |
|-----------|-------|
| **Location** | `docs/contracts/ueia-ladder-phase-map-v1.md` — 23 commit hashes |
| **Current state** | Markdown table only. Human-readable but not machine-readable. |
| **Classification** | **Possible fit** |
| **Why** | A machine-readable frozen array of seam hashes could serve as a lineage anchor — enabling automated verification that the current HEAD descends from all 23 governance checkpoints. However, seam hashes are documentation-plane artifacts, not code-plane. Converting them to a compiled constant risks conflating governance documentation with build artifacts. A generated YAML or JSON registry may be more appropriate than a compiled table. |

### Rank 7: Social Preference Vocabulary

| Attribute | Value |
|-----------|-------|
| **Location** | `SimulationService.java` line 1436 |
| **Current state** | `Set.of("solitary", "balanced", "social")` — single location, used in `normalizeSocialPreference()` |
| **Classification** | **Possible fit** |
| **Why** | Small set (3 values), currently in a single location (no duplication). However, Lane D (Relationship Dynamics Seed) is the stated strategic direction and will likely expand this vocabulary. If the set grows or appears in a second file, it crosses the threshold into "good fit". Worth monitoring but not worth centralizing yet. |

### Rank 8: Agent Role Matrix Roles

| Attribute | Value |
|-----------|-------|
| **Location** | `docs/control-room/source-packs/2026-03-22/agent-role-matrix-v2__2026-03-22_GMT.md` — 11 roles across 2 authority tiers |
| **Current state** | Markdown governance document. Not referenced in code. |
| **Classification** | **Possible fit** |
| **Why** | Governance metadata frozen by document authority. A const registry could anchor role validation if agent roles are ever checked in code. Today they are human-readable governance references only — no code depends on the role names. Converting to a frozen constant would only be justified if a future UEIA seam introduces programmatic role checks. |

---

## 4. Explicit Non-Targets

These surfaces are classified as **bad fit**. Frozen static composition is inappropriate here and must not be applied.

### FixtureConformanceClassification Enum

| Attribute | Value |
|-----------|-------|
| **Location** | `FixtureConformanceClassification.java` — 3 values: `ALLOWED`, `ALLOWED_WITH_STOP_RULES`, `NOT_ALLOWED` |
| **Why bad fit** | Already a proper Java enum with frozen vocabulary. The enum keyword itself provides compile-time enforcement. Adding a concatenation or generation layer on top would add indirection with no stability benefit. |

### Daily Rhythm Seed Defaults

| Attribute | Value |
|-----------|-------|
| **Location** | `SimulationFile.java` `DailyRhythmSeed` inner class — 7 default time windows (breakfast, lunch, dinner, morningTea, afternoonTea, eveningWind, snack) |
| **Why bad fit** | These are configuration defaults that are intentionally overridden at runtime by `simulation.yaml`. They are not frozen vocabulary — they are fallback values. Freezing them as compile-time constants would contradict their purpose as overridable defaults. |

### Affect State Runtime Values

| Attribute | Value |
|-----------|-------|
| **Location** | `AffectState.java` — `valence` (double, -1.0 to 1.0), `activation` (double, 0.0 to 1.0), `socialDrive` (double, 0.0 to 1.0) |
| **Why bad fit** | Mutable runtime doubles that change every tick via dream, ponder, and evening-wind nudges. This is the exact category of data that compile-time anchors must not pretend to replace. The *vocabulary* of mood labels (content, calm, neutral, uneasy, distressed) derived from these values is a schema enum (covered in §3 Rank 5); the *values themselves* are fundamentally mutable. |

### Agent Memory Contents

| Attribute | Value |
|-----------|-------|
| **Location** | Agent memory layers — working, recent, archived (`List<String>` in `AgentMemoryLedgerResponse`) |
| **Why bad fit** | Runtime neural memory. Grows, compresses, and mutates through dream cycles and ponder events. No frozen representation is possible or desirable. |

### World Locations and Object States

| Attribute | Value |
|-----------|-------|
| **Location** | `World.java` — location tree, object states, agent positions |
| **Why bad fit** | Mutable world-state memory. Changes every tick through proposal review, agent actions, and state mutations. Freezing any of this would break the simulation. |

---

## 5. Memory-Plane Separation

Every surface identified in this report belongs to exactly one of four memory planes. This classification determines which surfaces are eligible for frozen static composition and which are not.

### Control-Plane Markers

Governance-level artifacts that define the rules of the system. Change only through explicit governance authority (UEIA seams, Callum Gate decisions).

| Surface | Evidence | Eligible for Freezing? |
|---------|----------|----------------------|
| UEIA seam hash registry (23 hashes) | `ueia-ladder-phase-map-v1.md` | Yes — already frozen by governance |
| Conformance classifications (3 values) | `FixtureConformanceClassification.java` | Yes — already a Java enum |
| Fixture consumer profiles (4 profiles) | `FixtureConsumerProfile.java` | Yes — already a Java enum |
| Agent Role Matrix (11 roles) | `agent-role-matrix-v2` governance doc | Yes — frozen by document authority |
| 8 unresolved conformance cells | Frozen baseline (2026-03-16) | No — deliberately unresolved; freezing would infer resolution |

### Bootstrap / Static Metadata

Configuration-like data that is set once at system load time and does not change during simulation runtime.

| Surface | Evidence | Eligible for Freezing? |
|---------|----------|----------------------|
| Canonical fixture DEFINITIONS (4 entries) | `CanonicalFixtures.java`, Seam 7 | Yes — already `Collections.unmodifiableList()` |
| ALLOWED_PROPOSAL_TYPES (3 values) | `SimulationService.java` L75, `ChatService.java` L42 | Yes — duplicated, should be single source |
| OBJECT_LIKE_PARENT_TOKENS (15 values) | `SimulationService.java` L76–92, `WorldModel.java` L14–30 | Yes — duplicated, should be single source |
| Daily rhythm seed defaults (7 windows) | `SimulationFile.java` `DailyRhythmSeed` | No — intentionally overridable |

### Neural / Runtime Memory

Data that is created and consumed during simulation execution. The *type vocabulary* that classifies this data may be frozen, but the *data itself* is inherently mutable.

| Surface | Evidence | Eligible for Freezing? |
|---------|----------|----------------------|
| MemoryLedgerEntry.type vocabulary (7 values) | `MemoryLedgerEntry.java` L19 (comment-only) | **Yes — the vocabulary is frozen; the entries are not** |
| Social preference vocabulary (3 values) | `SimulationService.java` L1436 | Conditionally — if vocabulary grows or duplicates |
| Schema enum vocabularies (multiple) | `schemas/*.schema.yaml` | Conditionally — for schema-vs-code alignment validation |

### Mutable World-State Memory

Data that changes every tick. Never eligible for freezing.

| Surface | Evidence | Eligible for Freezing? |
|---------|----------|----------------------|
| Affect state values (valence, activation, socialDrive) | `AffectState.java` | No |
| Agent memory contents (working/recent/archived) | `AgentMemoryLedgerResponse` | No |
| World locations and object states | `World.java` | No |
| Agent positions and current activities | `Agent.java` | No |
| Plan contents (SHORT_TERM, MID_TERM, LONG_TERM) | `Memory.java` plan lists | No |

---

## 6. UEIA Marker Architecture

Draft specification for six Unix-side marker types used in agentic folder workflows. Each marker is a small structured file placed in a task directory to signal lifecycle state.

### Marker Format

```
marker-type.marker
```

Each marker is a UTF-8 text file containing a YAML document with three required invariants and marker-specific fields.

### Required Invariants (Present on Every Marker)

| Invariant | Type | Description |
|-----------|------|-------------|
| `authority_origin` | string | Who created this marker. One of: human name (e.g., `callum`), agent role ID (e.g., `cto-command-interface`), or specific agent name. |
| `lifecycle_status` | string | Current state from the marker's status vocabulary (defined per marker type below). |
| `lineage_pointer` | string \| null | ID or commit hash of the parent marker or UEIA seam in the provenance chain. `null` only for root requests with no parent. |

### Marker Types

#### request.marker

Signals a new task request from a human or upstream agent.

| Field | Type | Description |
|-------|------|-------------|
| `authority_origin` | string | Callum Gate or Northbridge |
| `lifecycle_status` | enum | `open`, `accepted`, `rejected` |
| `lineage_pointer` | string \| null | Parent request ID, or `null` for root requests |
| `request_id` | string | Unique identifier for this request |
| `summary` | string | Human-readable task description |
| `created_at` | ISO-8601 | Timestamp of request creation |

#### plan.marker

Signals a plan has been produced and is awaiting review.

| Field | Type | Description |
|-------|------|-------------|
| `authority_origin` | string | Agent that produced the plan |
| `lifecycle_status` | enum | `draft`, `approved`, `superseded` |
| `lineage_pointer` | string | request.marker ID that triggered this plan |
| `plan_id` | string | Unique identifier for this plan |
| `step_count` | integer | Number of planned steps |
| `created_at` | ISO-8601 | Timestamp of plan creation |

#### execution.marker

Signals active execution is underway.

| Field | Type | Description |
|-------|------|-------------|
| `authority_origin` | string | Agent executing the plan |
| `lifecycle_status` | enum | `running`, `paused`, `completed`, `failed` |
| `lineage_pointer` | string | plan.marker ID being executed |
| `execution_id` | string | Unique identifier for this execution |
| `started_at` | ISO-8601 | Timestamp of execution start |
| `current_step` | integer \| null | Current step index, or `null` if not step-based |

#### result.marker

Signals execution complete, result available for review.

| Field | Type | Description |
|-------|------|-------------|
| `authority_origin` | string | Agent that completed execution |
| `lifecycle_status` | enum | `pending_review`, `accepted`, `rejected` |
| `lineage_pointer` | string | execution.marker ID that produced this result |
| `result_id` | string | Unique identifier for this result |
| `artifact_paths` | list\<string\> | Paths to output artifacts relative to task directory |
| `completed_at` | ISO-8601 | Timestamp of result production |

#### hold-fail.marker

Signals a Callum Gate hold or an execution failure requiring intervention.

| Field | Type | Description |
|-------|------|-------------|
| `authority_origin` | string | `callum` for holds, agent name for failures |
| `lifecycle_status` | enum | `held`, `failed`, `released` |
| `lineage_pointer` | string | execution.marker or result.marker ID |
| `reason` | string | Human-readable explanation of hold or failure |
| `hold_fail_id` | string | Unique identifier |
| `created_at` | ISO-8601 | Timestamp |

#### lineage.marker

Archive and provenance reference linking a task to the UEIA seam chain.

| Field | Type | Description |
|-------|------|-------------|
| `authority_origin` | string | `cto-command-interface` (Northbridge) |
| `lifecycle_status` | enum | `frozen`, `superseded` |
| `lineage_pointer` | string | UEIA seam commit hash |
| `seam_number` | integer | UEIA seam number (1–23+) |
| `seam_name` | string | Human-readable seam name |
| `frozen_at` | ISO-8601 | Timestamp of freeze |

### Marker Lifecycle Rules

1. A marker is created by writing the file atomically to the task directory.
2. A marker's `lifecycle_status` may be updated in place (same file, new status). No other fields may change after creation.
3. A new marker type in the chain (e.g., plan.marker after request.marker) must reference the prior marker via `lineage_pointer`.
4. Only `callum` may create hold-fail.marker with status `held`. Agents may only create hold-fail.marker with status `failed`.
5. Only `cto-command-interface` may create or update lineage.marker.
6. A marker file must never be deleted. Superseded markers are updated to `superseded` status.
7. The `lineage_pointer` chain must be traversable back to either a root request (`null` pointer) or a UEIA seam hash.

---

## 7. Placement Recommendation

Four candidate layers for introducing frozen static composition into the SmallVille / UEIA stack, evaluated against current repository reality.

### Option A: Rust Helper Layer

**Mechanism**: A `smallville-anchors` Rust crate using `concat_arrays!` and related macros to define compile-time frozen tables. Consumed by Java via generated constants (build artifact) or FFI.

**Pros**:
- Strongest compile-time guarantee — if it compiles, sizes are correct
- `#![no_std]` means zero runtime dependency
- `array-concat-test` repo already demonstrates the pattern

**Cons**:
- Rust is 0% of the current codebase — introduces a new language, toolchain, and CI dependency
- FFI bridge to Java is heavyweight for what are essentially string sets
- Overpowered for the current surface sizes (3–15 elements)

**Verdict**: Not recommended as first step. Re-evaluate if the codebase adopts Rust for other purposes (e.g., a performance-critical sidecar) or if the number of frozen surfaces grows significantly.

### Option B: Generated Artifact Layer

**Mechanism**: A build plugin (Maven, Gradle, or standalone script) that reads a single-source YAML/JSON definition of frozen vocabularies and generates Java constants (e.g., `public static final Set<String> ALLOWED_PROPOSAL_TYPES = Set.of(...)`) into a `Generated.java` class consumed by both `SimulationService` and `ChatService`.

**Pros**:
- Uses existing Java toolchain — no new language
- Single source of truth for duplicated constants
- Build-time verification (generated code won't compile if source definition is malformed)
- Straightforward to implement as a Maven plugin or annotation processor

**Cons**:
- Adds a build step and a generated source directory
- Generated code can be overwritten if developers edit the output instead of the source

**Verdict**: Recommended for Rank 2–3 surfaces (duplicated `Set.of()` constants). The generation source could be a YAML file under `schemas/` or a dedicated `frozen-vocabularies.yaml`.

### Option C: Protocol / Schema Layer

**Mechanism**: Extend existing `schemas/*.schema.yaml` files with machine-readable vocabulary registries. The Python contract test suite validates that Java code constants match the schema-defined vocabularies at test time.

**Pros**:
- Uses existing infrastructure (YAML schemas + Python pytest)
- No new language or build plugin
- Validation happens in the existing test pipeline
- Already partially practiced — `affect-state.schema.yaml` defines `moodLabel` enum, Python tests validate schema structure

**Cons**:
- Validation is test-time, not compile-time — a mismatch is caught in CI, not in the editor
- Requires discipline to keep schemas updated when Java constants change

**Verdict**: Recommended for Rank 1 surface (MemoryLedgerEntry.type vocabulary) and Rank 5 surfaces (schema enum vocabularies). A new `schemas/memory-ledger-entry-types.schema.yaml` or an addition to the existing ledger schema could canonicalize the 7 type values, with a Python test asserting alignment.

### Option D: Not in Current Repo Yet

**Mechanism**: Defer all frozen-composition infrastructure. Continue with manual duplication and comment-documented vocabularies.

**Pros**:
- Zero cost, zero risk, zero new infrastructure
- Current surfaces are small enough that manual review has worked so far

**Cons**:
- Drift risk grows with every new feature (Lane D relationship dynamics will add surfaces)
- MemoryLedgerEntry.type has already drifted from 4 values (original MemoryType enum) to 7 values (comment-documented) with no enforcement
- Active duplication (ALLOWED_PROPOSAL_TYPES, OBJECT_LIKE_PARENT_TOKENS) is a ticking clock

**Verdict**: Not recommended. The evidence shows drift has already occurred (MemoryType enum has 4 values; MemoryLedgerEntry.type comment documents 7). Deferring increases the gap.

### Combined Recommendation

| Surface Tier | Recommended Layer | Rationale |
|-------------|-------------------|-----------|
| Rank 1 (MemoryLedgerEntry.type) | **Option C** — Schema + test validation | Lowest friction; adds a schema definition and a Python test; catches drift in CI |
| Rank 2–3 (duplicated Set.of() constants) | **Option B** — Generated artifact | Eliminates duplication at the source; single YAML → generated Java constant |
| Rank 4 (CanonicalFixtures) | **No change** — already well-frozen | `Collections.unmodifiableList()` + Seam 7 governance is sufficient |
| Rank 5–8 (secondary candidates) | **Option C** — Schema extension | Add vocabularies to existing schema files; validate in Python test suite |
| Non-targets | **None** — these must not be frozen | Mutable runtime and world-state memory is categorically excluded |

---

## 8. Ranked Next-Step Order

Prioritized adoption sequence if Callum Gate approves implementation. Each step is independently valuable and can be stopped at any gate.

| Priority | Action | Surface | Layer | Estimated Scope |
|----------|--------|---------|-------|----------------|
| **P0** | Centralize MemoryLedgerEntry.type as a Java enum or `Set.of()` constant | Rank 1 | Java source (Option B/C) | 1 new enum/constant file + update scattered literals |
| **P1** | Deduplicate ALLOWED_PROPOSAL_TYPES to a shared constant | Rank 2 | Java source (Option B) | Extract to shared class, update 2 consumers |
| **P2** | Deduplicate OBJECT_LIKE_PARENT_TOKENS to a shared constant | Rank 3 | Java source (Option B) | Extract to shared class, update 2 consumers |
| **P3** | Add MemoryLedgerEntry.type vocabulary to ledger-export schema | Rank 1 | Schema layer (Option C) | Schema update + Python contract test |
| **P4** | Define marker file format specification in `schemas/` | §6 markers | Schema layer (Option C) | New `marker.schema.yaml` + Python validation |
| **P5** | Evaluate schema-vs-code vocabulary validation for moodLabel, gate_level | Rank 5 | Schema layer (Option C) | Python test additions |
| **P6** | Evaluate machine-readable seam hash registry | Rank 6 | Schema or generated (Option B/C) | New `ueia-seam-registry.yaml` if ladder extends |
| **P7** | Monitor social preference vocabulary growth through Lane D | Rank 7 | Deferred | Re-evaluate after Lane D implementation |

---

## 9. Boundary Compliance Statement

This document complies with the following hard boundaries:

| Boundary | Status |
|----------|--------|
| No code edits | ✓ Compliant — no source files modified |
| No repo mutations | ✓ Compliant — no files created, moved, or deleted in the simulation codebase |
| No commits | ✓ Compliant — no git operations performed |
| No staging | ✓ Compliant — no files staged |
| No speculative retrieval redesign | ✓ Compliant — memory retrieval architecture is not addressed |
| No weight-editing proposals | ✓ Compliant — no LLM weight or fine-tuning proposals |
| No conflation of compile-time anchors with runtime sim memory | ✓ Compliant — §4 and §5 explicitly separate mutable memory from frozen vocabulary |
| No assumption that Rust must be the final enforcement layer | ✓ Compliant — §7 evaluates Rust as one of four options and does not recommend it as first step |

All evidence is grounded in live public repo truth:
- `walz333/smallville-tinyworld` at commit `ba57ec4` (verified 2026-04-01)
- `walz333/array-concat-test` at commit `79ac0b0` (verified 2026-04-01)

Compile-time concatenation is being evaluated as a stability-anchor mechanism for frozen control surfaces, not as a substitute for runtime memory, world-state, or learned model parameters.
