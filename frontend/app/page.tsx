'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/auth';
import Link from 'next/link';

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    if (authService.isAuthenticated()) {
      router.push('/dashboard');
    }
  }, [router]);

  return (
    <div className="max-w-4xl mx-auto">
      <header className="text-center mb-8">
        <h1 className="text-4xl font-bold text-gray-900 mb-2">
          HR Application
        </h1>
        <p className="text-lg text-gray-600">
          Employee Profile Management System
        </p>
      </header>

      <div className="card mb-8">
        <h2 className="text-2xl font-semibold mb-4">Welcome</h2>
        <p className="text-gray-700 mb-4">
          This is the HR application for managing employee profiles and absence requests.
        </p>
        <div className="space-y-2 mb-6">
          <p className="text-sm text-gray-600">
            <strong>Managers:</strong> Full access to all employee data and absence request approvals
          </p>
          <p className="text-sm text-gray-600">
            <strong>Employees:</strong> Can view their own profile, request absences, and see coworker basic info
          </p>
        </div>
        
        <div className="flex space-x-4">
          <Link href="/login">
            <button className="btn-primary">Sign In</button>
          </Link>
        </div>
      </div>

      <div className="card">
        <h3 className="text-xl font-semibold mb-3">Demo Accounts</h3>
        <div className="space-y-2 text-sm">
          <div className="p-3 bg-gray-50 rounded">
            <p><strong>Manager Account:</strong></p>
            <p>Email: manager@company.com</p>
            <p>Password: password</p>
          </div>
          <div className="p-3 bg-gray-50 rounded">
            <p><strong>Employee Account:</strong></p>
            <p>Email: john.doe@company.com</p>
            <p>Password: password</p>
          </div>
        </div>
      </div>
    </div>
  );
}