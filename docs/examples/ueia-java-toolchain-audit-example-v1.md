# UEIA Java Toolchain Audit Example v1

## Purpose

This example shows how to read the Java toolchain inventory and compatibility review together without treating either document as mutation authority. It is illustrative only.

## Worked Example

Read the two audit documents in this order:

1. start with `docs/contracts/ueia-java-toolchain-inventory-v1.md`
2. then read `docs/contracts/ueia-java-toolchain-compatibility-review-v1.md`

Example reading pattern:

- The inventory tells you that the repo exposes three standalone Maven projects and no repo-visible Gradle files, wrappers, or CI pipeline files.
- The inventory also tells you that `smallville` and `java-client` explicitly declare Java 17, while `examples/java-example` does not.
- The compatibility review then tells you how to interpret that difference: it is a repo-grounded unknown for the example module, not automatic evidence that the example is broken.
- The compatibility review also identifies why the example module should be treated carefully in any future approved mutation phase: it depends on an external JitPack artifact by commit-hash-style version rather than a repo-local module relationship.

The intended outcome of this reading is:

- clearer audit visibility
- safer future planning
- no automatic upgrade decision

## Fresh Chat / Container Use

To resume cleanly in a fresh chat or container, provide:

- the repo at its current branch and HEAD
- `docs/contracts/ueia-java-toolchain-inventory-v1.md`
- `docs/contracts/ueia-java-toolchain-compatibility-review-v1.md`
- `docs/contracts/ueia-java-toolchain-audit-brief-v1.md`
- `docs/contracts/ueia-implementation-readiness-note-v1.md`
- `docs/contracts/ueia-ladder-phase-map-v1.md`
- `docs/examples/ueia-handover-bootstrap-example-v1.md`

Example starter summary:

```text
Use the repo as the source of truth.

Audit inputs:
- docs/contracts/ueia-java-toolchain-inventory-v1.md
- docs/contracts/ueia-java-toolchain-compatibility-review-v1.md
- docs/contracts/ueia-java-toolchain-audit-brief-v1.md

Current repo-grounded signals:
- Maven POMs exist for smallville, java-client, and examples/java-example
- smallville and java-client explicitly declare Java 17
- examples/java-example does not explicitly declare compiler source/target
- no repo-visible wrappers or CI pipeline files are present

Hard boundaries:
- audit only
- no build or runtime mutation authority
- repo files remain source of truth
```

## Source-Of-Truth Reminder

Repo files remain the source of truth for Java and build declarations. Environment observations may help explain the audit context, but they do not override repo declarations.

## Boundary Reminder

- This example does not create a new governance authority seam.
- This example does not authorize Java, Maven, Gradle, plugin, dependency, wrapper, CI, or build-file mutation.
- This example does not authorize runtime behavior, workflow behavior, routing behavior, UI behavior, control behavior, scoring, ranking, or prioritization.
- Water Mill remains design-asset-derived context only unless accepted repo evidence states otherwise.
- Packet content remains interpretive artifact only and never runtime world state.
- Pre-decision artifacts remain distinct from review-decision artifacts.
