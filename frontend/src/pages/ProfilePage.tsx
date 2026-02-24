import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { getProfile, updateProfile } from "../services/profileService";
import type { StudentProfile } from "../types";

export default function ProfilePage() {
  const { t } = useTranslation();
  const [, setProfile] = useState<StudentProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [grade, setGrade] = useState("");
  const [school, setSchool] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    getProfile()
      .then((data) => {
        setProfile(data);
        setGrade(data.grade || "");
        setSchool(data.school || "");
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setMessage("");
    try {
      const updated = await updateProfile({ grade, school });
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
          <input
            id="grade"
            type="text"
            value={grade}
            onChange={(e) => setGrade(e.target.value)}
            placeholder={t("profile.gradePlaceholder")}
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
    </div>
  );
}
