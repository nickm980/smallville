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
import UserTableButton from './modal';
import { MagnifyingGlassIcon } from '@heroicons/react/24/outline';

export interface User {
  name: string;
  location: string;
  action: string;
  emoji: string;
}

export default function UsersTable({ users }: { users: User[] }) {
  return (
    <Table>
      <TableHead>
        <TableRow>
          <TableHeaderCell>Name</TableHeaderCell>
          <TableHeaderCell>Location</TableHeaderCell>
          <TableHeaderCell>Activity</TableHeaderCell>
          <TableHeaderCell>Emoji</TableHeaderCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {users.length === 0 && (
          <TableRow>
            <TableCell>No agents loaded yet.</TableCell>
            <TableCell>
              <Text>Start the backend or advance the seeded tiny world.</Text>
            </TableCell>
            <TableCell>
              <Text>-</Text>
            </TableCell>
            <TableCell>
              <Text>-</Text>
            </TableCell>
          </TableRow>
        )}
        {users.map((user) => (
          <TableRow key={user.name}>
            <TableCell>{user.name}</TableCell>
            <TableCell>
              <Text>{user.location}</Text>
            </TableCell>
            <TableCell>
              <Text>{user.action}</Text>
            </TableCell>
            <TableCell>
              <Text>{user.emoji}</Text>
            </TableCell>

          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
