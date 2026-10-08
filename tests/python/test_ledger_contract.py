"""Validate ledger export JSON against schemas/ledger-export.schema.yaml."""

import json
import os
import glob

import pytest
import yaml

REPO_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
SCHEMA_DIR = os.path.join(REPO_ROOT, 'schemas')


def load_yaml_schema(name):
    path = os.path.join(SCHEMA_DIR, name)
    with open(path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)


def find_ledger_export_files():
    pattern = os.path.join(REPO_ROOT, 'runs', '**', 'endpoint_ledger_export_*.json')
    return glob.glob(pattern, recursive=True)


def load_json(path):
    with open(path, 'r', encoding='utf-8-sig') as f:
        raw = json.load(f)
    # Handle endpoint capture wrapper
    if 'body' in raw and 'generatedAtUtc' not in raw:
        return raw['body']
    return raw


class TestLedgerExportSchema:
    """Validate ledger export files against the schema."""

    def test_schema_exists(self):
        schema = load_yaml_schema('ledger-export.schema.yaml')
        assert schema is not None
        assert schema.get('title') == 'Ledger Export'

    def test_required_fields_defined(self):
        schema = load_yaml_schema('ledger-export.schema.yaml')
        required = schema.get('required', [])
        assert 'generatedAtUtc' in required
        assert 'scenario' in required
        assert 'world' in required
        assert 'governanceLedger' in required
        assert 'memoryIndex' in required

    @pytest.mark.skipif(
        not find_ledger_export_files(),
        reason='No ledger export captures found in runs/'
    )
    def test_captured_exports_have_required_fields(self):
        try:
            from jsonschema import Draft202012Validator, ValidationError
            from referencing import Registry, Resource
        except ImportError:
            pytest.skip('jsonschema or referencing not installed')

        schema = load_yaml_schema('ledger-export.schema.yaml')
        # Build a registry so $ref: "affect-state.schema.yaml" etc. can resolve
        registry = Registry()
        for schema_file in os.listdir(SCHEMA_DIR):
            if schema_file.endswith('.schema.yaml'):
                s = load_yaml_schema(schema_file)
                resource = Resource.from_contents(s)
                registry = registry.with_resource(schema_file, resource)
        validator = Draft202012Validator(schema, registry=registry)
        for path in find_ledger_export_files():
            data = load_json(path)
            errors = list(validator.iter_errors(data))
            if errors:
                pytest.fail(f'{path}: {errors[0].message}')

    @pytest.mark.skipif(
        not find_ledger_export_files(),
        reason='No ledger export captures found in runs/'
    )
    def test_governance_ledger_is_list(self):
        for path in find_ledger_export_files():
            data = load_json(path)
            if 'governanceLedger' in data:
                assert isinstance(data['governanceLedger'], list), \
                    f'{path}: governanceLedger should be a list'

    @pytest.mark.skipif(
        not find_ledger_export_files(),
        reason='No ledger export captures found in runs/'
    )
    def test_memory_index_keys_are_agent_names(self):
        for path in find_ledger_export_files():
            data = load_json(path)
            if 'memoryIndex' in data and data['memoryIndex']:
                for agent_name, index in data['memoryIndex'].items():
                    assert isinstance(agent_name, str)
                    assert 'agentName' in index or 'totalEvents' in index, \
                        f'{path}: memoryIndex entry for {agent_name} missing expected fields'

    def test_memory_index_schema_includes_ponder_fields(self):
        """Verify the ledger-export schema defines ponderCount and lastPonderTick."""
        schema = load_yaml_schema('ledger-export.schema.yaml')
        mem_props = (schema.get('properties', {})
                     .get('memoryIndex', {})
                     .get('additionalProperties', {})
                     .get('properties', {}))
        assert 'ponderCount' in mem_props, \
            'ledger-export schema missing ponderCount in memoryIndex'
        assert 'lastPonderTick' in mem_props, \
            'ledger-export schema missing lastPonderTick in memoryIndex'

    @pytest.mark.skipif(
        not find_ledger_export_files(),
        reason='No ledger export captures found in runs/'
    )
    def test_memory_index_ponder_fields_when_present(self):
        """If memoryIndex entries have ponder fields, validate their types."""
        for path in find_ledger_export_files():
            data = load_json(path)
            if 'memoryIndex' not in data:
                continue
            for agent_name, index in data['memoryIndex'].items():
                if 'ponderCount' in index:
                    assert isinstance(index['ponderCount'], int), \
                        f'{path}: {agent_name} ponderCount should be int'
                    assert index['ponderCount'] >= 0
                if 'lastPonderTick' in index:
                    assert isinstance(index['lastPonderTick'], int), \
                        f'{path}: {agent_name} lastPonderTick should be int'
