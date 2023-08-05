'use client';

import { Card, AreaChart, Title, Text, Flex, BarList } from '@tremor/react';

export default function LocationVisitsChart({ data }: { data: any[] }) {
  return (
    <>
      <Title>Location Visits</Title>
      <Flex className="mt-6">
        <Text>Location</Text>
        <Text className="text-right">Visits</Text>
      </Flex>
      <BarList
        data={data}
        valueFormatter={(number: number) =>
          Intl.NumberFormat('us').format(number).toString()
        }
        className="mt-2"
      />
    </>
  );
}
