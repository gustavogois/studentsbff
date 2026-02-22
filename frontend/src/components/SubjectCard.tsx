import { Link } from "react-router-dom";
import type { Subject } from "../types";

interface SubjectCardProps {
  subject: Subject;
}

export default function SubjectCard({ subject }: SubjectCardProps) {
  return (
    <Link
      to={`/subjects/${subject.id}`}
      className="block rounded-lg border border-gray-200 bg-white p-6 shadow-sm transition hover:shadow-md"
    >
      <h3 className="text-lg font-semibold text-gray-900">{subject.name}</h3>
      <p className="mt-1 text-sm text-gray-500">
        Created {new Date(subject.createdAt).toLocaleDateString()}
      </p>
    </Link>
  );
}
