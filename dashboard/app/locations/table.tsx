'use client';

import {
  Table,
  TableHead,
  TableRow,
  TableHeaderCell,
  TableBody,
  TableCell,
  Text,
  Button
} from '@tremor/react';
import QuickModal from '../modal';
import { useState } from 'react';
import { QuestionMarkCircleIcon } from '@heroicons/react/24/outline';
import { updateLocation } from '../../lib/smallville';
import { useRouter } from 'next/navigation';
export interface SmallvilleLocation {
  name: string;
  state: string | undefined | null;
}

export default function LocationsTable({
  locations,
  onUpdated
}: {
  locations: SmallvilleLocation[];
  onUpdated?: () => Promise<void> | void;
}) {
  const router = useRouter();
  const [isOpen, setIsOpen] = useState(false);
  const [isPending, setPending] = useState(false);
  const [currentLocationName, setCurrentLocationName] = useState('');
  const [nextState, setNextState] = useState('');

  async function changeLocationState(name: string, state: string) {
    if (!name || !state.trim()) {
      return;
    }

    setPending(true);
    const ok = await updateLocation(name, state);
    setPending(false);

    if (ok) {
      setIsOpen(false);
      setNextState('');
      if (onUpdated) {
        await onUpdated();
      } else {
        router.refresh();
      }
    }
  }

  function openEditor(location: SmallvilleLocation) {
    setCurrentLocationName(location.name);
    setNextState(location.state ?? '');
    setIsOpen(true);
  }

  return (
    <>
      <Table>
        <TableHead>
          <TableRow>
            <TableHeaderCell>Name</TableHeaderCell>
            <TableHeaderCell>State</TableHeaderCell>
            <TableHeaderCell className="text-right"></TableHeaderCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {locations.length === 0 && (
            <TableRow>
              <TableCell>No locations loaded yet.</TableCell>
              <TableCell>
                <Text>The dashboard will populate once the Smallville server is running.</Text>
              </TableCell>
              <TableCell className="text-right">
                <Text>-</Text>
              </TableCell>
            </TableRow>
          )}
          {locations.map((location) => (
            <TableRow key={location.name}>
              <TableCell>{location.name}</TableCell>
              <TableCell>
                <Text>{location.state || 'not known'}</Text>
              </TableCell>
              <TableCell className="text-right">
                <Text>
                  <Button
                    className="mx-0"
                    size="xs"
                    variant="secondary"
                    color="gray"
                    onClick={() => {
                      openEditor(location);
                    }}
                  >
                    Edit State
                  </Button>
                </Text>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <QuickModal
        setIsOpen={setIsOpen}
        isOpen={isOpen}
        title={'Edit Location'}
      >
        <div className="relative flex-1">
          <label htmlFor="search" className="sr-only">
            Location State
          </label>
          <div className="rounded-md shadow-sm">
            <div
              className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3"
              aria-hidden="true"
            >
              <QuestionMarkCircleIcon
              className="mr-3 h-10 w-4 text-gray-400"
              aria-hidden="true"
            />
          </div>
          <input
              type="text"
              name="location_state"
              id="location_state"
              autoComplete="off"
              disabled={isPending}
              className="h-10 block w-full rounded-md border border-gray-200 pl-9 focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              placeholder="Enter a new state (off, on, in use, etc.)"
              spellCheck={false}
              value={nextState}
              onChange={(e) => {
                setNextState(e.currentTarget.value);
              }}
              onKeyDown={async (e) => {
                if (e.key === 'Enter') {
                  await changeLocationState(currentLocationName, nextState);
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
        <Button
          onClick={() => {
            setIsOpen(false);
          }}
        >
          Cancel
        </Button>
        <Button
          onClick={async () => {
            await changeLocationState(currentLocationName, nextState);
          }}
          loading={isPending}
        >
          Change State
        </Button>
      </QuickModal>
    </>
  );
}
