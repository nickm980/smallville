import {
  Card,
  Text,
  Title,
} from '@tremor/react';
import LocationsTable from './table';
import { getAllLocations, getInfo } from '../../lib/smallville';
import LocationVisitsChart from './location_visits';
import StatusCard from '../status-card';
import SimulationControls from '../simulation-controls';

export default async function LocationsPage() {
  const locations = await getAllLocations();
  const info = await getInfo();
  const chartData = info.prompts.map((prompt, index) => ({
    'Response Time': Math.abs(prompt.responseTime),
    Month: index,
  }));

  return (
    <main className="p-4 md:p-10 mx-auto max-w-7xl">
      <Title>Locations & Objects</Title>
      <Text>View and edit the location states of the simulation world</Text>
      <StatusCard />
      <SimulationControls />
      <div className="mt-6 grid gap-6 lg:grid-cols-2">
        <Card>
          <LocationsTable locations={locations} />
        </Card>
        <Card key={'Visit Frequency'}>
          <LocationVisitsChart data={info.locationVisits} />
        </Card>
      </div>
    </main>
  );
}
