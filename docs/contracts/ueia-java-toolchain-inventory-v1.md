# UEIA Java Toolchain Inventory v1

## Purpose

This document records the repo-visible Java, build, and toolchain surface at the current frozen repo state. It is an inventory only. It does not create a new governance authority seam, does not widen the UEIA ladder, and does not authorize any implementation or build mutation.

## Repo-Visible Java / Build / Toolchain File Inventory

### Build Entry Points Present

- `smallville/pom.xml`
- `java-client/pom.xml`
- `examples/java-example/pom.xml`

### Java Source Roots Present

- `smallville/src/main/java`
- `smallville/src/test/java`
- `java-client/src/main/java`
- `java-client/src/test/java`
- `examples/java-example/src/main/java`

### Java Source Roots Absent

- `examples/java-example/src/test/java`

### Repo-Visible Build / Toolchain Files Not Present

- `build.gradle`
- `build.gradle.kts`
- `settings.gradle`
- `settings.gradle.kts`
- `gradle.properties`
- `mvnw`
- `mvnw.cmd`
- `gradlew`
- `gradlew.bat`
- `.mvn/`
- `gradle/wrapper/`
- `.github/workflows/`
- `.gitlab-ci.yml`
- `Jenkinsfile`
- `.circleci/`
- `azure-pipelines*.yml`

## Java Version Declarations Found

### `smallville/pom.xml`

- `maven.compiler.source`: `17`
- `maven.compiler.target`: `17`

### `java-client/pom.xml`

- `maven.compiler.source`: `17`
- `maven.compiler.target`: `17`

### `examples/java-example/pom.xml`

- no explicit `maven.compiler.source` declaration found
- no explicit `maven.compiler.target` declaration found

## Wrapper Presence / Absence

- Maven wrapper: absent
- Gradle wrapper: absent

## Plugin Inventory

### `smallville/pom.xml`

- `org.apache.maven.plugins:maven-compiler-plugin:3.9.0`
- `org.apache.maven.plugins:maven-shade-plugin:3.2.4`

### `java-client/pom.xml`

- `org.apache.maven.plugins:maven-source-plugin:3.3.1`

### `examples/java-example/pom.xml`

- no explicit plugins declared

## Dependency-Management Points That Actually Exist

### Version Properties

Declared in `smallville/pom.xml`:

- `junit-jupiter.version`: `5.7.1`
- `slf4j.version`: `1.7.36`
- `jackson.version`: `2.17.2`
- `snakeyaml.version`: `2.2`
- `stanford-corenlp.version`: `4.5.6`

### Direct Dependency Version Pins

Declared directly in repo-visible POM files:

- `smallville/pom.xml` uses direct dependency pins and property-backed pins
- `java-client/pom.xml` uses direct dependency pins
- `examples/java-example/pom.xml` uses a direct dependency pin to `com.github.nickm980:smallville:2b663b0`

### Management Blocks Not Present

- no `<dependencyManagement>` block found
- no `<pluginManagement>` block found

## Repository Declarations That Actually Exist

### `smallville/pom.xml`

- repository id: `reposilite-repository`
- repository url: `https://maven.reposilite.com/snapshots`

### `examples/java-example/pom.xml`

- repository id: `jitpack.io`
- repository url: `https://jitpack.io`

### `java-client/pom.xml`

- no explicit repository declarations found

## Module / Build Relationship Summary

- The repo currently exposes three standalone Maven projects.
- No repo-visible parent POM relationship is declared between the three projects.
- No repo-visible Maven reactor `<modules>` relationship is declared between the three projects.
- `smallville` is the main server/runtime library project with explicit compiler and shading plugin declarations.
- `java-client` is a separate client project with explicit compiler declarations and a sources JAR plugin declaration.
- `examples/java-example` is a separate example project that depends on an external JitPack-resolved `smallville` artifact by commit-hash-style version rather than a repo-local module link.

## Environment Note

- `java -version` reports OpenJDK / Temurin `17.0.18`
- Maven CLI is not currently available on `PATH` in this audit environment, so this inventory records repo declarations and direct environment observations only

## Boundary Carry-Forward

- This inventory does not change canonical-path discipline.
- This inventory does not weaken pre-decision versus review-decision separation where the ladder is referenced.
- Water Mill remains design-asset-derived context only unless accepted repo evidence states otherwise.
- Packet content remains interpretive artifact only and never runtime world state.

## Explicit Non-Authorization

"This document is audit-only and does not create a new governance authority seam."

"This document does not authorize Java, Maven, Gradle, plugin, dependency, wrapper, CI, or build-file mutation."

"Repo files remain the source of truth for version and build declarations."

"Environment observations support the audit but do not override repo declarations."
