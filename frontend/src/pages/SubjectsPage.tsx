import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import {
  getSubjects,
  createSubject,
  deleteSubject,
} from "../services/subjectService";
import type { Subject } from "../types";

export default function SubjectsPage() {
  const { t } = useTranslation();
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [newName, setNewName] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const loadSubjects = () => {
    getSubjects()
      .then(setSubjects)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadSubjects();
  }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newName.trim()) return;
    setSubmitting(true);
    try {
      await createSubject({ name: newName.trim() });
      setNewName("");
      loadSubjects();
    } catch (err) {
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm("Delete this subject?")) return;
    try {
      await deleteSubject(id);
      loadSubjects();
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) {
    return <div className="text-gray-500">{t("common.loading")}</div>;
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900">
        {t("subjects.title")}
      </h1>

      <form onSubmit={handleCreate} className="mt-6 flex gap-3">
        <input
          type="text"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          placeholder={t("subjects.newPlaceholder")}
          className="flex-1 rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
        />
        <button
          type="submit"
          disabled={submitting || !newName.trim()}
          className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
        >
          {t("subjects.addButton")}
        </button>
      </form>

      {subjects.length === 0 ? (
        <div className="mt-6 rounded-lg border-2 border-dashed border-gray-300 p-8 text-center">
          <p className="text-gray-500">{t("subjects.emptyTitle")}</p>
        </div>
      ) : (
        <ul className="mt-6 divide-y divide-gray-200 rounded-lg border border-gray-200 bg-white">
          {subjects.map((subject) => (
            <li
              key={subject.id}
              className="flex items-center justify-between px-4 py-3"
            >
              <Link
                to={`/subjects/${subject.id}`}
                className="font-medium text-indigo-600 hover:text-indigo-800"
              >
                {subject.name}
              </Link>
              <button
                onClick={() => handleDelete(subject.id)}
                className="text-sm text-red-600 hover:text-red-800"
              >
                {t("common.delete")}
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
