import { useTranslation } from "react-i18next";
import EmptyState from "./EmptyState";
import type { Topic } from "../types";

interface TopicListProps {
  topics: Topic[];
  onDelete: (id: string) => void;
  onEdit: (topic: Topic) => void;
}

function DifficultyBadge({ level }: { level: number }) {
  const colors = [
    "",
    "bg-green-100 text-green-800",
    "bg-lime-100 text-lime-800",
    "bg-yellow-100 text-yellow-800",
    "bg-orange-100 text-orange-800",
    "bg-red-100 text-red-800",
  ];

  return (
    <span
      className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${colors[level]}`}
    >
      {level}/5
    </span>
  );
}

export default function TopicList({
  topics,
  onDelete,
  onEdit,
}: TopicListProps) {
  const { t } = useTranslation();

  if (topics.length === 0) {
    return (
      <EmptyState
        icon={
          <svg
            className="h-10 w-10"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={1.5}
              d="M8.25 6.75h12M8.25 12h12m-12 5.25h12M3.75 6.75h.007v.008H3.75V6.75zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zM3.75 12h.007v.008H3.75V12zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm-.375 5.25h.007v.008H3.75v-.008zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z"
            />
          </svg>
        }
        message={t("topics.emptyTitle")}
      />
    );
  }

  return (
    <ul className="divide-y divide-gray-200">
      {topics.map((topic) => (
        <li key={topic.id} className="flex items-center justify-between py-3">
          <div className="flex items-center gap-3">
            <span className="text-gray-900">{topic.name}</span>
            <DifficultyBadge level={topic.difficulty} />
          </div>
          <div className="flex gap-2">
            <button
              onClick={() => onEdit(topic)}
              className="text-sm text-indigo-600 hover:text-indigo-800"
            >
              {t("common.edit")}
            </button>
            <button
              onClick={() => onDelete(topic.id)}
              className="text-sm text-red-600 hover:text-red-800"
            >
              {t("common.delete")}
            </button>
          </div>
        </li>
      ))}
    </ul>
  );
}
