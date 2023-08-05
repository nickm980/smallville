'use client';

import { ChatBubbleLeftEllipsisIcon } from '@heroicons/react/24/outline';
import { usePathname, useRouter } from 'next/navigation';
import { FormEventHandler, useEffect, useState, useTransition } from 'react';

export const dynamic = 'force-dynamic';

interface AgentListBoxProps {
  people: {
    name: string;
  }[];
  disabled?: boolean
  onChange: FormEventHandler<HTMLDivElement> | undefined;
}

import { Select, SelectItem } from '@tremor/react';
import QuickModal from './modal';
import { interview } from '../lib/smallville';

export function AgentListBox(props: AgentListBoxProps) {
  return (
    <div className="max-w-sm mx-auto space-y-6">
      <Select onChange={props.onChange} disabled={props.disabled}>
        {props.people.map((item) => (
          <SelectItem key={item.name} value={item.name}></SelectItem>
        ))}
      </Select>
    </div>
  );
}

export default function InterviewInput({
  agents
}: {
  agents: { name: string }[];
}) {
  const [isPending, setPending] = useState(false)
  const [isOpen, setIsOpen] = useState(false);
  const [answer, setAnswer] = useState('Loading...');

  function openModal() {
    setIsOpen(true);
  }

  let currentAgent = agents[0] != undefined ? agents[0].name : "";

  function handleChange(agent: any) {
    console.log(agent);
    console.log("agent change")
    currentAgent = agent;
  }

  async function handleSearch(question: any) {
    let answer = 'answer to a question';

    if (currentAgent == undefined) {
      answer = 'Please choose an agent before asking an interview question';
      console.log(currentAgent)
      console.log("this")
      openModal();
      setAnswer(answer);
      return;
    }
    setPending(true)

    answer = await interview(currentAgent, question)
    console.log('answer ' + answer);
    openModal();
    setAnswer(answer);
    setPending(false)
  }

  return (
    <>
      <div className="flex space-x-4 wrap">
        <AgentListBox
          people={agents}
          onChange={(e) => {
            handleChange(e);
          }}
          disabled={isPending}
        ></AgentListBox>

        <div className="relative flex-1">
          <label htmlFor="search" className="sr-only">
            Interview
          </label>
          <div className="rounded-md shadow-sm">
            <div
              className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3"
              aria-hidden="true"
            >
              <ChatBubbleLeftEllipsisIcon
                className="mr-3 h-10 w-4 text-gray-400"
                aria-hidden="true"
              />
            </div>
            <input
              type="text"
              name="interview"
              id="interview"
              autoComplete="off"
              disabled={isPending}
              className="h-10 block w-full rounded-md border border-gray-200 pl-9 focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              placeholder="What is your name?"
              spellCheck={false}
              onKeyDown={async (e) => {
                if (e.key === 'Enter') {
                  await handleSearch(e.currentTarget.value);
                }
              }}
            />
          </div>

          {isPending && (
            <div className="absolute right-0 top-0 bottom-0 flex items-center justify-center">
              <svg
                className="animate-spin -ml-1 mr-3 h-5 w-5 text-gray-700"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                />
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                />
              </svg>
            </div>
          )}
        </div>
      </div>

      <QuickModal
        setIsOpen={setIsOpen}
        isOpen={isOpen}
        title={'Interview with ' + currentAgent}
      >
        <p>{answer}</p>
      </QuickModal>
    </>
  );
}
