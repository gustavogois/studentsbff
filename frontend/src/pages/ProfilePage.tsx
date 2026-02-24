import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import {
  getGrades,
  getProfile,
  updateProfile,
} from "../services/profileService";
import type { GradeOption, StudentProfile } from "../types";

const LANGUAGES = [
  { code: "en", label: "English" },
  { code: "pt-BR", label: "Português (Brasil)" },
  { code: "pt-PT", label: "Português (Portugal)" },
];

export default function ProfilePage() {
  const { t, i18n } = useTranslation();
  const [, setProfile] = useState<StudentProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [grade, setGrade] = useState("");
  const [school, setSchool] = useState("");
  const [turma, setTurma] = useState("");
  const [grades, setGrades] = useState<GradeOption[]>([]);
  const [message, setMessage] = useState("");

  useEffect(() => {
    Promise.all([getProfile(), getGrades()])
      .then(([profileData, gradesData]) => {
        setProfile(profileData);
        setGrade(profileData.grade || "");
        setSchool(profileData.school || "");
        setTurma(profileData.turma || "");
        setGrades(gradesData);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setMessage("");
    try {
      const updated = await updateProfile({
        grade: grade || null,
        school,
        turma,
      });
      setProfile(updated);
      setMessage(t("profile.updateSuccess"));
    } catch (err) {
      console.error(err);
      setMessage(t("profile.updateError"));
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="text-gray-500">{t("common.loading")}</div>;
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900">
        {t("profile.title")}
      </h1>

      <form onSubmit={handleSubmit} className="mt-6 max-w-md space-y-4">
        <div>
          <label
            htmlFor="grade"
            className="block text-sm font-medium text-gray-700"
          >
            {t("profile.grade")}
          </label>
          <select
            id="grade"
            value={grade}
            onChange={(e) => setGrade(e.target.value)}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          >
            <option value="">{t("profile.gradePlaceholder")}</option>
            {grades.map((g) => (
              <option key={g.value} value={g.value}>
                {t(`grades.${g.value}`, g.label)}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label
            htmlFor="turma"
            className="block text-sm font-medium text-gray-700"
          >
            {t("profile.turma")}
          </label>
          <input
            id="turma"
            type="text"
            value={turma}
            onChange={(e) => setTurma(e.target.value)}
            placeholder={t("profile.turmaPlaceholder")}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          />
        </div>

        <div>
          <label
            htmlFor="school"
            className="block text-sm font-medium text-gray-700"
          >
            {t("profile.school")}
          </label>
          <input
            id="school"
            type="text"
            value={school}
            onChange={(e) => setSchool(e.target.value)}
            placeholder={t("profile.schoolPlaceholder")}
            className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          />
        </div>

        <button
          type="submit"
          disabled={saving}
          className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
        >
          {saving ? t("profile.saving") : t("profile.saveButton")}
        </button>

        {message && (
          <p
            className={`text-sm ${message === t("profile.updateSuccess") ? "text-green-600" : "text-red-600"}`}
          >
            {message}
          </p>
        )}
      </form>

      <div className="mt-8 max-w-md">
        <h2 className="text-lg font-semibold text-gray-900">
          {t("profile.language")}
        </h2>
        <div className="mt-3 flex gap-2">
          {LANGUAGES.map((lang) => (
            <button
              key={lang.code}
              onClick={() => i18n.changeLanguage(lang.code)}
              className={`rounded-md px-4 py-2 text-sm font-medium ${
                i18n.resolvedLanguage === lang.code
                  ? "bg-indigo-600 text-white"
                  : "bg-gray-100 text-gray-700 hover:bg-gray-200"
              }`}
            >
              {lang.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
