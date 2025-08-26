'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/auth';
import Link from 'next/link';

export default function DashboardPage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState<string>('');
  const router = useRouter();

  useEffect(() => {
    if (!authService.isAuthenticated()) {
      router.push('/login');
      return;
    }
    
    setIsAuthenticated(true);
    setUserRole(authService.getUserRole() || '');
  }, [router]);

  const handleLogout = () => {
    authService.logout();
  };

  if (!isAuthenticated) {
    return <div>Loading...</div>;
  }

  return (
    <div className="max-w-6xl mx-auto">
      <header className="mb-8 flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">HR Dashboard</h1>
          <p className="text-gray-600">Role: {userRole}</p>
        </div>
        <button
          onClick={handleLogout}
          className="btn-secondary"
        >
          Logout
        </button>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* My Profile */}
        <Link href="/profile/me" className="card hover:shadow-lg transition-shadow">
          <h3 className="text-xl font-semibold mb-2">My Profile</h3>
          <p className="text-gray-600">View and edit your profile information</p>
        </Link>

        {/* Employee Profiles */}
        <Link href="/profiles" className="card hover:shadow-lg transition-shadow">
          <h3 className="text-xl font-semibold mb-2">Employee Profiles</h3>
          <p className="text-gray-600">
            {userRole === 'MANAGER' ? 'Manage all employee profiles' : 'View coworker profiles'}
          </p>
        </Link>

        {/* Absence Requests */}
        <Link href="/absence-requests" className="card hover:shadow-lg transition-shadow">
          <h3 className="text-xl font-semibold mb-2">Absence Requests</h3>
          <p className="text-gray-600">
            {userRole === 'MANAGER' ? 'Review and approve requests' : 'Manage your absence requests'}
          </p>
        </Link>

        {/* Manager-only sections */}
        {userRole === 'MANAGER' && (
          <>
            <Link href="/absence-requests/pending" className="card hover:shadow-lg transition-shadow">
              <h3 className="text-xl font-semibold mb-2">Pending Approvals</h3>
              <p className="text-gray-600">Review pending absence requests</p>
            </Link>
          </>
        )}
      </div>

      <div className="mt-12 card">
        <h2 className="text-2xl font-semibold mb-4">Quick Actions</h2>
        <div className="space-y-3">
          <Link href="/absence-requests/new" className="block">
            <button className="btn-primary">Request Time Off</button>
          </Link>
          
          {userRole === 'MANAGER' && (
            <Link href="/profiles" className="block">
              <button className="btn-secondary">View All Employees</button>
            </Link>
          )}
        </div>
      </div>
    </div>
  );
}