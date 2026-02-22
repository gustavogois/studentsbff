import type { SchoolEvent } from "../types";

const typeBadgeStyles: Record<SchoolEvent["eventType"], string> = {
  EXAM: "bg-red-100 text-red-800",
  ASSIGNMENT: "bg-blue-100 text-blue-800",
  DEADLINE: "bg-orange-100 text-orange-800",
  OTHER: "bg-gray-100 text-gray-800",
};

interface EventCardProps {
  event: SchoolEvent;
  onDelete: (id: string) => void;
}

export default function EventCard({ event, onDelete }: EventCardProps) {
  const badgeStyle = typeBadgeStyles[event.eventType] ?? typeBadgeStyles.OTHER;

  const formattedDate = new Date(event.eventDate).toLocaleDateString("en-US", {
    weekday: "short",
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

  return (
    <div className="rounded-lg border border-gray-200 bg-white p-4">
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="flex items-center gap-2">
            <h3 className="font-medium text-gray-900">{event.title}</h3>
            <span
              className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${badgeStyle}`}
            >
              {event.eventType}
            </span>
          </div>
          <p className="mt-1 text-sm text-gray-500">{formattedDate}</p>
          {event.subjectName && (
            <p className="mt-1 text-sm text-indigo-600">{event.subjectName}</p>
          )}
          {event.description && (
            <p className="mt-2 text-sm text-gray-600">{event.description}</p>
          )}
        </div>
        <button
          onClick={() => onDelete(event.id)}
          className="ml-4 text-sm text-red-600 hover:text-red-800"
        >
          Delete
        </button>
      </div>
    </div>
  );
}
