'use client';

import {
  Card,
  Text,
  Title,
  Grid,
} from '@tremor/react';
import LocationsTable, { SmallvilleLocation } from './table';
import { getAllLocations, getInfo } from '../../lib/smallville';
import LocationVisitsChart from './location_visits';
import React, { useEffect, useState } from 'react';

export default function LocationsPage(props: any) {
  // const locations: SmallvilleLocation[] =[];
  const [data, setData] = useState({chartData: [], locations: [], analytics: {locationVisits: []}});

  useEffect(() => {
    const fetchData = async () => {
      const locations: any  = await getAllLocations()
      const info: any = await getInfo()

      const chartData = info.prompts.map((prompt: any, index: number) => ({
        "Response Time": Math.abs(prompt.responseTime),
        Month: index,
      }));

      console.log(info)
      setData({
        analytics: info,
        locations: locations,
        chartData: chartData
      })
    }

    fetchData();
  }, []);
  
  return (
    <main className="p-4 md:p-10 mx-auto max-w-7xl">
      <Title>Locations & Objects</Title>
      <Text>View and edit the location states of the simulation world</Text>
      <Grid numItemsSm={1} numItemsLg={2} className="gap-6 mt-6">
        <Card>
          <LocationsTable locations={data.locations}></LocationsTable>
        </Card>
        <Card key={'Visit Frequency'}>
          <LocationVisitsChart data={data.analytics.locationVisits}></LocationVisitsChart>
        </Card>
      </Grid>
    </main>
  );
}
