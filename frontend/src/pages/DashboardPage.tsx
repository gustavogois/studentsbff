import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import SubjectCard from "../components/SubjectCard";
import { getSubjects } from "../services/subjectService";
import type { Subject } from "../types";

export default function DashboardPage() {
  const { user } = useAuth();
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getSubjects()
      .then(setSubjects)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="text-gray-500">Loading...</div>;
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900">
        Hello, {user?.name}!
      </h1>
      <p className="mt-1 text-gray-600">Welcome to your study dashboard.</p>

      <div className="mt-8">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">
            Your Subjects
          </h2>
          <Link
            to="/subjects"
            className="text-sm text-indigo-600 hover:text-indigo-800"
          >
            Manage subjects
          </Link>
        </div>

        {subjects.length === 0 ? (
          <div className="mt-4 rounded-lg border-2 border-dashed border-gray-300 p-8 text-center">
            <p className="text-gray-500">No subjects yet.</p>
            <Link
              to="/subjects"
              className="mt-2 inline-block text-sm text-indigo-600 hover:text-indigo-800"
            >
              Add your first subject
            </Link>
          </div>
        ) : (
          <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {subjects.map((subject) => (
              <SubjectCard key={subject.id} subject={subject} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
