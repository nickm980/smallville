"""Validate packet artifacts against repo-local JSON schemas."""

from __future__ import annotations

from pathlib import Path
from typing import Any

import yaml
from jsonschema import Draft202012Validator
from referencing import Registry, Resource


class SchemaValidator:
    """Load repo-local schemas with cross-file $ref support."""

    def __init__(self, repo_root: str | Path | None = None) -> None:
        if repo_root is None:
            repo_root = Path(__file__).resolve().parents[2]
        self.repo_root = Path(repo_root)
        self.schema_dir = self.repo_root / "schemas"
        self.registry = self._build_registry()

    def _load_schema(self, schema_name: str) -> dict[str, Any]:
        schema_path = self.schema_dir / schema_name
        with schema_path.open("r", encoding="utf-8") as handle:
            schema = yaml.safe_load(handle)
        if not isinstance(schema, dict):
            raise ValueError(f"{schema_name} did not load as an object schema")
        return schema

    def _build_registry(self) -> Registry:
        registry = Registry()
        for schema_path in sorted(self.schema_dir.glob("*.schema.yaml")):
            with schema_path.open("r", encoding="utf-8") as handle:
                schema = yaml.safe_load(handle)
            registry = registry.with_resource(
                schema_path.name,
                Resource.from_contents(schema),
            )
        return registry

    def validate(self, instance: dict[str, Any], schema_name: str) -> None:
        schema = self._load_schema(schema_name)
        validator = Draft202012Validator(schema, registry=self.registry)
        errors = sorted(validator.iter_errors(instance), key=lambda err: list(err.path))
        if not errors:
            return

        first_error = errors[0]
        path = ".".join(str(part) for part in first_error.path) or "<root>"
        raise ValueError(f"{schema_name} validation failed at {path}: {first_error.message}")
