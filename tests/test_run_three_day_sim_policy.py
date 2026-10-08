import asyncio
import gzip
import importlib.util
import json
import types
import sys
import unittest
from pathlib import Path
from tempfile import TemporaryDirectory


ROOT = Path(__file__).resolve().parents[1]
MODULE_PATH = ROOT / 'scripts' / 'run_three_day_sim.py'
sys.modules.setdefault('aiohttp', types.SimpleNamespace(ClientSession=object, TCPConnector=object, ClientTimeout=object))
SPEC = importlib.util.spec_from_file_location('run_three_day_sim', MODULE_PATH)
assert SPEC and SPEC.loader
MODULE = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = MODULE
SPEC.loader.exec_module(MODULE)


class RunThreeDaySimPolicyTests(unittest.TestCase):
    def setUp(self) -> None:
        self.policy = MODULE.merge_policy_config({})
        self.trace_root = MODULE.init_policy_trace_root(self.policy)
        self.snapshot = {
            'governance': {'reviewWindowHours': 24},
            'agents': [],
            'proposalHistory': [],
        }

    def test_plan_policy_decisions_defers_until_review_window_then_approves(self) -> None:
        proposal = {
            'id': 'proposal-1',
            'agent': 'Alex',
            'type': 'add_location',
            'parentLocation': 'Garden',
            'name': 'Raised Planter',
            'targetLocation': 'Garden: Raised Planter',
            'status': 'pending',
            'createdAtTick': 1,
            'createdAtSimulationTime': '5:38 pm',
        }

        early = MODULE.plan_policy_decisions(
            [proposal],
            self.snapshot,
            self.policy,
            self.trace_root,
            tick_index=1,
            timestep_minutes=60,
            auto_govern_enabled=True,
        )
        self.assertEqual('deferred_time_window', early['decisions'][0]['action'])

        matured = MODULE.plan_policy_decisions(
            [proposal],
            self.snapshot,
            self.policy,
            self.trace_root,
            tick_index=25,
            timestep_minutes=60,
            auto_govern_enabled=True,
        )
        self.assertEqual('approve', matured['decisions'][0]['action'])

    def test_plan_policy_decisions_rejects_building_scale_with_alternative(self) -> None:
        proposal = {
            'id': 'proposal-2',
            'agent': 'Alex',
            'type': 'add_location',
            'parentLocation': 'Garden',
            'name': 'Tea House',
            'targetLocation': 'Garden: Tea House',
            'status': 'pending',
            'createdAtTick': 1,
            'createdAtSimulationTime': '5:38 pm',
        }

        decision = MODULE.plan_policy_decisions(
            [proposal],
            self.snapshot,
            self.policy,
            self.trace_root,
            tick_index=25,
            timestep_minutes=60,
            auto_govern_enabled=True,
        )['decisions'][0]

        self.assertEqual('reject_scope', decision['action'])
        self.assertIn('Consider', decision['suggestion'])

    def test_plan_policy_decisions_defers_when_agent_addition_cap_is_spent(self) -> None:
        self.trace_root['ticks'].append({
            'tick_index': 25,
            'simulated_time': '5:38 pm',
            'simulated_day_index': 1,
            'review_window_hours': 24,
            'pending_before_count': 1,
            'pending_after_count': 0,
            'blocking_pending_count': 0,
            'blocking_pending_ids': [],
            'agent_outcomes': [],
            'proposal_decisions': [{
                'proposal_id': 'approved-1',
                'agent': 'Alex',
                'type': 'add_location',
                'parent_location': 'Garden',
                'target_location': 'Garden: Raised Planter',
                'name': 'Raised Planter',
                'status_before': 'pending',
                'created_at_tick': 1,
                'created_at_simulation_time': '5:38 pm',
                'action': 'approve',
                'rationale': 'Approved.',
                'suggestion': None,
                'age_hours': 24.0,
                'effort_labor_days': 0.5,
                'effort_class': 'add_location',
                'simulated_day_index': 1,
                'approval_day_index': 1,
                'requires_human_review': False,
                'executed': True,
            }],
            'budget_snapshot': {},
        })
        proposal = {
            'id': 'proposal-3',
            'agent': 'Alex',
            'type': 'add_location',
            'parentLocation': 'Garden',
            'name': 'Compost Corner',
            'targetLocation': 'Garden: Compost Corner',
            'status': 'pending',
            'createdAtTick': 2,
            'createdAtSimulationTime': '6:38 pm',
        }

        decision = MODULE.plan_policy_decisions(
            [proposal],
            self.snapshot,
            self.policy,
            self.trace_root,
            tick_index=26,
            timestep_minutes=60,
            auto_govern_enabled=True,
        )['decisions'][0]

        self.assertEqual('deferred_budget', decision['action'])

    def test_build_agent_tick_outcomes_marks_submitted_duplicate_and_no_proposal(self) -> None:
        snapshot = {
            'agents': [
                {'name': 'Alex', 'canProposeWorldChanges': True, 'action': 'Plan', 'location': 'Garden', 'shortPlans': [], 'workingMemories': []},
                {'name': 'Jamie', 'canProposeWorldChanges': True, 'action': 'Observe', 'location': 'Garden', 'shortPlans': [], 'workingMemories': []},
                {'name': 'Sam', 'canProposeWorldChanges': True, 'action': 'Read', 'location': 'Blue House', 'shortPlans': [], 'workingMemories': []},
            ],
            'proposalHistory': [
                {
                    'id': 'p-alex',
                    'agent': 'Alex',
                    'targetLocation': 'Garden: Raised Planter',
                    'status': 'pending',
                    'createdAtTick': 3,
                },
                {
                    'id': 'p-jamie',
                    'agent': 'Jamie',
                    'targetLocation': 'Garden: Raised Planter',
                    'status': 'rejected',
                    'createdAtTick': 3,
                },
            ],
        }

        outcomes = {
            item['agent']: item['outcome']
            for item in MODULE.build_agent_tick_outcomes(snapshot, tick_index=3)
        }
        self.assertEqual('submitted', outcomes['Alex'])
        self.assertEqual('rejected_duplicate', outcomes['Jamie'])
        self.assertEqual('no_proposal', outcomes['Sam'])

    def test_save_ledger_export_artifacts_writes_bundle_and_snapshot_files(self) -> None:
        class FakeClient:
            async def get_json(self, path: str) -> dict:
                if path == '/world/ledger/export':
                    return {
                        'world': {
                            'agents': [{'name': 'Alex Rowan'}],
                        },
                        'governanceLedger': {'guidanceRules': ['stay grounded']},
                        'memoryIndex': {'agents': [{'agent': 'Alex Rowan'}]},
                    }
                if path == '/agents/Alex%20Rowan/memory-ledger':
                    return {
                        'agent': 'Alex Rowan',
                        'dreamPacks': [{
                            'id': 'dream-pack-1',
                            'summary': ['Remember the greenhouse tea'],
                        }],
                    }
                raise AssertionError(f'unexpected path: {path}')

        with TemporaryDirectory() as tmpdir:
            output_dir = Path(tmpdir)
            asyncio.run(MODULE.save_ledger_export_artifacts(FakeClient(), output_dir, snapshot_label='cold_start'))

            self.assertTrue((output_dir / 'ledger_export.json').exists())
            self.assertTrue((output_dir / 'governance_ledger.json').exists())
            self.assertTrue((output_dir / 'memory_index.json').exists())
            self.assertTrue((output_dir / 'memory_ledgers' / 'Alex_Rowan.json').exists())
            self.assertTrue((output_dir / 'ledger_snapshots' / 'cold_start' / 'ledger_export.json').exists())

            with gzip.open(output_dir / 'dream_packs' / 'Alex_Rowan' / 'dream-pack-1.json.gz', 'rt', encoding='utf-8') as handle:
                payload = json.load(handle)

            self.assertEqual('dream-pack-1', payload['id'])
            self.assertEqual(['Remember the greenhouse tea'], payload['summary'])


if __name__ == '__main__':
    unittest.main()
