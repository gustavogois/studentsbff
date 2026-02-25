import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import {
  getSchoolEvents,
  createSchoolEvent,
  updateSchoolEvent,
  deleteSchoolEvent,
} from "../services/schoolEventService";
import { getSubjects } from "../services/subjectService";
import ConfirmDialog from "../components/ConfirmDialog";
import EmptyState from "../components/EmptyState";
import type { SchoolEvent, SchoolEventRequest, EventType, Subject } from "../types";

const EVENT_TYPES: EventType[] = ["EXAM", "ASSIGNMENT", "DEADLINE"];

const TYPE_COLORS: Record<EventType, string> = {
  EXAM: "bg-red-100 text-red-800",
  ASSIGNMENT: "bg-blue-100 text-blue-800",
  DEADLINE: "bg-orange-100 text-orange-800",
};

function EventTypeBadge({ type, label }: { type: EventType; label: string }) {
  return (
    <span
      className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${TYPE_COLORS[type]}`}
    >
      {label}
    </span>
  );
}

export default function SchoolEventsPage() {
  const { t } = useTranslation();
  const [events, setEvents] = useState<SchoolEvent[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingEvent, setEditingEvent] = useState<SchoolEvent | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<string | null>(null);

  const [title, setTitle] = useState("");
  const [eventType, setEventType] = useState<EventType>("EXAM");
  const [eventDate, setEventDate] = useState("");
  const [subjectId, setSubjectId] = useState("");
  const [description, setDescription] = useState("");

  const loadData = () => {
    Promise.all([getSchoolEvents(), getSubjects()])
      .then(([eventsData, subjectsData]) => {
        setEvents(eventsData);
        setSubjects(subjectsData);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  const resetForm = () => {
    setTitle("");
    setEventType("EXAM");
    setEventDate("");
    setSubjectId("");
    setDescription("");
    setEditingEvent(null);
    setShowForm(false);
  };

  const openCreateForm = () => {
    resetForm();
    setShowForm(true);
  };

  const openEditForm = (event: SchoolEvent) => {
    setEditingEvent(event);
    setTitle(event.title);
    setEventType(event.eventType);
    setEventDate(event.eventDate);
    setSubjectId(event.subjectId ?? "");
    setDescription(event.description ?? "");
    setShowForm(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !eventDate) return;
    setSubmitting(true);
    const request: SchoolEventRequest = {
      title: title.trim(),
      eventType,
      eventDate,
      subjectId: subjectId || null,
      description: description.trim() || undefined,
    };
    try {
      if (editingEvent) {
        await updateSchoolEvent(editingEvent.id, request);
      } else {
        await createSchoolEvent(request);
      }
      resetForm();
      loadData();
    } catch (err) {
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteSchoolEvent(deleteTarget);
      loadData();
    } catch (err) {
      console.error(err);
    } finally {
      setDeleteTarget(null);
    }
  };

  if (loading) {
    return <div className="text-gray-500">{t("common.loading")}</div>;
  }

  return (
    <div>
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">
          {t("events.title")}
        </h1>
        {!showForm && (
          <button
            onClick={openCreateForm}
            className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
          >
            {t("events.createTitle")}
          </button>
        )}
      </div>

      {showForm && (
        <form
          onSubmit={handleSubmit}
          className="mt-6 rounded-lg border border-gray-200 bg-white p-4"
        >
          <h2 className="text-lg font-semibold text-gray-900">
            {editingEvent ? t("events.editTitle") : t("events.createTitle")}
          </h2>
          <div className="mt-4 grid gap-4 sm:grid-cols-2">
            <div>
              <label
                htmlFor="event-title"
                className="block text-sm font-medium text-gray-700"
              >
                {t("events.titleLabel")}
              </label>
              <input
                id="event-title"
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder={t("events.titlePlaceholder")}
                className="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label
                htmlFor="event-type"
                className="block text-sm font-medium text-gray-700"
              >
                {t("events.type")}
              </label>
              <select
                id="event-type"
                value={eventType}
                onChange={(e) => setEventType(e.target.value as EventType)}
                className="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              >
                {EVENT_TYPES.map((type) => (
                  <option key={type} value={type}>
                    {t(`events.type${type}`)}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label
                htmlFor="event-date"
                className="block text-sm font-medium text-gray-700"
              >
                {t("events.date")}
              </label>
              <input
                id="event-date"
                type="date"
                value={eventDate}
                onChange={(e) => setEventDate(e.target.value)}
                className="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label
                htmlFor="event-subject"
                className="block text-sm font-medium text-gray-700"
              >
                {t("events.subject")}
              </label>
              <select
                id="event-subject"
                value={subjectId}
                onChange={(e) => setSubjectId(e.target.value)}
                className="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              >
                <option value="">{t("events.subjectNone")}</option>
                {subjects.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="sm:col-span-2">
              <label
                htmlFor="event-description"
                className="block text-sm font-medium text-gray-700"
              >
                {t("events.description")}
              </label>
              <textarea
                id="event-description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder={t("events.descriptionPlaceholder")}
                rows={2}
                className="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              />
            </div>
          </div>
          <div className="mt-4 flex justify-end gap-3">
            <button
              type="button"
              onClick={resetForm}
              className="rounded-md bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200"
            >
              {t("events.cancelButton")}
            </button>
            <button
              type="submit"
              disabled={submitting || !title.trim() || !eventDate}
              className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
            >
              {t("events.saveButton")}
            </button>
          </div>
        </form>
      )}

      {events.length === 0 && !showForm ? (
        <div className="mt-6">
          <EmptyState
            icon={
              <svg
                className="h-12 w-12"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 012.25-2.25h13.5A2.25 2.25 0 0121 7.5v11.25m-18 0A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75m-18 0v-7.5A2.25 2.25 0 015.25 9h13.5A2.25 2.25 0 0121 11.25v7.5"
                />
              </svg>
            }
            message={t("events.emptyTitle")}
            actionLabel={t("events.emptyAction")}
            onAction={openCreateForm}
          />
        </div>
      ) : (
        events.length > 0 && (
          <ul className="mt-6 divide-y divide-gray-200 rounded-lg border border-gray-200 bg-white">
            {events.map((event) => (
              <li
                key={event.id}
                className="flex items-center justify-between px-4 py-3"
              >
                <div className="flex flex-col gap-1">
                  <div className="flex items-center gap-3">
                    <span className="font-medium text-gray-900">
                      {event.title}
                    </span>
                    <EventTypeBadge
                      type={event.eventType}
                      label={t(`events.type${event.eventType}`)}
                    />
                  </div>
                  <div className="flex items-center gap-3 text-sm text-gray-500">
                    <span>{event.eventDate}</span>
                    {event.subjectName && <span>{event.subjectName}</span>}
                  </div>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => openEditForm(event)}
                    className="text-sm text-indigo-600 hover:text-indigo-800"
                  >
                    {t("common.edit")}
                  </button>
                  <button
                    onClick={() => setDeleteTarget(event.id)}
                    className="text-sm text-red-600 hover:text-red-800"
                  >
                    {t("common.delete")}
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )
      )}

      <ConfirmDialog
        open={deleteTarget !== null}
        title={t("events.deleteConfirmTitle")}
        message={t("events.deleteConfirmMessage")}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
