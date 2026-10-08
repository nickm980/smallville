'use client';

import { Badge, Card, Text } from '@tremor/react';
import { useEffect, useState } from 'react';
import { SMALLVILLE_URL, pingSmallville } from '../lib/smallville';

export default function StatusCard() {
  const [connected, setConnected] = useState<boolean | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function checkConnection() {
      const ok = await pingSmallville();
      if (!cancelled) {
        setConnected(ok);
      }
    }

    checkConnection();
    const interval = window.setInterval(checkConnection, 15000);

    return () => {
      cancelled = true;
      window.clearInterval(interval);
    };
  }, []);

  return (
    <Card className="mt-6">
      <div className="flex items-center justify-between gap-4">
        <div>
          <Text>Backend</Text>
          <p className="text-sm text-slate-700">{SMALLVILLE_URL}</p>
          {connected === false && (
            <p className="mt-2 text-sm text-rose-700">
              Start the sim with <code>.\scripts\start-tiny-world.ps1 -Port 8090</code>
            </p>
          )}
        </div>
        <Badge color={connected ? 'emerald' : connected === false ? 'rose' : 'gray'}>
          {connected ? 'Connected' : connected === false ? 'Offline' : 'Checking'}
        </Badge>
      </div>
    </Card>
  );
}
