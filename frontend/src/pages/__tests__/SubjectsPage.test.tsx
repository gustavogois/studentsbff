import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";

vi.mock("../../services/subjectService", () => ({
  getSubjects: vi.fn(),
  createSubject: vi.fn(),
  deleteSubject: vi.fn(),
}));

import {
  getSubjects,
  createSubject,
  deleteSubject,
} from "../../services/subjectService";
import SubjectsPage from "../SubjectsPage";

const mockedGetSubjects = vi.mocked(getSubjects);
const mockedCreateSubject = vi.mocked(createSubject);
const mockedDeleteSubject = vi.mocked(deleteSubject);

describe("SubjectsPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should list subjects", async () => {
    mockedGetSubjects.mockResolvedValueOnce([
      { id: "1", name: "Math", createdAt: "", updatedAt: "" },
    ]);

    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText("Math")).toBeInTheDocument();
    });
  });

  it("should create subject", async () => {
    mockedGetSubjects
      .mockResolvedValueOnce([])
      .mockResolvedValueOnce([
        { id: "1", name: "Math", createdAt: "", updatedAt: "" },
      ]);
    mockedCreateSubject.mockResolvedValueOnce({
      id: "1",
      name: "Math",
      createdAt: "",
      updatedAt: "",
    });

    const user = userEvent.setup();
    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(
        screen.getByPlaceholderText("New subject name")
      ).toBeInTheDocument();
    });

    await user.type(screen.getByPlaceholderText("New subject name"), "Math");
    await user.click(screen.getByText("Add Subject"));

    await waitFor(() => {
      expect(mockedCreateSubject).toHaveBeenCalledWith({ name: "Math" });
    });
  });

  it("should delete subject", async () => {
    mockedGetSubjects
      .mockResolvedValueOnce([
        { id: "1", name: "Math", createdAt: "", updatedAt: "" },
      ])
      .mockResolvedValueOnce([]);
    mockedDeleteSubject.mockResolvedValueOnce();

    window.confirm = vi.fn(() => true);
    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText("Math")).toBeInTheDocument();
    });

    await user.click(screen.getByText("Delete"));

    await waitFor(() => {
      expect(mockedDeleteSubject).toHaveBeenCalledWith("1");
    });
  });

  it("should show empty state", async () => {
    mockedGetSubjects.mockResolvedValueOnce([]);

    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(
        screen.getByText("No subjects yet. Add one above.")
      ).toBeInTheDocument();
    });
  });
});
