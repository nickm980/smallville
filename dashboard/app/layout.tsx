import './globals.css';

import Nav from './nav';
import { Suspense } from 'react';

export const metadata = {
  title: 'Dashboard - Smallville',
  description:
    'Smallville user admin dashboard. View and edit agents, locations, and stats'
};

export default async function RootLayout({
  children
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="h-full bg-gray-50">
      <body className="h-full">
        <Suspense>
          <Nav />
        </Suspense>
        {children}
      </body>
    </html>
  );
}
