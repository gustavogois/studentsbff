import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";

vi.mock("../../services/subjectService", () => ({
  getSubjects: vi.fn(),
  createSubject: vi.fn(),
  updateSubject: vi.fn(),
  deleteSubject: vi.fn(),
}));

import {
  getSubjects,
  createSubject,
  updateSubject,
  deleteSubject,
} from "../../services/subjectService";
import SubjectsPage from "../SubjectsPage";

const mockedGetSubjects = vi.mocked(getSubjects);
const mockedCreateSubject = vi.mocked(createSubject);
const mockedUpdateSubject = vi.mocked(updateSubject);
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
      </MemoryRouter>,
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
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(
        screen.getByPlaceholderText("subjects.newPlaceholder"),
      ).toBeInTheDocument();
    });

    await user.type(
      screen.getByPlaceholderText("subjects.newPlaceholder"),
      "Math",
    );
    await user.click(screen.getByText("subjects.addButton"));

    await waitFor(() => {
      expect(mockedCreateSubject).toHaveBeenCalledWith({ name: "Math" });
    });
  });

  it("should show confirm dialog and delete subject on confirm", async () => {
    mockedGetSubjects
      .mockResolvedValueOnce([
        { id: "1", name: "Math", createdAt: "", updatedAt: "" },
      ])
      .mockResolvedValueOnce([]);
    mockedDeleteSubject.mockResolvedValueOnce();

    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("Math")).toBeInTheDocument();
    });

    await user.click(screen.getByText("common.delete"));

    // Confirm dialog should appear
    expect(
      screen.getByText("subjects.deleteConfirmMessage"),
    ).toBeInTheDocument();

    // Click confirm
    await user.click(screen.getByText("confirm.delete"));

    await waitFor(() => {
      expect(mockedDeleteSubject).toHaveBeenCalledWith("1");
    });
  });

  it("should cancel delete when dialog cancelled", async () => {
    mockedGetSubjects.mockResolvedValueOnce([
      { id: "1", name: "Math", createdAt: "", updatedAt: "" },
    ]);

    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("Math")).toBeInTheDocument();
    });

    await user.click(screen.getByText("common.delete"));

    // Confirm dialog should appear
    expect(
      screen.getByText("subjects.deleteConfirmMessage"),
    ).toBeInTheDocument();

    // Click cancel
    await user.click(screen.getByText("confirm.cancel"));

    // Dialog should close, no delete
    expect(
      screen.queryByText("subjects.deleteConfirmMessage"),
    ).not.toBeInTheDocument();
    expect(mockedDeleteSubject).not.toHaveBeenCalled();
  });

  it("should show empty state", async () => {
    mockedGetSubjects.mockResolvedValueOnce([]);

    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("subjects.emptyTitle")).toBeInTheDocument();
    });
  });

  it("should show edit button on each subject", async () => {
    mockedGetSubjects.mockResolvedValueOnce([
      { id: "1", name: "Math", createdAt: "", updatedAt: "" },
    ]);

    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("common.edit")).toBeInTheDocument();
    });
  });

  it("should enter edit mode on click", async () => {
    mockedGetSubjects.mockResolvedValueOnce([
      { id: "1", name: "Math", createdAt: "", updatedAt: "" },
    ]);

    const user = userEvent.setup();
    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("Math")).toBeInTheDocument();
    });

    await user.click(screen.getByText("common.edit"));

    expect(screen.getByDisplayValue("Math")).toBeInTheDocument();
  });

  it("should save on Enter", async () => {
    mockedGetSubjects
      .mockResolvedValueOnce([
        { id: "1", name: "Math", createdAt: "", updatedAt: "" },
      ])
      .mockResolvedValueOnce([
        { id: "1", name: "Mathematics", createdAt: "", updatedAt: "" },
      ]);
    mockedUpdateSubject.mockResolvedValueOnce({
      id: "1",
      name: "Mathematics",
      createdAt: "",
      updatedAt: "",
    });

    const user = userEvent.setup();
    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("Math")).toBeInTheDocument();
    });

    await user.click(screen.getByText("common.edit"));
    const input = screen.getByDisplayValue("Math");
    await user.clear(input);
    await user.type(input, "Mathematics{Enter}");

    await waitFor(() => {
      expect(mockedUpdateSubject).toHaveBeenCalledWith("1", {
        name: "Mathematics",
      });
    });
  });

  it("should cancel on Escape", async () => {
    mockedGetSubjects.mockResolvedValueOnce([
      { id: "1", name: "Math", createdAt: "", updatedAt: "" },
    ]);

    const user = userEvent.setup();
    render(
      <MemoryRouter>
        <SubjectsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("Math")).toBeInTheDocument();
    });

    await user.click(screen.getByText("common.edit"));
    const input = screen.getByDisplayValue("Math");
    await user.clear(input);
    await user.type(input, "New Name{Escape}");

    // After Escape, input should be gone and original name restored
    expect(screen.queryByDisplayValue("New Name")).not.toBeInTheDocument();
    expect(screen.getByText("Math")).toBeInTheDocument();
    expect(mockedUpdateSubject).not.toHaveBeenCalled();
  });
});
