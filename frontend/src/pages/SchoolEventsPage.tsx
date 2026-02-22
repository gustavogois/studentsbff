import { useEffect, useState } from "react";
import {
  getSchoolEvents,
  deleteSchoolEvent,
} from "../services/schoolEventService";
import { syncGmail } from "../services/gmailSyncService";
import type { SchoolEvent, GmailSyncResult } from "../types";
import EventCard from "../components/EventCard";

export default function SchoolEventsPage() {
  const [events, setEvents] = useState<SchoolEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [syncing, setSyncing] = useState(false);
  const [syncResult, setSyncResult] = useState<GmailSyncResult | null>(null);
  const [error, setError] = useState("");

  const loadEvents = () => {
    getSchoolEvents()
      .then(setEvents)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadEvents();
  }, []);

  const handleSync = async () => {
    setSyncing(true);
    setSyncResult(null);
    setError("");
    try {
      const result = await syncGmail(30);
      setSyncResult(result);
      loadEvents();
    } catch (err) {
      setError("Failed to sync Gmail. Please make sure you have connected your Google account.");
      console.error(err);
    } finally {
      setSyncing(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm("Delete this event?")) return;
    try {
      await deleteSchoolEvent(id);
      loadEvents();
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) {
    return <div className="text-gray-500">Loading...</div>;
  }

  return (
    <div>
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">School Events</h1>
        <button
          onClick={handleSync}
          disabled={syncing}
          className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
        >
          {syncing ? "Syncing..." : "Sync Gmail"}
        </button>
      </div>

      {syncResult && (
        <div className="mt-4 rounded-md bg-green-50 p-3 text-sm text-green-800">
          Found {syncResult.newEventsCount} new event
          {syncResult.newEventsCount !== 1 ? "s" : ""} (
          {syncResult.skippedDuplicates} duplicate
          {syncResult.skippedDuplicates !== 1 ? "s" : ""} skipped,{" "}
          {syncResult.totalEmails} email
          {syncResult.totalEmails !== 1 ? "s" : ""} scanned)
        </div>
      )}

      {error && (
        <div className="mt-4 rounded-md bg-red-50 p-3 text-sm text-red-800">
          {error}
        </div>
      )}

      {events.length === 0 ? (
        <div className="mt-6 rounded-lg border-2 border-dashed border-gray-300 p-8 text-center">
          <p className="text-gray-500">
            No events yet. Click &quot;Sync Gmail&quot; to import school events
            from your email.
          </p>
        </div>
      ) : (
        <div className="mt-6 space-y-3">
          {events.map((event) => (
            <EventCard key={event.id} event={event} onDelete={handleDelete} />
          ))}
        </div>
      )}
    </div>
  );
}
