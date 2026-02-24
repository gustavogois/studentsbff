import { useTranslation } from "react-i18next";
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
      <p className="text-sm text-gray-500">{t("topics.emptyTitle")}</p>
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
