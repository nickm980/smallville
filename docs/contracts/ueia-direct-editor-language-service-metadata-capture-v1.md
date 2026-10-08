# UEIA Direct Editor / Language-Service Metadata Capture v1

## 1. Scope and Authority
This document records one narrow evidence-only lane under frozen-authority discipline. It captures only directly observable editor, diagnostics-surface, or language-service metadata exposed by the current environment and compares that metadata against already frozen SmallVille project-model evidence. It does not authorize fixes, implementation, build mutation, dependency mutation, toolchain mutation, IDE mutation, unresolved-cell implementation, consuming-seam design, or scope widening.

## 2. Evidence Sources Used
- `docs/contracts/ueia-smallville-project-model-evidence-v1.md`
- `docs/contracts/ueia-full-diagnostics-export-and-editor-compliance-evidence-v1.md`
- `docs/contracts/ueia-diagnostics-capture-v1.md`
- live workspace-scope diagnostics query from the current environment on 2026-03-17
- live targeted diagnostics query for `c:\SmallVille\smallville`
- live targeted diagnostics queries for:
  - `c:\SmallVille\smallville\src\main\java\io\github\nickm980\smallville\ueia\fixture\CanonicalFixtures.java`
  - `c:\SmallVille\smallville\src\test\java\io\github\nickm980\smallville\ueia\fixture\CanonicalFixtureSectionSpanIndexTest.java`

## 3. Directly Observable Editor / Language-Service Metadata
Only the following direct metadata was exposed by the current environment during this lane:

- current workspace-scope diagnostics query exposed `190` total visible results and returned only the first `50`
- the returned diagnostics were surfaced as `compileError` entries rather than with a more specific provider or language-server identity
- the exposed aggregate subset still included Java-language-service-style symptom text such as:
  - `var cannot be resolved to a type`
  - missing-type and missing-nested-type cascades in UEIA fixture tests
  - ordinary unused-import and unused-field warnings in non-UEIA files
- the same environment returned `No errors found` for a targeted diagnostics query against `c:\SmallVille\smallville`
- the same environment returned `No errors found` for targeted file-level diagnostics queries against:
  - `CanonicalFixtures.java`
  - `CanonicalFixtureSectionSpanIndexTest.java`

Direct metadata not exposed in this lane:

- no editor product identity tied to the diagnostics entries
- no language-server name, version, or process identity
- no effective Java compliance setting
- no effective imported-project model
- no provider-specific metadata beyond the generic `compileError` label and diagnostic text

## 4. Comparison Against Existing Project-Model Evidence
The frozen project-model evidence remains unchanged:

- `smallville/pom.xml` declares Java 17 source and target
- prior frozen environment evidence recorded Java 17 runtime and compiler context
- prior frozen evidence recorded absence of common repo-visible workspace model files such as `.settings`, `.classpath`, `.project`, and `.vscode/settings.json`

The newly observed direct metadata is consistent with the already known contradiction and adds one bounded update:

- the contradiction remains because aggregate diagnostics still expose Java-language-level failure patterns and dependent type-resolution fallout while targeted module and file queries return no errors
- the aggregate total is not stable across evidence captures: prior frozen evidence recorded `216` visible problems, while the current live workspace-scope query exposed `190`
- that change in aggregate total strengthens the existing conclusion that the diagnostics surface is unstable or differently scoped across queries, but it still does not expose the underlying effective editor or language-service project model

## 5. What Remains Unavailable
- direct editor or extension identity for the diagnostics producer
- direct language-service identity and version
- direct effective Java compliance level for `smallville`
- direct imported-project or classpath model for `smallville`
- direct explanation for why workspace-scope aggregate diagnostics and targeted queries disagree
- direct evidence showing whether the aggregate total changed because of refresh behavior, stale state eviction, different query scope, or another editor-side condition

## 6. Does This Now Justify A Future Environment-Oriented Remediation Lane
No. This lane adds a small amount of fresh metadata, most notably that the current aggregate count is `190` rather than the previously captured `216`, but it still does not expose the decisive environment details needed to justify remediation. The safest interpretation remains that the diagnostics surface is inconsistent while the effective editor or language-service project model remains unexposed.

## 7. Safest Next Move
Hold the line. Preserve the frozen blocked categories and treat this document as additional contradiction evidence only. If future authority is granted, any follow-on lane should remain narrowly focused on obtaining direct environment-level metadata that is still unavailable here before any remediation is considered.

## 8. What This Document Does Not Authorize
- no fixes
- no implementation lane
- no unresolved-cell implementation
- no consuming-seam design
- no vocabulary expansion
- no runtime, workflow, routing, approval, UI, or API widening
- no toolchain mutation
- no IDE or workspace-configuration mutation
- no `java-client` or example-module implementation work