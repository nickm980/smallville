'use client';

import { Card, AreaChart, Title, Text } from '@tremor/react';

export default function Chart({data}: {data: any[]}) {
  return (
    <Card className="mt-8">
      <Title>Performance</Title>
      <Text>Last 50 LLM response times</Text>
      <AreaChart
        className="mt-4 h-80"
        data={data}
        categories={['Response Time']}
        index="Month"
        colors={['indigo']}
        valueFormatter={(number: number) =>
          `${Intl.NumberFormat('us').format(number).toString()} ms`
        }
        yAxisWidth={60}
      />
    </Card>
  );
}
