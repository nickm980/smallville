# UEIA SmallVille Project-Model Evidence v1

## 1. Scope and Authority
This document records a diagnostics-only, read-only, non-authorizing evidence lane. It captures only repo-visible project-model signals and observable environment/editor signals relevant to `smallville` and does not authorize fixes, implementation, build mutation, dependency mutation, toolchain mutation, IDE mutation, or scope widening.

## 2. Evidence Sources Used
Only the following repo files and environment signals were observed in this session:
- `docs/contracts/ueia-java-toolchain-audit-v1.md`
- `docs/contracts/ueia-problem-triage-inventory-v1.md`
- `docs/contracts/ueia-diagnostics-capture-v1.md`
- `docs/contracts/ueia-full-diagnostics-export-and-editor-compliance-evidence-v1.md`
- `docs/contracts/ueia-primary-fix-lane-proposal-v1.md`
- `docs/contracts/ueia-canonicalfixtures-post-fix-verification-v1.md`
- `smallville/pom.xml`
- observable `java -version`
- observable `javac -version`
- observable executable paths for `java` and `javac`
- observable `JAVA_HOME`
- presence or absence checks for `.settings`, `.classpath`, `.project`, and `.vscode/settings.json`
- current targeted diagnostics query for `c:\SmallVille\smallville`

## 3. Repo Project-Model Signals
The repo-visible project model for `smallville` declares Java 17 explicitly through `maven.compiler.source=17` and `maven.compiler.target=17` in `smallville/pom.xml`.

The same file explicitly declares `org.apache.maven.plugins:maven-compiler-plugin:3.9.0` and replaces Maven's default `compile` and `testCompile` executions with named executions `java-compile` and `java-test-compile`.

The repo-visible build model also declares `org.apache.maven.plugins:maven-shade-plugin:3.2.4` for packaging, but no repo-visible evidence in this session showed any separate editor project-model metadata overriding the Java 17 posture for `smallville`.

Project-model files observed as absent in the workspace during this session:
- `.settings`
- `.classpath`
- `.project`
- `.vscode/settings.json`

No other repo-visible workspace/editor model file was directly observed in this lane.

## 4. Observable Environment Signals
The observable Java runtime and compiler signals in this session were:
- `java -version`: `openjdk version "17.0.18" 2026-01-20`, `OpenJDK Runtime Environment Temurin-17.0.18+8`, `OpenJDK 64-Bit Server VM Temurin-17.0.18+8`
- `javac -version`: `javac 17.0.18`
- `java` executable path: `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\java.exe`
- `javac` executable path: `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\javac.exe`
- `JAVA_HOME`: `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\`

Observable workspace/editor config files in this session were absent rather than present:
- `.settings`: absent
- `.classpath`: absent
- `.project`: absent
- `.vscode/settings.json`: absent

The current targeted diagnostics query for `c:\SmallVille\smallville` returned no errors.

## 5. Contradiction Map
Repo declarations and shell/runtime/compiler context are aligned with each other:
- `smallville/pom.xml` declares Java 17 source and target
- the observable `java` runtime is Java 17
- the observable `javac` compiler is Java 17
- `JAVA_HOME` points to a Java 17 installation

The contradiction remains between those aligned signals and the frozen diagnostics behavior previously captured in the accepted evidence set:
- frozen diagnostics artifacts recorded Java-1.8-style parser behavior against `smallville`, including rejection of `record` and `var`
- frozen diagnostics artifacts also recorded an exact aggregate Problems total while targeted re-queries for `smallville` and specific files returned no errors
- the current session repeats that inconsistency at the narrow module level because the live targeted diagnostics query for `c:\SmallVille\smallville` again returns no errors, while the frozen accepted diagnostics evidence still records the earlier aggregate-visible conflict

Conservative contradiction summary:
- repo truth observed in this session says Java 17
- shell/runtime/compiler truth observed in this session says Java 17
- diagnostics behavior observed across the frozen evidence set remains inconsistent with both of those truths and is not directly explained by any repo-visible workspace project-model file observed in this lane

## 6. What Remains Unknown
- the editor or language-service's actual effective compliance level for `smallville` remains unknown because no direct editor compliance setting was exposed in this environment
- the editor or language-service's actual project import state for `smallville` remains unknown
- whether the contradictory diagnostics behavior comes from stale aggregate state, project-model desynchronization, language-server transport limits, or another editor-side condition remains unknown
- whether any hidden user-level or extension-level Java settings exist outside the repo-visible workspace remains unknown
- whether a future environment-oriented lane could safely remediate the contradiction without first obtaining direct editor/language-service metadata remains unknown

## 7. Does This Justify A Future Environment-Oriented Lane
No. The current evidence is strong enough to show a contradiction, but not strong enough to justify remediation because the decisive missing evidence is still the direct editor/language-service effective project-model and compliance metadata for `smallville`.

## 8. Safest Next Move
Authorize only a further explicitly bounded evidence-only lane that captures direct editor or language-service project-model and effective compliance metadata for `smallville`, if such metadata can be exposed without mutation.

## 9. What This Document Does Not Authorize
- no fixes
- no code edits
- no build-file edits
- no dependency changes
- no unresolved-cell implementation
- no consuming-seam design
- no runtime / UI / API widening
- no toolchain mutation
- no IDE or workspace configuration mutation