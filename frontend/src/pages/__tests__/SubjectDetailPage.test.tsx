import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Routes, Route } from "react-router-dom";

vi.mock("../../services/subjectService", () => ({
  getSubject: vi.fn(),
  deleteSubject: vi.fn(),
}));

vi.mock("../../services/topicService", () => ({
  getTopics: vi.fn(),
  createTopic: vi.fn(),
  updateTopic: vi.fn(),
  deleteTopic: vi.fn(),
}));

import { getSubject } from "../../services/subjectService";
import {
  getTopics,
  createTopic,
  deleteTopic,
} from "../../services/topicService";
import SubjectDetailPage from "../SubjectDetailPage";

const mockedGetSubject = vi.mocked(getSubject);
const mockedGetTopics = vi.mocked(getTopics);
const mockedCreateTopic = vi.mocked(createTopic);
const mockedDeleteTopic = vi.mocked(deleteTopic);

function renderWithRouter() {
  return render(
    <MemoryRouter initialEntries={["/subjects/sub-1"]}>
      <Routes>
        <Route path="/subjects/:id" element={<SubjectDetailPage />} />
        <Route path="/subjects" element={<div>Subjects List</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe("SubjectDetailPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should show subject name", async () => {
    mockedGetSubject.mockResolvedValueOnce({
      id: "sub-1",
      name: "Mathematics",
      createdAt: "",
      updatedAt: "",
    });
    mockedGetTopics.mockResolvedValueOnce([]);

    renderWithRouter();

    await waitFor(() => {
      expect(screen.getByText("Mathematics")).toBeInTheDocument();
    });
  });

  it("should list topics", async () => {
    mockedGetSubject.mockResolvedValueOnce({
      id: "sub-1",
      name: "Mathematics",
      createdAt: "",
      updatedAt: "",
    });
    mockedGetTopics.mockResolvedValueOnce([
      {
        id: "t-1",
        name: "Fractions",
        difficulty: 4,
        createdAt: "",
        updatedAt: "",
      },
    ]);

    renderWithRouter();

    await waitFor(() => {
      expect(screen.getByText("Fractions")).toBeInTheDocument();
      expect(screen.getByText("4/5")).toBeInTheDocument();
    });
  });

  it("should create topic", async () => {
    mockedGetSubject
      .mockResolvedValueOnce({
        id: "sub-1",
        name: "Mathematics",
        createdAt: "",
        updatedAt: "",
      })
      .mockResolvedValueOnce({
        id: "sub-1",
        name: "Mathematics",
        createdAt: "",
        updatedAt: "",
      });
    mockedGetTopics.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    mockedCreateTopic.mockResolvedValueOnce({
      id: "t-1",
      name: "Fractions",
      difficulty: 3,
      createdAt: "",
      updatedAt: "",
    });

    const user = userEvent.setup();
    renderWithRouter();

    await waitFor(() => {
      expect(screen.getByPlaceholderText("Topic name")).toBeInTheDocument();
    });

    await user.type(screen.getByPlaceholderText("Topic name"), "Fractions");
    await user.click(screen.getByText("Add Topic"));

    await waitFor(() => {
      expect(mockedCreateTopic).toHaveBeenCalledWith("sub-1", {
        name: "Fractions",
        difficulty: 3,
      });
    });
  });

  it("should delete topic", async () => {
    mockedGetSubject
      .mockResolvedValueOnce({
        id: "sub-1",
        name: "Mathematics",
        createdAt: "",
        updatedAt: "",
      })
      .mockResolvedValueOnce({
        id: "sub-1",
        name: "Mathematics",
        createdAt: "",
        updatedAt: "",
      });
    mockedGetTopics
      .mockResolvedValueOnce([
        {
          id: "t-1",
          name: "Fractions",
          difficulty: 4,
          createdAt: "",
          updatedAt: "",
        },
      ])
      .mockResolvedValueOnce([]);
    mockedDeleteTopic.mockResolvedValueOnce();

    const user = userEvent.setup();
    renderWithRouter();

    await waitFor(() => {
      expect(screen.getByText("Fractions")).toBeInTheDocument();
    });

    await user.click(screen.getByText("Delete"));

    await waitFor(() => {
      expect(mockedDeleteTopic).toHaveBeenCalledWith("sub-1", "t-1");
    });
  });
});
