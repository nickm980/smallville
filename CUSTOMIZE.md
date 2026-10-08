# Local Setup

This checkout now has a repo-local setup flow so you can work on Smallville without manually wiring Java and Maven into your shell.
It is also preconfigured for a local Ollama setup instead of the legacy OpenAI defaults.

## First Run

From the repo root:

```powershell
.\scripts\bootstrap.ps1
```

That script will:

- find a local JDK 17+ install
- download Apache Maven 3.9.12 into `.tools/` if it is missing
- install the npm dependencies for `dashboard`, `javascript-client`, and `examples/javascript-phaser`
- create `dashboard/.env.local` from the example file if needed

## Common Commands

Build the Java server:

```powershell
.\scripts\build-server.ps1
```

Run the Java tests:

```powershell
.\scripts\test-server.ps1
```

Start the Java server:

```powershell
.\scripts\start-server.ps1
```

Start the seeded tiny world:

```powershell
.\scripts\start-tiny-world.ps1 -Port 8090
```

Start the Java server with a hosted provider instead:

```powershell
$env:OPENAI_API_KEY = 'your-key-here'
.\scripts\start-server.ps1
```

Run the dashboard:

```powershell
.\scripts\dashboard-dev.ps1 -Port 3002
```

Run the dashboard against the tiny world server:

```powershell
.\scripts\dashboard-dev.ps1 -Port 3002 -SmallvilleUrl http://localhost:8090
```

The dashboard helper now defaults to the tiny-world backend at `http://localhost:8090`.
If you need another port, set `NEXT_PUBLIC_SMALLVILLE_URL` in `dashboard/.env.local`.

```powershell
NEXT_PUBLIC_SMALLVILLE_URL=http://localhost:8090
```

## Best Customization Entry Points

- `smallville/src/main/resources/config.yaml`
  Controls model/API settings and simulation-level config.
- `smallville/src/main/resources/prompts.yaml`
  Main prompt templates for reactions, plans, summaries, and interview answers.
- `smallville/src/main/resources/simulation.yaml`
  Seed world/agent data for bootstrapping a custom scenario.
- `smallville/src/main/java/io/github/nickm980/smallville`
  Java server behavior, API routes, world logic, memory handling, and update pipeline.
- `dashboard/app`
  Next.js dashboard pages and UI.
- `dashboard/lib/smallville.ts`
  Dashboard API calls to the local Smallville server.
- `javascript-client`
  JavaScript client package for game integrations.
- `examples/javascript-phaser`
  Example game client using Phaser.

## Notes

- The server now defaults to Ollama at `http://localhost:11434/v1/chat/completions` with model `llama3.2:3b-16k`.
- No API key is required for the default local Ollama setup.
- You can switch the live default model and per-agent runtime overrides from the dashboard world console at `/world`.
- Persona import in the dashboard expects YAML with quoted location paths when they contain colons, for example `location: "Green House: Potting Bench"`.
- The seeded tiny world now includes goals, rituals, social preferences, proposal permissions, and a default daily rhythm so the autonomy/model tools have live data to work with.
- If you want a different boot-time local model, edit `smallville/src/main/resources/config.yaml`.
- When you run from this repo checkout, Smallville will prefer editable files in `smallville/src/main/resources` before it falls back to the ones bundled inside the jar.
- `.\scripts\start-tiny-world.ps1` runs from `scenarios/tiny-world`, which enables the seeded `simulation.yaml` for a tiny starter scenario without changing your main config.
- `.\scripts\build-server.ps1` skips tests by default so the server can be packaged locally even though this checkout currently has failing upstream tests in `PlansParsingTest` and `MemoryStreamTest`.
- Port `8080` was already in use on this machine during setup, so the dashboard base URL is now configurable through `dashboard/.env.local`.
- The Java server preloads NLP/model assets during startup, so the first run can take a while.
- Advancing the sim can take around 1-2 minutes with a local Ollama model because each tick asks the LLM to update the agents.
