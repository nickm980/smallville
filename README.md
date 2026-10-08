# Smallville — Governed Generative-Agent Simulation [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/nickm980/towny/blob/main/LICENSE) [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/nickm980/smallville/issues)

> A governance-first Smallville fork for live world-building, proposal-safe simulation control, and intelligence-engine bootstrapping.

**Status:** experimental · active · governance-first fork of [nickm980/smallville](https://github.com/nickm980/smallville)

---

## Why this fork exists

Upstream Smallville demonstrated that LLM-backed agents can store memories and act in a simulated world.
This fork asks a harder question: **how do you govern, audit, and steer those agents once they are running?**

The answer is an emerging control-room / governance layer around the original simulation engine — adding proposal review, live world-building workflows, scenario schemas, per-aspect model routing, and a live dashboard — so that a human operator or research team can supervise agent intelligence in real time rather than simply launching a simulation and hoping for the best.

This fork explores how generative-agent simulation can be made more governable, auditable, and suitable for real-world experimental world-building.

## What this fork adds

| Layer | What it provides |
|---|---|
| **Control-room governance** | Activation protocols, Northbridge review process, operator decision surfaces in `docs/control-room/` |
| **Live dashboard** | World canvas, tick timeline, model selectors, proposal queue, YAML persona import (`dashboard/`) |
| **Scenario / schema system** | Declarative YAML scenarios with JSON Schema validation (`scenarios/`, `schemas/`) |
| **Per-aspect model routing** | Route each intelligence aspect (memory, planning, activity, conversation, reflection) to a distinct local model via `modelRouting` in config |
| **Sidecar direction** | Python sidecar scaffolding for packet-level analysis and validation (`sidecar/`, `requirements-sidecar.txt`) |
| **Local-first Ollama support** | First-class local inference — no API key needed, works out of the box with Ollama |
| **Memory ledger & journaling** | Persistent agent memory with NDJSON event ledgers, dream packs, and affect-state tracking |
| **Evaluation harness** | Eval design docs and test tiers from pure-unit through embedded-server to live-tick smoke (`docs/evals/`, `tests/`) |

## Architecture at a glance

```
┌─────────────────────────────────────────────────────┐
│                   Operator / Browser                │
│         Dashboard (Next.js) · Control Room          │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP
┌──────────────────────▼──────────────────────────────┐
│              Java Host / Simulator (Javalin)         │
│  ┌────────────┐ ┌───────────┐ ┌──────────────────┐  │
│  │ Scenarios  │ │  Agents   │ │ Runtime Settings │  │
│  │ & Schemas  │ │ & Memory  │ │ & Model Routing  │  │
│  └────────────┘ └───────────┘ └──────────────────┘  │
│  ┌────────────┐ ┌───────────┐ ┌──────────────────┐  │
│  │ Proposals  │ │  Prompts  │ │  Affect / Ponder │  │
│  │ & Review   │ │ & Chat    │ │  & Dreaming      │  │
│  └────────────┘ └───────────┘ └──────────────────┘  │
└──────────────────────┬──────────────────────────────┘
                       │ OpenAI-compatible API
┌──────────────────────▼──────────────────────────────┐
│          Ollama (local) or Hosted Provider           │
│   Per-aspect routing: memoryRanking → qwen2.5:0.5b  │
│   longTermPlanning → llama3.2:3b-16k   etc.         │
└─────────────────────────────────────────────────────┘
         ┌─────────────┐
         │   Sidecar    │  (Python, packet validation)
         └─────────────┘
```

## Fastest way to see it working

**Prerequisites:** Java 17+, Ollama running locally with at least one model pulled (e.g. `ollama pull llama3.2:3b-16k`).

```powershell
# 1. Build the server
.\scripts\build-server.ps1

# 2. Start the seeded tiny-world scenario
.\scripts\start-tiny-world.ps1 -Port 8090

# 3. In a second terminal — launch the dashboard
.\scripts\dashboard-dev.ps1 -Port 3002 -SmallvilleUrl http://localhost:8090
```

Then open `http://localhost:3002` — you will see agents, a world canvas, tick timeline, and the proposal queue.

For full local setup details (bootstrap, Maven, npm), see [CUSTOMIZE.md](CUSTOMIZE.md).

## Current stack

| Component | Technology |
|---|---|
| Simulation host | Java 17, Javalin, SnakeYAML |
| Build | Maven 3.9 |
| Dashboard | Next.js, Tailwind |
| Inference | Ollama (local-first) or any OpenAI-compatible endpoint |
| Sidecar | Python 3, aiohttp, jsonschema |
| Scenarios | YAML configs + JSON Schema validation |
| Tests | JUnit 5, 280+ methods across 3 tiers |

## Client libraries

Supported client languages: **Java**, **JavaScript**, or use the HTTP endpoints directly.

<details>
<summary>Java client (via JitPack)</summary>

```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
<dependencies>
	<dependency>
		<groupId>com.github.nickm980</groupId>
		<artifactId>smallville</artifactId>
		<version>2b663b0</version>
	</dependency>
</dependencies>
```

```java
SmallvilleClient client = SmallvilleClient.create("http://localhost:8080", new AgentHandlerCallback() {
    public void handle(SimulationUpdateEvent event) {
        List<SmallvilleAgent> agents = event.getAgents();
        List<SmallvilleLocation> locations = event.getLocations();
    }
});

client.createLocation("Red House");
client.createObject("Red House", "Kitchen", new ObjectState("occupied"));

List<String> memories = new ArrayList<String>();
memories.add("Memory1");
client.createAgent("John", memories, "Red House: Kitchen", "Cooking");

client.updateState();
```
</details>

<details>
<summary>JavaScript client</summary>

```
npm i smallville
```

```javascript
const client = new Smallville({
    host: "http://localhost:8080",
    stateHandler: function(state) {
        const agents = state.agents;
        const objects = state.locations;
        const conversations = state.conversations;
        console.log('[State Change]: The simulation has been updated');
    },
});
```
</details>

## Hosted providers

If you want to use OpenAI or another hosted OpenAI-compatible provider instead of local Ollama:
```
java -jar smallville-server.jar --api-key <OPEN_AI_KEY> --port 8080
```

## Dashboard

Run the Next.js dashboard from `dashboard/` or via the helper script:
```powershell
.\scripts\dashboard-dev.ps1 -Port 3002 -SmallvilleUrl http://localhost:8090
```
The dashboard provides prompt analytics, agent state inspection, location editing, and interview tools.
The `/world` page adds a world canvas, tick timeline, model selectors, a proposal queue, and YAML persona import for live world-building workflows.

## Configuration

- **Scenario config:** `scenarios/<name>/config.yaml` — model, API path, model routing, agent definitions
- **Prompts:** `smallville/src/main/resources/prompts.yaml` — reaction, plan, summary, and interview templates
- **Schema:** `schemas/scenario-config.schema.yaml` — validates scenario configs

See [CUSTOMIZE.md](CUSTOMIZE.md) for full customization entry points.

## Research lineage

This project builds on the ideas in [Generative Agents: Interactive Simulacra of Human Behavior](https://arxiv.org/pdf/2304.03442.pdf) (Park et al., 2023). Original tileset by LimeZu. Upstream repository: [nickm980/smallville](https://github.com/nickm980/smallville).

## Community

For help getting started or project updates, join the community Discord: https://discord.gg/APVSw2DrCX
