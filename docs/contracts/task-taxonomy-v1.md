# Task Taxonomy v1

This note defines bounded, content-only task classes for the `tiny-world` and `two-house-garden` scenario family. These classes are scenario semantics only. They do not add engine fields, scheduling rules, parser mechanics, or runtime behaviors.

## Use

These task classes give reviewers and scenario authors a common vocabulary for calm domestic, garden, and sheltered-space work. They are intended to keep early specialist work legible inside the microcosm before any broader simulation or real-world adapter layer is introduced.

## Task Classes

| Task class | Scenario use | Likely locations | Typical outputs | Review cues |
| --- | --- | --- | --- | --- |
| `water check` | Confirm whether watering is needed soon. | Garden, Green House, Potting Bench, Water Barrel | "Check the watering needs in the Green House." | Mentions moisture, timing, or readiness without inventing new systems. |
| `barrel review` | Inspect water storage condition and readiness. | Green House: Water Barrel, Garden storage corners | "Rain barrel is half full." | Reads as state inspection, not as a location-creation leap. |
| `bed inspection` | Review beds, borders, or path-side planting areas for readiness. | Garden beds, path edges, sheltered borders | "Inspect the herb bed by the gate." | Stays grounded in visible plant areas and maintenance cues. |
| `planting` | Place seedlings, starts, or seeds into planned positions. | Potting Bench, Garden beds, sheltered planters | "Set out seedlings for the south bed." | Connects to preparation or layout rather than fantasy expansion. |
| `watering` | Carry out actual watering once need has been identified. | Green House, Garden, Water Barrel adjacency | "Water the seed trays before midday." | Clear action with plausible tool or source reference. |
| `plant health review` | Check vigor, stress, or visible plant condition. | Green House, Garden beds, trays, planters | "Look for yellowing leaves on the tomatoes." | Observational and calm, not diagnostic overreach. |
| `sale-ready stock check` | Review whether plants or bundles look presentable and complete in a simulated stock sense. | Potting Bench, trays, sheltered display spots | "See which herb pots look ready for sale." | Treated as scenario readiness only, not live commerce or pricing. |
| `propagation / bed tidy` | Thin, sort, clean, or reset propagation areas and beds. | Potting Bench, tray areas, Garden beds | "Tidy cuttings and clear spent stems." | Blends maintenance with preparation, not waste-system complexity. |
| `container / tray check` | Inspect pots, trays, labels, and small growing containers. | Glass Table, Potting Bench, shelf or bench areas | "Check whether the seed trays need turning." | Focuses on arrangement and condition of small growing units. |
| `greenhouse or sheltered-space inspection` | Review enclosed growing spaces for comfort, access, and readiness. | Green House, porch, cold-frame-like sheltered spaces | "Inspect the greenhouse before tea." | Reads as environmental review, not sensor integration. |
| `path/gate check` | Review access routes, gate condition, and whether shared movement areas are ready for the next work block. | Garden path, Garden: Gate, entry corners between houses and beds | "Check whether the gate should stay closed while watering." | Keeps access and boundary work visible without inventing transport or security systems. |
| `tea / meal checkpoint` | Pause work for tea, food, or shared reset. | Blue House: Kitchen, Garden Bench, Green House table | "Pause for tea once the notes are done." | Keeps domestic rhythm visible and grounded. |
| `tidy/status review` | Close out a work block by restoring order and noting current state. | Blue House, Green House, Garden, work surfaces | "Leave the bench tidy and note what still needs attention." | Summarizes readiness and loose ends without new mechanics. |
| `note-making / plan review` | Record observations, check plans, and review next steps. | Blue House: Study, Green House: Glass Table, Bench | "Review the planting notes for tomorrow." | Produces legible planning language without parser-specific assumptions. |

## Boundaries

- These labels are for scenario writing, run review, and operator discussion.
- They should stay calm, local, and reviewable inside the two-house-plus-garden family.
- They do not imply live sensors, SEPA feeds, economics, stock systems, or any external task adapter.
