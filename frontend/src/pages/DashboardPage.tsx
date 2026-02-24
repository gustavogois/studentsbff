import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuth } from "../contexts/AuthContext";
import SubjectCard from "../components/SubjectCard";
import { getSubjects } from "../services/subjectService";
import type { Subject } from "../types";

export default function DashboardPage() {
  const { t } = useTranslation();
  const { user } = useAuth();
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
          <div className="mt-4 rounded-lg border-2 border-dashed border-gray-300 p-8 text-center">
            <p className="text-gray-500">{t("dashboard.emptyTitle")}</p>
            <Link
              to="/subjects"
              className="mt-2 inline-block text-sm text-indigo-600 hover:text-indigo-800"
            >
              {t("dashboard.emptyAction")}
            </Link>
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
