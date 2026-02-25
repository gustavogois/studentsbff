import { afterEach, describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";

vi.mock("../../services/schoolEventService", () => ({
  getSchoolEvents: vi.fn(),
}));

import { getSchoolEvents } from "../../services/schoolEventService";
import CalendarPage from "../CalendarPage";

const mockedGetSchoolEvents = vi.mocked(getSchoolEvents);

const mockEvent = {
  id: "1",
  title: "Math Exam",
  eventType: "EXAM" as const,
  eventDate: "2026-03-15",
  subjectId: null,
  subjectName: null,
  description: null,
  createdAt: "",
  updatedAt: "",
};

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return { ...actual, useNavigate: () => mockNavigate };
});

describe("CalendarPage", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.useFakeTimers({ shouldAdvanceTime: true });
    vi.setSystemTime(new Date(2026, 2, 10)); // March 10, 2026
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("should fetch events for current month", async () => {
    mockedGetSchoolEvents.mockResolvedValueOnce([mockEvent]);

    render(
      <MemoryRouter>
        <CalendarPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(mockedGetSchoolEvents).toHaveBeenCalledWith(
        "2026-03-01",
        "2026-03-31",
      );
    });

    expect(screen.getByText("Math Exam")).toBeInTheDocument();
  });

  it("should refetch on month change", async () => {
    mockedGetSchoolEvents
      .mockResolvedValueOnce([mockEvent])
      .mockResolvedValueOnce([]);

    const user = userEvent.setup({ advanceTimers: vi.advanceTimersByTime });
    render(
      <MemoryRouter>
        <CalendarPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(mockedGetSchoolEvents).toHaveBeenCalledTimes(1);
    });

    await user.click(screen.getByLabelText("next-month"));

    await waitFor(() => {
      expect(mockedGetSchoolEvents).toHaveBeenCalledWith(
        "2026-04-01",
        "2026-04-30",
      );
    });
  });

  it("should show loading state", () => {
    mockedGetSchoolEvents.mockReturnValue(new Promise(() => {})); // never resolves

    render(
      <MemoryRouter>
        <CalendarPage />
      </MemoryRouter>,
    );

    expect(screen.getByText("common.loading")).toBeInTheDocument();
  });

  it("should navigate to events on event click", async () => {
    mockedGetSchoolEvents.mockResolvedValueOnce([mockEvent]);

    const user = userEvent.setup({ advanceTimers: vi.advanceTimersByTime });
    render(
      <MemoryRouter>
        <CalendarPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("Math Exam")).toBeInTheDocument();
    });

    await user.click(screen.getByText("Math Exam"));

    expect(mockNavigate).toHaveBeenCalledWith("/events");
  });
});
