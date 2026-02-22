import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

const mockUser = {
  id: "1",
  name: "Test User",
  email: "test@example.com",
  role: "STUDENT",
  avatarUrl: null,
};

vi.mock("../../contexts/AuthContext", () => ({
  useAuth: () => ({
    user: mockUser,
    isLoading: false,
  }),
}));

vi.mock("../../services/subjectService", () => ({
  getSubjects: vi.fn(),
}));

import { getSubjects } from "../../services/subjectService";
import DashboardPage from "../DashboardPage";

const mockedGetSubjects = vi.mocked(getSubjects);

describe("DashboardPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should show welcome message", async () => {
    mockedGetSubjects.mockResolvedValueOnce([]);

    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText("Hello, Test User!")).toBeInTheDocument();
    });
  });

  it("should show subject cards", async () => {
    mockedGetSubjects.mockResolvedValueOnce([
      {
        id: "1",
        name: "Math",
        createdAt: "2024-01-01",
        updatedAt: "2024-01-01",
      },
      {
        id: "2",
        name: "Science",
        createdAt: "2024-01-01",
        updatedAt: "2024-01-01",
      },
    ]);

    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText("Math")).toBeInTheDocument();
      expect(screen.getByText("Science")).toBeInTheDocument();
    });
  });

  it("should show empty state", async () => {
    mockedGetSubjects.mockResolvedValueOnce([]);

    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText("No subjects yet.")).toBeInTheDocument();
    });
  });
});
