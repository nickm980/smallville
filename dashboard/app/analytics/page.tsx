import {
  Text,
  Title,
} from '@tremor/react';
import { getInfo } from '../../lib/smallville';
import Chart from './chart';
import StatusCard from '../status-card';
import SimulationControls from '../simulation-controls';

export default async function AnalyticsPage() {
  const info = await getInfo();
  const chartData = info.prompts.map((prompt, index) => ({
    'Response Time': Math.abs(prompt.responseTime),
    Month: index,
  }));

  return (
    <main className="p-4 md:p-10 mx-auto max-w-7xl">
      <Title>Prompts & Analytics</Title>
      <Text>General information and analytics useful for debugging</Text>
      <StatusCard />
      <SimulationControls />
      <Chart data={chartData} />
    </main>
  );
}
