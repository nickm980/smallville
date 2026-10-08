# Models Ask-Shadow Runtime Surface Checkpoint

Date: 2026-03-21

## 1. Checkpoint Purpose

This checkpoint freezes one narrow read-only runtime observability seam only:
- `GET /models` now exposes ask-shadow runtime state

This checkpoint does not reopen:
- `/agents/{name}/ask` behavior
- bridge logic
- config authoring
- YAML loading
- runtime mutation
- memory, proposals, `updateState`, retrieval, or architecture work

## 2. Repo Reality At Freeze

Branch at freeze:
- `main`

HEAD at freeze:
- `2a7db2e50beed0bf3dfff97a652c3605055e6dc6`

Working tree at freeze:
- dirty tracked tree already existed before this docs-freeze lane
- this lane records the live seam only and does not normalize unrelated tracked or untracked changes

## 3. Exact Runtime Surface Frozen Here

The frozen surface is a read-only runtime observability surface on `GET /models`.

Live repo shape:

```json
{
  "askShadowBridge": {
    "enabled": false,
    "endpoint": "http://127.0.0.1:8010/neural/turn",
    "connectTimeoutMs": 200,
    "callTimeoutMs": 1200
  }
}
```

Exposed fields:
- `enabled`
- `endpoint`
- `connectTimeoutMs`
- `callTimeoutMs`

These values are effective runtime values produced through `RuntimeSettingsService`.
They are not frozen here as a raw config-file mirror.

## 4. Current Safe Fallbacks Proven In Repo Tests

Current fallback/default values proven in repo tests:
- `enabled = false`
- `endpoint = http://127.0.0.1:8010/neural/turn`
- `connectTimeoutMs = 200`
- `callTimeoutMs = 1200`

Current normalization behavior also proven in repo tests:
- blank or missing endpoint falls back to `http://127.0.0.1:8010/neural/turn`
- non-positive timeout values fall back to the safe defaults above
- oversized timeout values are normalized by `RuntimeSettingsService` before exposure

## 5. Boundary Preservation Frozen Here

`/agents/{name}/ask` behavior did not change in this seam.

Specifically:
- the native Java-host answer remains the caller-visible answer
- the ask-shadow bridge remains shadow-only and advisory-only
- no bridge answer or advisory content is surfaced to API callers in this seam
- no mutation-capable or write surface is introduced on `GET /models`

This is not:
- a config-authoring lane
- a bridge-logic lane
- an ask-path behavior lane

## 6. Exact Files Inspected

Files inspected for this freeze:
- `smallville/src/main/java/io/github/nickm980/smallville/api/v1/dto/ModelsResponse.java`
- `smallville/src/main/java/io/github/nickm980/smallville/api/v1/SimulationService.java`
- `smallville/src/test/java/io/github/nickm980/smallville/EndpointsTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/RuntimeSettingsServiceTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/api/v1/AskShadowBridgeClientTest.java`
- `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`
- `docs/handover/proposal-runtime-feature-checkpoint-2026-03-20.md`
- `docs/handover/runtime-polish-cluster-checkpoint-2026-03-20.md`

Repo-grounded note:
- requested path `smallville/src/test/java/io/github/nickm980/smallville/api/v1/SimulationServiceTest.java` was not present on current HEAD
- live inspected file was `smallville/src/test/java/io/github/nickm980/smallville/SimulationServiceTest.java`

## 7. Test Evidence

No tests were rerun in this docs-freeze lane.

Most recent live focused verification already available for this seam used:

```powershell
mvn -q "-Dtest=RuntimeSettingsServiceTest,AskShadowBridgeClientTest,SimulationServiceTest,EndpointsTest" test
```

Recorded live results from that focused verification:
- `RuntimeSettingsServiceTest`: 2 tests, 0 failures, 0 errors, 0 skipped
- `AskShadowBridgeClientTest`: 5 tests, 0 failures, 0 errors, 0 skipped
- `SimulationServiceTest`: 34 tests, 0 failures, 0 errors, 0 skipped
- `EndpointsTest`: 16 tests, 0 failures, 0 errors, 1 skipped

Repo tests inspected for this freeze show:
- `EndpointsTest` locks the `GET /models` ask-shadow surface shape and current fallback values
- `RuntimeSettingsServiceTest` locks effective-value fallback and normalization behavior
- `SimulationServiceTest` locks unchanged native-answer and disabled-shadow behavior on `/agents/{name}/ask`
- `AskShadowBridgeClientTest` locks the bounded shadow-only bridge contract

## 8. What This Checkpoint Does Not Claim

This checkpoint does not claim:
- bridge availability or health monitoring
- write access to runtime settings
- remote escalation enablement
- surfaced bridge content for operators or API callers
- active config authoring in the scenario YAML family

## 9. Recommended Next Lane

Recommended next lane: review-only confirmation of whether the active scenario config should explicitly author the ask-shadow keys, without reopening runtime behavior or bridge logic.
