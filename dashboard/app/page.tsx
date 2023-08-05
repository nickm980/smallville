import { Card, Title, Text } from '@tremor/react';
import UsersTable from './table';
import InterviewInput from './interview_button';
import { getAgents } from '../lib/smallville';

export const dynamic = 'force-dynamic';

export default async function IndexPage({
  searchParams
}: {
  searchParams: { q: string };
}) {

  const search = searchParams.q ?? '';
  const users = await getAgents();
    
  return (
    <main className="p-4 md:p-10 mx-auto max-w-7xl">
      <Title>Interview Agents</Title>
      <Text>Take the role of an interviewer and ask the agent questions</Text>
      <div  className="mt-6"></div>
      <InterviewInput agents={users}></InterviewInput>
      <Title className="mt-6">Generative Agents</Title>
      <Text>A list of all the generative agents</Text>
      <Card className="mt-6">
        <UsersTable users={users} />
      </Card>
    </main>
  );
}
