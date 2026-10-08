"""Validate simulation configuration YAML against schemas/simulation-file.schema.yaml."""

import os

import pytest
import yaml

REPO_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
SCHEMA_DIR = os.path.join(REPO_ROOT, 'schemas')


def load_yaml_schema(name):
    path = os.path.join(SCHEMA_DIR, name)
    with open(path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)


class TestSimulationFileSchema:
    """Validate the simulation-file schema covers ponder and evening-wind config."""

    def test_schema_exists(self):
        schema = load_yaml_schema('simulation-file.schema.yaml')
        assert schema is not None
        assert schema.get('title') == 'Smallville Simulation File'

    def test_daily_rhythm_includes_evening_wind(self):
        schema = load_yaml_schema('simulation-file.schema.yaml')
        rhythm_props = schema['$defs']['dailyRhythmSeed']['properties']
        assert 'eveningWind' in rhythm_props, \
            'dailyRhythmSeed should include eveningWind field'

    def test_memory_seed_defined(self):
        schema = load_yaml_schema('simulation-file.schema.yaml')
        assert 'memorySeed' in schema.get('$defs', {}), \
            'simulation-file schema should define memorySeed'

    def test_memory_seed_has_ponder_fields(self):
        schema = load_yaml_schema('simulation-file.schema.yaml')
        mem_props = schema['$defs']['memorySeed']['properties']
        assert 'ponderEnabled' in mem_props
        assert 'ponderBlendAlpha' in mem_props
        assert 'ponderCooldownTicks' in mem_props

    def test_memory_seed_has_evening_fields(self):
        schema = load_yaml_schema('simulation-file.schema.yaml')
        mem_props = schema['$defs']['memorySeed']['properties']
        assert 'eveningSocialBoost' in mem_props
        assert 'eveningActivationDamp' in mem_props

    def test_memory_seed_has_dream_fields(self):
        schema = load_yaml_schema('simulation-file.schema.yaml')
        mem_props = schema['$defs']['memorySeed']['properties']
        assert 'dreamIntervalTicks' in mem_props
        assert 'dreamWindowStart' in mem_props
        assert 'dreamWindowEnd' in mem_props
        assert 'hotMemoryLimit' in mem_props

    def test_top_level_memory_property(self):
        schema = load_yaml_schema('simulation-file.schema.yaml')
        props = schema.get('properties', {})
        assert 'memory' in props, \
            'simulation-file schema should have top-level memory property'

    @pytest.fixture
    def two_house_config(self):
        config_path = os.path.join(
            REPO_ROOT, 'scenarios', 'two-house-garden-v1', 'simulation.yaml'
        )
        if not os.path.isfile(config_path):
            pytest.skip('two-house-garden-v1 scenario not found')
        with open(config_path, 'r', encoding='utf-8') as f:
            return yaml.safe_load(f)

    def test_two_house_config_has_ponder_config(self, two_house_config):
        mem = two_house_config.get('memory', {})
        assert mem.get('ponderEnabled') is True
        assert isinstance(mem.get('ponderBlendAlpha'), (int, float))
        assert isinstance(mem.get('ponderCooldownTicks'), int)

    def test_two_house_config_has_evening_wind(self, two_house_config):
        rhythm = two_house_config.get('dailyRhythm', {})
        assert rhythm.get('eveningWind') is not None, \
            'two-house-garden-v1 dailyRhythm should have eveningWind'

    def test_two_house_config_has_evening_nudge_params(self, two_house_config):
        mem = two_house_config.get('memory', {})
        assert isinstance(mem.get('eveningSocialBoost'), (int, float))
        assert isinstance(mem.get('eveningActivationDamp'), (int, float))
