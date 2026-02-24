import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import {
  getSubjects,
  createSubject,
  updateSubject,
  deleteSubject,
} from "../services/subjectService";
import ConfirmDialog from "../components/ConfirmDialog";
import type { Subject } from "../types";

export default function SubjectsPage() {
  const { t } = useTranslation();
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [newName, setNewName] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editName, setEditName] = useState("");
  const [deleteTarget, setDeleteTarget] = useState<string | null>(null);
  const editInputRef = useRef<HTMLInputElement>(null);

  const loadSubjects = () => {
    getSubjects()
      .then(setSubjects)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadSubjects();
  }, []);

  useEffect(() => {
    if (editingId && editInputRef.current) {
      editInputRef.current.focus();
    }
  }, [editingId]);

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

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteSubject(deleteTarget);
      loadSubjects();
    } catch (err) {
      console.error(err);
    } finally {
      setDeleteTarget(null);
    }
  };

  const startEditing = (subject: Subject) => {
    setEditingId(subject.id);
    setEditName(subject.name);
  };

  const cancelEditing = () => {
    setEditingId(null);
    setEditName("");
  };

  const saveEdit = async () => {
    if (!editingId || !editName.trim()) {
      cancelEditing();
      return;
    }
    try {
      await updateSubject(editingId, { name: editName.trim() });
      loadSubjects();
    } catch (err) {
      console.error(err);
    } finally {
      cancelEditing();
    }
  };

  const handleEditKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      e.preventDefault();
      saveEdit();
    } else if (e.key === "Escape") {
      cancelEditing();
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
              {editingId === subject.id ? (
                <input
                  ref={editInputRef}
                  type="text"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  onKeyDown={handleEditKeyDown}
                  onBlur={saveEdit}
                  className="flex-1 rounded-md border border-gray-300 px-2 py-1 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                  aria-label={t("subjects.editLabel")}
                />
              ) : (
                <Link
                  to={`/subjects/${subject.id}`}
                  className="font-medium text-indigo-600 hover:text-indigo-800"
                >
                  {subject.name}
                </Link>
              )}
              <div className="flex items-center gap-2">
                {editingId !== subject.id && (
                  <button
                    onClick={() => startEditing(subject)}
                    className="text-sm text-gray-500 hover:text-gray-700"
                  >
                    {t("common.edit")}
                  </button>
                )}
                <button
                  onClick={() => setDeleteTarget(subject.id)}
                  className="text-sm text-red-600 hover:text-red-800"
                >
                  {t("common.delete")}
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}

      <ConfirmDialog
        open={deleteTarget !== null}
        title={t("subjects.deleteConfirmTitle")}
        message={t("subjects.deleteConfirmMessage")}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
