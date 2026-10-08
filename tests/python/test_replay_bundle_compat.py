"""Confirm existing replay bundle artifacts remain unchanged and compatible.

Validates that ledger export additions do not break the existing bundle format
(manifest.yaml, proposal_review.md, operator_notes.md).
"""

import os
import glob

import pytest
import yaml

REPO_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))


def find_run_bundles():
    """Find all run directories that contain a manifest.yaml."""
    pattern = os.path.join(REPO_ROOT, 'runs', '*', 'manifest.yaml')
    return [os.path.dirname(p) for p in glob.glob(pattern)]


def load_yaml(path):
    with open(path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)


class TestReplayBundleCompat:
    """Verify existing run bundle artifacts are intact."""

    @pytest.mark.skipif(
        not find_run_bundles(),
        reason='No run bundles with manifest.yaml found in runs/'
    )
    def test_manifests_have_required_fields(self):
        for bundle_dir in find_run_bundles():
            manifest_path = os.path.join(bundle_dir, 'manifest.yaml')
            manifest = load_yaml(manifest_path)
            assert manifest is not None, f'{manifest_path} is empty'
            assert 'run_id' in manifest, f'{manifest_path} missing run_id'
            assert 'scenario_name' in manifest, f'{manifest_path} missing scenario_name'

    @pytest.mark.skipif(
        not find_run_bundles(),
        reason='No run bundles with manifest.yaml found in runs/'
    )
    def test_proposal_review_exists_when_ticks_ran(self):
        for bundle_dir in find_run_bundles():
            manifest = load_yaml(os.path.join(bundle_dir, 'manifest.yaml'))
            tick_count = manifest.get('intended_tick_count', 0)
            if tick_count and tick_count > 0:
                review_path = os.path.join(bundle_dir, 'proposal_review.md')
                assert os.path.isfile(review_path), \
                    f'{bundle_dir}: proposal_review.md missing for {tick_count}-tick run'

    @pytest.mark.skipif(
        not find_run_bundles(),
        reason='No run bundles with manifest.yaml found in runs/'
    )
    def test_operator_notes_exists(self):
        for bundle_dir in find_run_bundles():
            notes_path = os.path.join(bundle_dir, 'operator_notes.md')
            assert os.path.isfile(notes_path), \
                f'{bundle_dir}: operator_notes.md missing'

    @pytest.mark.skipif(
        not find_run_bundles(),
        reason='No run bundles with manifest.yaml found in runs/'
    )
    def test_cold_world_capture_exists(self):
        for bundle_dir in find_run_bundles():
            cold_path = os.path.join(bundle_dir, 'endpoint_world_cold.json')
            assert os.path.isfile(cold_path), \
                f'{bundle_dir}: endpoint_world_cold.json missing'

    @pytest.mark.skipif(
        not find_run_bundles(),
        reason='No run bundles with manifest.yaml found in runs/'
    )
    def test_manifest_schema_unchanged(self):
        """Verify manifest fields haven't been removed or renamed."""
        expected_fields = {'run_id', 'scenario_name'}
        for bundle_dir in find_run_bundles():
            manifest = load_yaml(os.path.join(bundle_dir, 'manifest.yaml'))
            actual_fields = set(manifest.keys())
            missing = expected_fields - actual_fields
            assert not missing, \
                f'{bundle_dir}: manifest missing expected fields: {missing}'
