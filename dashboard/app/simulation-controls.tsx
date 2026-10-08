'use client';

import { Button, Card, Text } from '@tremor/react';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { advanceSimulation } from '../lib/smallville';

interface SimulationControlsProps {
  onRefresh?: () => Promise<void> | void;
}

export default function SimulationControls(props: SimulationControlsProps) {
  const router = useRouter();
  const [pending, setPending] = useState(false);
  const [message, setMessage] = useState('');

  async function refreshPage() {
    if (props.onRefresh) {
      await props.onRefresh();
      return;
    }

    router.refresh();
  }

  async function stepSimulation() {
    setPending(true);
    setMessage('');

    const ok = await advanceSimulation();
    if (ok) {
      setMessage('Simulation advanced by one step.');
      await refreshPage();
    } else {
      setMessage('Could not advance the simulation.');
    }

    setPending(false);
  }

  return (
    <Card className="mt-6">
      <div className="flex flex-wrap items-center gap-3">
        <Button loading={pending} onClick={stepSimulation}>
          Advance One Tick
        </Button>
        <Button variant="secondary" color="gray" disabled={pending} onClick={refreshPage}>
          Refresh Dashboard
        </Button>
        <Text>{message}</Text>
      </div>
    </Card>
  );
}
