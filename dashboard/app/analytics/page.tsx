'use client';

import {
  Text,
  Title,
} from '@tremor/react';
import { getAllLocations, getInfo } from '../../lib/smallville';
import Chart from './chart';
import React, { useEffect, useState } from 'react';

export default function LocationsPage(props: any) {
  // const locations: SmallvilleLocation[] =[];
  const [data, setData] = useState({chartData: [], analytics: {locationVisits: []}});

  useEffect(() => {
    const fetchData = async () => {
      const info: any = await getInfo()

      const chartData = info?.prompts?.map((prompt: any, index: number) => ({
        "Response Time": Math.abs(prompt.responseTime),
        Month: index,
      }));

      console.log(info)
      setData({
        analytics: info,
        chartData: chartData
      })
    }

    fetchData();
  }, []);
  
  return (
    <main className="p-4 md:p-10 mx-auto max-w-7xl">
      <Title>Prompts & Analytics</Title>
      <Text>General information and analytics useful for debugging</Text>
      <Chart data={data.chartData}></Chart>
    </main>
  );
}
