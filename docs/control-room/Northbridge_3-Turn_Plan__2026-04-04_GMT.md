# Northbridge 3-Turn Execution Plan

**Timestamp:** 2026-04-04 GMT
**Baseline HEAD:** `b1a4f66` — pushed to `walz/main`, in-sync (0/0)
**Prior Lanes Completed:** 7 (compile-readiness, test-commit, test-classification, tier-3 tests, first-tick smoke, routed-model smoke, diff audit + regression proof)
**Classification at Plan Time:** A — full test surface green (287 methods, 0 failures), routing prototype live, README rewritten, public page updated

---

## Situation Assessment

### What is now true
- **Public page** is live with fork thesis, architecture diagram, what-this-fork-adds table, quick-start, and stack summary. The public framing gap identified in the CTO report is closed.
- **Per-aspect model routing** is committed and tested (5 dedicated unit tests + 3-tick smoke run). Each of the 6 intelligence aspects routes to a distinct local model.
- **Full test surface** is proven: 32 test classes, 287 methods, 0 failures after `mvn clean test`.
- **Dependency hygiene** is committed (.gitignore residue exclusion, sidecar deps pinned).
- The prototype is local-only (Ollama 0.20.0, 4 models). No cloud routing path exists yet.

### What remains under-built
1. **Live multi-tick governance run** — the 3-tick smoke proved routing works, but no extended supervised session has tested proposal review, affect drift, dream cycles, or memory ledger growth under the routed model.
2. **Sidecar activation** — `sidecar/` exists with `assembler.py` and `validators/` but has never been executed against a live tick stream. `requirements-sidecar.txt` is pinned but the Python env has not been wired to a running Java host.
3. **Dashboard integration with routing** — the dashboard shows model selectors but has no visibility into which aspect is using which model. No routing telemetry reaches the UI.
4. **Scenario coverage** — only `tiny-world` and `two-house-garden-v1` have `modelRouting` configs. No scenario without routing has been tested for graceful fallback.
5. **Public discoverability** — README is rewritten but the repo still has 0 stars, 0 forks. The "get to know you" prompt from the CTO report has not been placed in the repo.

---

## Turn 8: Governed Multi-Tick Session

**Lane type:** live-governed-session-and-telemetry-proof
**Estimated scope:** 1 session turn
**Hard boundary:** two-house-garden-v1 scenario, up to 10 ticks, no code changes beyond telemetry improvements

### Objectives
1. Run a supervised 10-tick session on `two-house-garden-v1` with the routed-model config
2. Capture per-tick timing, per-aspect model selection evidence, proposal outcomes, and affect-state progression
3. Validate that dream cycles fire (hot memory threshold triggers), memory ledger grows, and affect drift is coherent
4. Produce a structured session log (tick number, agent, aspect calls, model used, duration, outcome)

### Execution Steps
0. **Pre-flight:** verify Ollama running, 4 models loaded, port 8090 free, git clean
1. **Promote resolveModel logging to INFO** (temporary) — single-line change in `RuntimeSettingsService.java` so session logs prove which model handles each aspect call. Revert after capture.
2. **Start two-house-garden-v1:** `.\scripts\start-tiny-world.ps1 -Port 8090` with scenario override to `two-house-garden-v1`
3. **Drive 10 ticks:** scripted `POST /state` loop with per-tick timing capture
4. **Capture:** extract from server logs: aspect-routed model lines, proposal outcomes, affect-state changes, dream pack events
5. **Classify:** A (all ticks coherent, routing proven) / B (ticks succeed but gaps in telemetry) / C (tick failures or model errors)
6. **Revert INFO logging** back to DEBUG
7. **Commit session log** to `runs/` with structured naming

### Deliverables
- `runs/<timestamp>-two-house-garden-v1-governed-10tick/` — raw logs + structured session summary
- Northbridge session report with per-tick evidence table
- Classification for the 10-tick run

### Key Risks
- Extended run may surface Ollama memory pressure with 4 models (total ~4.8GB VRAM)
- Proposal review cycle has not been tested beyond single-tick observation
- Affect drift could compound into unrealistic emotional states over 10 ticks

---

## Turn 9: Sidecar Activation and Packet Validation

**Lane type:** sidecar-activation-and-live-packet-wiring
**Estimated scope:** 1 session turn
**Hard boundary:** Python sidecar only, no Java host changes beyond packet emission if needed

### Objectives
1. Activate the Python sidecar (`sidecar/assembler.py` + `sidecar/validators/`) against a live or replayed tick stream
2. Validate that sidecar can parse and validate scenario configs against `schemas/scenario-config.schema.yaml`
3. Establish the packet flow: Java host → sidecar → validation result
4. Write sidecar unit tests for the validator surface

