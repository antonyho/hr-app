import { EmployeeProfile } from '@/types';
import Link from 'next/link';

interface ProfileCardProps {
  profile: EmployeeProfile;
  showDetailedLink?: boolean;
}

export default function ProfileCard({ profile, showDetailedLink = false }: ProfileCardProps) {
  return (
    <div className="card">
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-xl font-semibold">
            {profile.firstName} {profile.lastName}
          </h3>
          <p className="text-gray-600">{profile.employeeId}</p>
        </div>
      </div>
      
      <div className="space-y-2 mb-4">
        {profile.position && (
          <p className="text-sm">
            <span className="font-medium">Position:</span> {profile.position}
          </p>
        )}
        {profile.department && (
          <p className="text-sm">
            <span className="font-medium">Department:</span> {profile.department}
          </p>
        )}
        {profile.phone && (
          <p className="text-sm">
            <span className="font-medium">Phone:</span> {profile.phone}
          </p>
        )}
        {profile.hireDate && (
          <p className="text-sm">
            <span className="font-medium">Hire Date:</span> {new Date(profile.hireDate).toLocaleDateString()}
          </p>
        )}
      </div>
      
      <div className="flex space-x-2">
        <Link href={`/profiles/${profile.id}/basic`}>
          <button className="btn-secondary text-sm">View Basic</button>
        </Link>
        {showDetailedLink && (
          <Link href={`/profiles/${profile.id}/detailed`}>
            <button className="btn-primary text-sm">View Details</button>
          </Link>
        )}
      </div>
    </div>
  );
}