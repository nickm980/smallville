# UEIA Java Toolchain Audit Brief v1

## Purpose

This brief defines an audit-only review of the Java and build-tool surface visible in the repo. It exists to support a later read-only inventory and compatibility review without authorizing upgrades, dependency changes, plugin changes, or any tooling mutation.

## Audit-Only Scope

This brief is limited to:

- inventory
- compatibility review
- mutation-risk identification
- planning for a later non-mutating audit pass

This brief does not authorize upgrades or tooling mutation.

## Repo Items To Inspect

The current repo scan identifies these build and toolchain entry points:

- `smallville/pom.xml`
- `java-client/pom.xml`
- `examples/java-example/pom.xml`

Current repo-visible observations:

- `smallville/pom.xml` explicitly sets Maven compiler source/target to `17`
- `java-client/pom.xml` explicitly sets Maven compiler source/target to `17`
- `examples/java-example/pom.xml` is present and should be checked for inherited/default compiler behavior and dependency linkage

## Version Inventory Checklist

Inspect and record:

- installed Java runtime version used in the audit environment
- installed Maven version used in the audit environment
- compiler source/target values in each `pom.xml`
- parent/child or cross-module dependency relationships
- plugin versions in each `pom.xml`
- library version pins in each `pom.xml`
- snapshot, alpha, RC, or commit-hash style dependency references

## Compatibility Review Checklist

Review for:

- Java source/target consistency across repo modules
- Maven plugin compatibility with the declared Java level
- dependency compatibility between `smallville`, `java-client`, and `examples/java-example`
- risk from mixed test frameworks or mixed plugin expectations
- risk from RC/alpha dependencies
- risk from commit-hash dependencies in example modules
- risk from repositories beyond Maven Central

## Mutation Risks

Potential mutation risks to call out before any upgrade:

- breaking Java compatibility across modules
- changing artifact resolution for the example module
- changing shade or packaging behavior in `smallville`
- changing source or test behavior through plugin upgrades
- introducing dependency conflicts by partially upgrading one module but not the others
- widening scope from audit into unapproved build mutation

## Explicit Non-Authorization

This brief does not authorize:

- Java upgrades
- Maven upgrades
- plugin upgrades
- dependency upgrades
- repository changes
- build-file mutation
- runtime-code mutation

It is audit-only and non-mutating by design.
