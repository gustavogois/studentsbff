import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getSubject, deleteSubject } from "../services/subjectService";
import {
  getTopics,
  createTopic,
  updateTopic,
  deleteTopic,
} from "../services/topicService";
import TopicList from "../components/TopicList";
import type { Subject, Topic } from "../types";

export default function SubjectDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [subject, setSubject] = useState<Subject | null>(null);
  const [topics, setTopics] = useState<Topic[]>([]);
  const [loading, setLoading] = useState(true);
  const [topicName, setTopicName] = useState("");
  const [topicDifficulty, setTopicDifficulty] = useState(3);
  const [editingTopic, setEditingTopic] = useState<Topic | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const loadData = async () => {
    if (!id) return;
    try {
      const [subjectData, topicsData] = await Promise.all([
        getSubject(id),
        getTopics(id),
      ]);
      setSubject(subjectData);
      setTopics(topicsData);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [id]);

  const handleCreateOrUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id || !topicName.trim()) return;
    setSubmitting(true);
    try {
      if (editingTopic) {
        await updateTopic(id, editingTopic.id, {
          name: topicName.trim(),
          difficulty: topicDifficulty,
        });
        setEditingTopic(null);
      } else {
        await createTopic(id, {
          name: topicName.trim(),
          difficulty: topicDifficulty,
        });
      }
      setTopicName("");
      setTopicDifficulty(3);
      loadData();
    } catch (err) {
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = (topic: Topic) => {
    setEditingTopic(topic);
    setTopicName(topic.name);
    setTopicDifficulty(topic.difficulty);
  };

  const handleDeleteTopic = async (topicId: string) => {
    if (!id) return;
    try {
      await deleteTopic(id, topicId);
      loadData();
    } catch (err) {
      console.error(err);
    }
  };

  const handleDeleteSubject = async () => {
    if (!id || !window.confirm("Delete this subject and all its topics?"))
      return;
    try {
      await deleteSubject(id);
      navigate("/subjects", { replace: true });
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) {
    return <div className="text-gray-500">Loading...</div>;
  }

  if (!subject) {
    return <div className="text-red-500">Subject not found.</div>;
  }

  return (
    <div>
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">{subject.name}</h1>
        <button
          onClick={handleDeleteSubject}
          className="rounded-md bg-red-50 px-3 py-1.5 text-sm text-red-600 hover:bg-red-100"
        >
          Delete Subject
        </button>
      </div>

      <div className="mt-8">
        <h2 className="text-lg font-semibold text-gray-900">Topics</h2>

        <form onSubmit={handleCreateOrUpdate} className="mt-4 flex gap-3">
          <input
            type="text"
            value={topicName}
            onChange={(e) => setTopicName(e.target.value)}
            placeholder="Topic name"
            className="flex-1 rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          />
          <select
            value={topicDifficulty}
            onChange={(e) => setTopicDifficulty(Number(e.target.value))}
            className="rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          >
            {[1, 2, 3, 4, 5].map((d) => (
              <option key={d} value={d}>
                Difficulty {d}
              </option>
            ))}
          </select>
          <button
            type="submit"
            disabled={submitting || !topicName.trim()}
            className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
          >
            {editingTopic ? "Update" : "Add Topic"}
          </button>
          {editingTopic && (
            <button
              type="button"
              onClick={() => {
                setEditingTopic(null);
                setTopicName("");
                setTopicDifficulty(3);
              }}
              className="rounded-md border border-gray-300 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
            >
              Cancel
            </button>
          )}
        </form>

        <div className="mt-4">
          <TopicList
            topics={topics}
            onDelete={handleDeleteTopic}
            onEdit={handleEdit}
          />
        </div>
      </div>
    </div>
  );
}
