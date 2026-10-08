# Residue Disposition Review Checkpoint — 2026-04-03

**Timestamp:** 2026-04-03 GMT
**Lane type:** review-only
**HEAD at review:** `104bd46` (feat: cherry-pick Codex KEEP-group)
**Triggered by:** March 22 control-room recommended next lane

## 1. Purpose

This checkpoint records the final disposition of every workspace residue item flagged in the March 22 source-pack refresh. No files were edited, deleted, staged, or committed as part of the review itself — only this disposition record and a `.gitignore` addition for `/output/`.

## 2. Disposition table

| Item | March 22 status | April 3 status | Disposition | Rationale |
|------|----------------|----------------|-------------|-----------|
| `docs/contracts/smallville-memory-triplet-schema-v1.md` | Visible untracked | **Gone** — file no longer exists | **CLOSED** | Never committed; removed between March 22 and April 3 without checkpoint record. No loss — content was a draft contract not referenced by runtime code. |
| `docs/contracts/smallville-reflection-and-retrieval-v1.md` | Visible untracked | **Gone** — file no longer exists | **CLOSED** | Same as above. Draft contract, never committed, now absent. |
| `output/pdf/smallville-app-summary.pdf` | Visible untracked | **Gone** — replaced by `smallville-cto-exec-assistant-brief.pdf` | **CLOSED** | Export artifact superseded. `/output/` now added to `.gitignore`. |
| `twohousev.1` | Visible untracked | **Gone** — intentionally removed per March 22 checkpoint | **CLOSED** | Untracked lineage noise. Removal was documented. |
| `tmp/pdfs` | Visible untracked | **Gone** — `tmp/` directory does not exist | **CLOSED** | Already covered by `/tmp/` in `.gitignore`. |
| `.codex-logs/` | Empty directory, untracked | Exists, empty, gitignored | **PARK** | Harmless workspace-local directory. Already in `.gitignore`. No action needed. |

## 3. Additional findings

### docs/contracts/ (96 files) — ACTIVE AUTHORITY

The 96 files currently in `docs/contracts/` are **tracked, implementation-relevant contract specifications**. They include API endpoint specs (e.g., `local-ledger-export-contract-v1.md`), memory governance specs (e.g., `dreaming-memory-governance-v1.md`), and UEIA conformance documents. At least one is directly referenced from Java source code (`LedgerEntryType.java` references `dreaming-memory-governance-v1.md`).

These are **not residue**. They are active authority and should remain tracked.

### output/pdf/smallville-cto-exec-assistant-brief.pdf — PARK

A newer export artifact exists at this path. It is now covered by the `/output/` gitignore entry and will not appear in future `git status` output.

### March 23 checkpoint discrepancy

The March 23 checkpoint (`reviewed-consequence-proof-freeze-checkpoint-2026-03-23.md`) listed `smallville-memory-triplet-schema-v1.md` and `smallville-reflection-and-retrieval-v1.md` as "Staged (A)". However, these files were never committed and no longer exist on disk. The checkpoint record is a frozen historical artifact and is not corrected here, but this discrepancy is noted for lineage clarity.

## 4. .gitignore changes

Added `/output/` to the "Local workspace residue" section of `.gitignore`, alongside the existing `/tmp/` and `/.codex-logs/` entries.

## 5. Residue lane status

**All March 22 residue items are now resolved.**

The "review-only: residue disposition and ignore-policy classification" lane recommended by the March 22 source-pack is hereby **closed**.

## 6. Authority note

- This checkpoint does not authorize runtime, architecture, retrieval, memory, mutation-authority, or model-path changes.
- Handover checkpoints (March 20–23) remain frozen historical records and are not modified.
- The `2026-03-22` source-pack files remain as lineage. A new `2026-04-03` source-pack refresh will carry the corrected state forward.
