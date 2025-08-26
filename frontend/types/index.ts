export type UserRole = 'MANAGER' | 'EMPLOYEE';

export type AbsenceStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface User {
  id: string;
  email: string;
  role: UserRole;
  createdAt: string;
  updatedAt: string;
}

export interface EmployeeProfile {
  id: string;
  userId: string;
  employeeId: string;
  firstName: string;
  lastName: string;
  department?: string;
  position?: string;
  hireDate?: string;
  phone?: string;
  address?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  managerId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AbsenceRequest {
  id: string;
  employeeId: string;
  startDate: string;
  endDate: string;
  reason?: string;
  status: AbsenceStatus;
  approvedBy?: string;
  requestedAt: string;
  approvedAt?: string;
  comments?: string;
}

export interface Feedback {
  id: string;
  profileId: string;
  feedbackBy: string;
  feedbackText: string;
  polishedFeedback?: string;
  createdAt: string;
}