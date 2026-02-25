import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Calendar from "../components/Calendar";
import { getSchoolEvents } from "../services/schoolEventService";
import type { SchoolEvent } from "../types";

function formatMonthRange(year: number, month: number) {
  const m = String(month + 1).padStart(2, "0");
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const from = `${year}-${m}-01`;
  const to = `${year}-${m}-${String(daysInMonth).padStart(2, "0")}`;
  return { from, to };
}

export default function CalendarPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const now = new Date();

  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth());
  const [events, setEvents] = useState<SchoolEvent[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchEvents = useCallback((y: number, m: number) => {
    setLoading(true);
    const { from, to } = formatMonthRange(y, m);
    getSchoolEvents(from, to)
      .then(setEvents)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    fetchEvents(year, month);
  }, [year, month, fetchEvents]);

  const handleMonthChange = (newYear: number, newMonth: number) => {
    setYear(newYear);
    setMonth(newMonth);
  };

  const handleEventClick = () => {
    navigate("/events");
  };

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">
        {t("calendar.title")}
      </h1>
      <div className="relative">
        {loading && (
          <div className="absolute inset-0 z-10 flex items-center justify-center rounded-lg bg-white/60">
            <span className="text-gray-500">{t("common.loading")}</span>
          </div>
        )}
        <Calendar
          year={year}
          month={month}
          events={events}
          onMonthChange={handleMonthChange}
          onEventClick={handleEventClick}
        />
      </div>
    </div>
  );
}
