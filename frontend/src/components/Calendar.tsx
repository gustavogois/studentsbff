import { useTranslation } from "react-i18next";
import type { SchoolEvent, EventType } from "../types";

interface CalendarProps {
  year: number;
  month: number; // 0-indexed (0=Jan, 11=Dec)
  events: SchoolEvent[];
  onEventClick?: (event: SchoolEvent) => void;
  onMonthChange?: (year: number, month: number) => void;
}

const EVENT_DOT_COLORS: Record<EventType, string> = {
  EXAM: "bg-red-500",
  ASSIGNMENT: "bg-blue-500",
  DEADLINE: "bg-orange-500",
};

const MONTH_NAMES = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];

function getDaysInMonth(year: number, month: number): number {
  return new Date(year, month + 1, 0).getDate();
}

function getFirstDayOfWeek(year: number, month: number): number {
  return new Date(year, month, 1).getDay(); // 0=Sun
}

function formatDateStr(year: number, month: number, day: number): string {
  const m = String(month + 1).padStart(2, "0");
  const d = String(day).padStart(2, "0");
  return `${year}-${m}-${d}`;
}

export default function Calendar({
  year,
  month,
  events,
  onEventClick,
  onMonthChange,
}: CalendarProps) {
  const { t } = useTranslation();

  const daysInMonth = getDaysInMonth(year, month);
  const firstDay = getFirstDayOfWeek(year, month);

  const today = new Date();
  const isCurrentMonth =
    today.getFullYear() === year && today.getMonth() === month;
  const todayDate = today.getDate();

  const eventsByDate = new Map<string, SchoolEvent[]>();
  for (const event of events) {
    const key = event.eventDate;
    if (!eventsByDate.has(key)) {
      eventsByDate.set(key, []);
    }
    eventsByDate.get(key)!.push(event);
  }

  const handlePrev = () => {
    if (!onMonthChange) return;
    if (month === 0) {
      onMonthChange(year - 1, 11);
    } else {
      onMonthChange(year, month - 1);
    }
  };

  const handleNext = () => {
    if (!onMonthChange) return;
    if (month === 11) {
      onMonthChange(year + 1, 0);
    } else {
      onMonthChange(year, month + 1);
    }
  };

  const dayHeaders = [
    t("calendar.sun"),
    t("calendar.mon"),
    t("calendar.tue"),
    t("calendar.wed"),
    t("calendar.thu"),
    t("calendar.fri"),
    t("calendar.sat"),
  ];

  const cells: (number | null)[] = [];
  for (let i = 0; i < firstDay; i++) {
    cells.push(null);
  }
  for (let d = 1; d <= daysInMonth; d++) {
    cells.push(d);
  }

  return (
    <div className="rounded-lg border border-gray-200 bg-white">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-gray-200 px-4 py-3">
        <button
          onClick={handlePrev}
          aria-label="prev-month"
          className="rounded p-1 text-gray-500 hover:bg-gray-100 hover:text-gray-700"
        >
          <svg
            className="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M15 19l-7-7 7-7"
            />
          </svg>
        </button>
        <h2 className="text-lg font-semibold text-gray-900">
          {MONTH_NAMES[month]} {year}
        </h2>
        <button
          onClick={handleNext}
          aria-label="next-month"
          className="rounded p-1 text-gray-500 hover:bg-gray-100 hover:text-gray-700"
        >
          <svg
            className="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 5l7 7-7 7"
            />
          </svg>
        </button>
      </div>

      {/* Day headers */}
      <div className="grid grid-cols-7 border-b border-gray-200">
        {dayHeaders.map((day) => (
          <div
            key={day}
            className="py-2 text-center text-xs font-medium text-gray-500"
          >
            {day}
          </div>
        ))}
      </div>

      {/* Day cells */}
      <div className="grid grid-cols-7">
        {cells.map((day, idx) => {
          if (day === null) {
            return <div key={`empty-${idx}`} className="min-h-[80px] p-1" />;
          }

          const dateStr = formatDateStr(year, month, day);
          const dayEvents = eventsByDate.get(dateStr) ?? [];
          const isToday = isCurrentMonth && day === todayDate;

          return (
            <div
              key={day}
              data-today={isToday ? "true" : undefined}
              className={`min-h-[80px] border-t border-gray-100 p-1 ${
                isToday ? "bg-indigo-50" : ""
              }`}
            >
              <div
                className={`mb-1 text-right text-sm ${
                  isToday
                    ? "inline-flex h-6 w-6 items-center justify-center rounded-full bg-indigo-600 text-white"
                    : "text-gray-700"
                }`}
              >
                {day}
              </div>
              <div className="flex flex-col gap-0.5">
                {dayEvents.map((event) => (
                  <button
                    key={event.id}
                    onClick={() => onEventClick?.(event)}
                    className={`truncate rounded px-1 text-left text-xs text-white ${EVENT_DOT_COLORS[event.eventType]}`}
                  >
                    {event.title}
                  </button>
                ))}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
