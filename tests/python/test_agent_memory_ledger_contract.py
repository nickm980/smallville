"""Validate per-agent memory ledger JSON against schemas/agent-memory-ledger.schema.yaml."""

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


def find_agent_memory_ledger_files():
    """Find any per-agent memory-ledger.json files in bundle output."""
    pattern = os.path.join(REPO_ROOT, 'runs', '**', 'agents', '*', 'memory-ledger.json')
    return glob.glob(pattern, recursive=True)


def load_json(path):
    with open(path, 'r', encoding='utf-8') as f:
        return json.load(f)


class TestAgentMemoryLedgerSchema:
    """Validate agent memory ledger files against the schema."""

    def test_schema_exists(self):
        schema = load_yaml_schema('agent-memory-ledger.schema.yaml')
        assert schema is not None
        assert schema.get('title') == 'Agent Memory Ledger'

    def test_required_fields_defined(self):
        schema = load_yaml_schema('agent-memory-ledger.schema.yaml')
        required = schema.get('required', [])
        assert 'agent' in required
        assert 'personaAnchors' in required
        assert 'affect' in required
        assert 'working' in required
        assert 'recent' in required
        assert 'archived' in required
        assert 'dreamPacks' in required

    def test_affect_state_schema_ref(self):
        schema = load_yaml_schema('agent-memory-ledger.schema.yaml')
        affect_prop = schema.get('properties', {}).get('affect', {})
        assert '$ref' in affect_prop, 'affect should reference affect-state.schema.yaml'

    @pytest.mark.skipif(
        not find_agent_memory_ledger_files(),
        reason='No agent memory-ledger.json files found in runs/'
    )
    def test_captured_ledgers_have_required_fields(self):
        try:
            from jsonschema import validate, ValidationError
        except ImportError:
            pytest.skip('jsonschema not installed')

        schema = load_yaml_schema('agent-memory-ledger.schema.yaml')
        for path in find_agent_memory_ledger_files():
            data = load_json(path)
            try:
                validate(instance=data, schema=schema)
            except ValidationError as e:
                pytest.fail(f'{path}: {e.message}')


class TestAffectStateSchema:
    """Validate the affect-state schema independently."""

    def test_schema_exists(self):
        schema = load_yaml_schema('affect-state.schema.yaml')
        assert schema is not None
        assert schema.get('title') == 'Affect State'

    def test_mood_label_enum(self):
        schema = load_yaml_schema('affect-state.schema.yaml')
        mood_prop = schema.get('properties', {}).get('moodLabel', {})
        assert 'enum' in mood_prop
        expected_moods = {'content', 'calm', 'neutral', 'uneasy', 'distressed'}
        assert set(mood_prop['enum']) == expected_moods

    def test_valence_bounds(self):
        schema = load_yaml_schema('affect-state.schema.yaml')
        valence = schema.get('properties', {}).get('valence', {})
        assert valence.get('minimum') == -1.0
        assert valence.get('maximum') == 1.0

    def test_affect_state_schema_documents_ponder_nudge(self):
        """Verify the affect-state schema description references ponder nudges."""
        schema = load_yaml_schema('affect-state.schema.yaml')
        desc = schema.get('description', '')
        assert 'ponder' in desc.lower(), \
            'affect-state schema description should reference ponder nudges'

    def test_activation_and_social_drive_bounds(self):
        """Verify activation and socialDrive have correct bounds for ponder modulation."""
        schema = load_yaml_schema('affect-state.schema.yaml')
        props = schema.get('properties', {})
        activation = props.get('activation', {})
        assert activation.get('minimum') == 0.0
        assert activation.get('maximum') == 1.0
        social = props.get('socialDrive', {})
        assert social.get('minimum') == 0.0
        assert social.get('maximum') == 1.0


class TestDreamPackSchema:
    """Validate the dream-pack schema independently."""

    def test_schema_exists(self):
        schema = load_yaml_schema('dream-pack.schema.yaml')
        assert schema is not None
        assert schema.get('title') == 'Dream Pack'

    def test_required_fields(self):
        schema = load_yaml_schema('dream-pack.schema.yaml')
        required = schema.get('required', [])
        assert 'packId' in required
        assert 'agentName' in required
        assert 'summaryText' in required
        assert 'sourceHashes' in required
