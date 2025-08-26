export default function Home() {
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

      <div className="card">
        <h2 className="text-2xl font-semibold mb-4">Welcome</h2>
        <p className="text-gray-700 mb-4">
          This is the HR application for managing employee profiles and absence requests.
        </p>
        <div className="space-y-2">
          <p className="text-sm text-gray-600">
            <strong>Managers:</strong> Full access to all employee data
          </p>
          <p className="text-sm text-gray-600">
            <strong>Employees:</strong> Can view their own profile and request absences
          </p>
          <p className="text-sm text-gray-600">
            <strong>Coworkers:</strong> Can view non-sensitive data and leave feedback
          </p>
        </div>
      </div>
    </div>
  )
}