### Execution Steps
0. **Environment:** activate `.venv`, verify `pip install -r requirements-sidecar.txt` clean
1. **Audit sidecar code:** read `assembler.py`, `validators/`, understand current packet format expectations
2. **Schema validation test:** feed `scenarios/tiny-world/config.yaml` and `scenarios/two-house-garden-v1/config.yaml` through the sidecar validator, assert pass
3. **Negative test:** feed a deliberately invalid config, assert rejection with meaningful error
4. **Live wiring (if feasible):** point sidecar at a running Java host session from Turn 8's logs or a fresh 1-tick run
5. **Tests:** write `tests/sidecar/` Python test files covering validator + assembler
6. **Classify:** A (sidecar validates live packets) / B (validates offline, not yet wired live) / C (code defects in sidecar)

### Deliverables
- Sidecar proven against both scenario configs
- Sidecar test suite in `tests/sidecar/`
- Classification for sidecar activation state
- Commit: sidecar tests + any fixes

### Key Risks
- Sidecar code may have been written speculatively and not yet functional
- Packet format between Java host and sidecar may not be defined yet
- Python env may have version conflicts with pinned deps

### Dependencies
- Benefits from Turn 8 session logs (can use as replay corpus), but not blocked by it

---

## Turn 10: Dashboard Routing Telemetry and Public Signal

**Lane type:** dashboard-routing-visibility-and-public-signal
**Estimated scope:** 1 session turn
**Hard boundary:** dashboard UI + one new API endpoint, no simulation logic changes

### Objectives
1. Add a `/api/routing` or equivalent endpoint to the Java host that returns the current `modelRouting` map and per-agent effective model resolution
2. Surface routing telemetry in the dashboard — show which model is assigned to each aspect, with live status
3. Place the CTO "get to know you" onboarding prompt in the repo (as `docs/onboarding-prompt.md` or similar)
4. Add a brief CONTRIBUTING.md or expand the README with contributor guidance

### Execution Steps
0. **Audit dashboard API:** read `dashboard/lib/` and `dashboard/app/` to understand existing API surface and data flow
1. **Java endpoint:** add `GET /api/routing` to the Javalin router that returns `runtimeSettings.getModelRouting()` + current effective model per agent
2. **Dashboard component:** add a routing status panel to the `/world` page or a new `/routing` page showing the aspect→model map
3. **Onboarding prompt:** create `docs/onboarding-prompt.md` with the CTO report's "get to know you" prompt
4. **Public signal:** review if repo description/topics can be updated via the GitHub API or manual action
5. **Test:** verify dashboard builds (`npm run build` in `dashboard/`), verify new endpoint returns correct JSON
6. **Classify:** A (routing visible in dashboard, onboarding prompt landed) / B (endpoint works, UI partial) / C (blocked)

### Deliverables
- `GET /api/routing` endpoint returning live routing state
- Dashboard routing telemetry component
- `docs/onboarding-prompt.md` — CTO onboarding prompt
- Commit: dashboard telemetry + onboarding + any public signal improvements

### Key Risks
- Dashboard may have build issues not surfaced since last full build
- Adding a new Javalin endpoint requires understanding the existing router setup
- GitHub repo description/topics require push-access actions outside git

### Dependencies
- Logically follows Turns 8-9 (routing proven, sidecar validated), but not strictly blocked

---

## Summary: 3-Turn Trajectory

| Turn | Lane | Primary Question | Success Gate |
|---|---|---|---|
| **8** | Governed 10-tick session | Does the routed-model prototype hold up under extended supervised operation? | 10 ticks complete, per-aspect model evidence captured, affect coherent |
| **9** | Sidecar activation | Can the Python sidecar validate live or replayed simulation packets? | Sidecar passes both scenario configs, test suite committed |
| **10** | Dashboard routing + public signal | Can an outside visitor see what the system does and how models are routed? | Routing telemetry in dashboard, onboarding prompt landed, contributor path clear |

### Strategic Arc
These 3 turns move the project from **"proven prototype on disk"** to **"observable, validated, externally legible system."** Turn 8 proves the simulation holds under load. Turn 9 activates the governance validation layer. Turn 10 makes the system visible to outsiders and closes the discoverability gap identified in the CTO report.

After Turn 10, the project would be at a natural checkpoint for:
- First external review / collaborator onboarding
- Academic write-up of the governance approach
- Cloud model routing when Ollama or another provider offers it
- Multi-scenario campaign testing

---

**Plan authored:** 2026-04-04 GMT
**Next action:** Turn 8 — governed multi-tick session on two-house-garden-v1
