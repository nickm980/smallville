import { Text, Title } from '@tremor/react';
import { getModels, getWorldSnapshot } from '../../lib/smallville';
import SimulationControls from '../simulation-controls';
import StatusCard from '../status-card';
import WorldConsole from './world-console';

export default async function WorldPage() {
  const [snapshot, models] = await Promise.all([getWorldSnapshot(), getModels()]);

  return (
    <main className="mx-auto max-w-7xl p-4 md:p-10">
      <Title>World Console</Title>
      <Text>
        Inspect the live world, guide proposals, switch local models, and import new personas
        from YAML without leaving the dashboard.
      </Text>
      <StatusCard />
      <SimulationControls />
      <WorldConsole initialSnapshot={snapshot} initialModels={models} />
    </main>
  );
}
