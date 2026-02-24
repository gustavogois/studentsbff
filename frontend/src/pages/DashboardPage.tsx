import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuth } from "../contexts/AuthContext";
import SubjectCard from "../components/SubjectCard";
import EmptyState from "../components/EmptyState";
import { getSubjects } from "../services/subjectService";
import type { Subject } from "../types";

const BookIcon = () => (
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
      d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18a8.967 8.967 0 00-6 2.292m0-14.25v14.25"
    />
  </svg>
);

export default function DashboardPage() {
  const { t } = useTranslation();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getSubjects()
      .then(setSubjects)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="text-gray-500">{t("common.loading")}</div>;
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900">
        {t("dashboard.hello", { name: user?.name ?? "" })}
      </h1>
      <p className="mt-1 text-gray-600">{t("dashboard.welcome")}</p>

      <div className="mt-8">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">
            {t("dashboard.yourSubjects")}
          </h2>
          <Link
            to="/subjects"
            className="text-sm text-indigo-600 hover:text-indigo-800"
          >
            {t("dashboard.manageSubjects")}
          </Link>
        </div>

        {subjects.length === 0 ? (
          <div className="mt-4">
            <EmptyState
              icon={<BookIcon />}
              message={t("dashboard.emptyTitle")}
              actionLabel={t("dashboard.emptyAction")}
              onAction={() => navigate("/subjects")}
            />
          </div>
        ) : (
          <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {subjects.map((subject) => (
              <SubjectCard key={subject.id} subject={subject} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
