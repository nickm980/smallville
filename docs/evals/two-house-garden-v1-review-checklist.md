# Two-House-Garden v1 Review Checklist

## Use

Use this checklist during the first host-native review passes for `two-house-garden-v1`. The goal is to judge whether the scenario stays coherent, bounded, and easy to audit without asking the engine to do anything new.

## Review Items

### Plan integrity
- Do plan summaries stay legible and free of stray time fragments or broken task phrases?
- Do plan-like actions match the calm horticultural and domestic tone of the scenario?

### Location validity
- Are agents acting in valid locations already present in the scenario seed or in clearly reviewed additions?
- Do movements between Blue House, Green House, and Garden make sense?

### Task coherence
- Can the main work be recognized as water checks, tray reviews, plant health work, planting, tidy passes, or note review?
- Do tea or meal pauses appear as grounding checkpoints rather than random detours?

### Water and garden relevance
- Do watering rounds, barrel review, beds, trays, containers, or sheltered-space checks show up in a plausible way?
- Is horticultural work specific enough to be meaningful without implying live external systems?

### Bounded proposals
- If proposals appear, do they stay inside the calm domestic and horticultural world frame?
- Do proposals avoid obvious structural mismatches or world-scale drift?

### Action-log readability
- Can a reviewer follow what happened from the action log without needing engine knowledge?
- Are summaries clear enough to classify against the approved task taxonomy?

### Restart reproducibility
- On cold restart, does the scenario return to the seeded world and agent setup expected for review?
- Are any differences limited to runtime timestamps rather than scenario structure?

## Review Outcome

- `Go` if the scenario stays coherent, bounded, and readable across cold start, short ticks, and restart review.
- `No-Go` if location validity, plan integrity, or task boundedness break down in a way that undermines auditability.
