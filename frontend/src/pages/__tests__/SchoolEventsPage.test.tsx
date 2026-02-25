import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";

vi.mock("../../services/schoolEventService", () => ({
  getSchoolEvents: vi.fn(),
  createSchoolEvent: vi.fn(),
  updateSchoolEvent: vi.fn(),
  deleteSchoolEvent: vi.fn(),
}));

vi.mock("../../services/subjectService", () => ({
  getSubjects: vi.fn(),
}));

import {
  getSchoolEvents,
  createSchoolEvent,
  deleteSchoolEvent,
} from "../../services/schoolEventService";
import { getSubjects } from "../../services/subjectService";
import SchoolEventsPage from "../SchoolEventsPage";

const mockedGetSchoolEvents = vi.mocked(getSchoolEvents);
const mockedCreateSchoolEvent = vi.mocked(createSchoolEvent);
const mockedDeleteSchoolEvent = vi.mocked(deleteSchoolEvent);
const mockedGetSubjects = vi.mocked(getSubjects);

const mockEvent = {
  id: "1",
  title: "Math Exam",
  eventType: "EXAM" as const,
  eventDate: "2026-03-15",
  subjectId: null,
  subjectName: null,
  description: null,
  createdAt: "2026-02-25T00:00:00Z",
  updatedAt: "2026-02-25T00:00:00Z",
};

describe("SchoolEventsPage", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    mockedGetSubjects.mockResolvedValue([]);
  });

  it("should display events list", async () => {
    mockedGetSchoolEvents.mockResolvedValueOnce([mockEvent]);

    render(
      <MemoryRouter>
        <SchoolEventsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("Math Exam")).toBeInTheDocument();
    });
    expect(screen.getByText("2026-03-15")).toBeInTheDocument();
    expect(screen.getByText("events.typeEXAM")).toBeInTheDocument();
  });

  it("should show empty state when no events", async () => {
    mockedGetSchoolEvents.mockResolvedValueOnce([]);

    render(
      <MemoryRouter>
        <SchoolEventsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("events.emptyTitle")).toBeInTheDocument();
    });
    expect(screen.getByText("events.emptyAction")).toBeInTheDocument();
  });

  it("should create event", async () => {
    mockedGetSchoolEvents
      .mockResolvedValueOnce([])
      .mockResolvedValueOnce([mockEvent]);
    mockedCreateSchoolEvent.mockResolvedValueOnce(mockEvent);

    const user = userEvent.setup();
    render(
      <MemoryRouter>
        <SchoolEventsPage />
      </MemoryRouter>,
    );

    // Click create from empty state
    await waitFor(() => {
      expect(screen.getByText("events.emptyAction")).toBeInTheDocument();
    });
    await user.click(screen.getByText("events.emptyAction"));

    // Fill form
    await user.type(
      screen.getByPlaceholderText("events.titlePlaceholder"),
      "Math Exam",
    );
    const dateInput = screen.getByLabelText("events.date");
    await user.type(dateInput, "2026-03-15");

    // Submit
    await user.click(screen.getByText("events.saveButton"));

    await waitFor(() => {
      expect(mockedCreateSchoolEvent).toHaveBeenCalledWith({
        title: "Math Exam",
        eventType: "EXAM",
        eventDate: "2026-03-15",
        subjectId: null,
        description: undefined,
      });
    });
  });

  it("should delete event with confirm", async () => {
    mockedGetSchoolEvents
      .mockResolvedValueOnce([mockEvent])
      .mockResolvedValueOnce([]);
    mockedDeleteSchoolEvent.mockResolvedValueOnce();

    const user = userEvent.setup();
    render(
      <MemoryRouter>
        <SchoolEventsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("Math Exam")).toBeInTheDocument();
    });

    await user.click(screen.getByText("common.delete"));

    expect(
      screen.getByText("events.deleteConfirmMessage"),
    ).toBeInTheDocument();

    await user.click(screen.getByText("confirm.delete"));

    await waitFor(() => {
      expect(mockedDeleteSchoolEvent).toHaveBeenCalledWith("1");
    });
  });

  it("should show event type labels", async () => {
    const events = [
      { ...mockEvent, id: "1", eventType: "EXAM" as const },
      {
        ...mockEvent,
        id: "2",
        title: "Homework",
        eventType: "ASSIGNMENT" as const,
      },
      {
        ...mockEvent,
        id: "3",
        title: "Project Due",
        eventType: "DEADLINE" as const,
      },
    ];
    mockedGetSchoolEvents.mockResolvedValueOnce(events);

    render(
      <MemoryRouter>
        <SchoolEventsPage />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("events.typeEXAM")).toBeInTheDocument();
    });
    expect(screen.getByText("events.typeASSIGNMENT")).toBeInTheDocument();
    expect(screen.getByText("events.typeDEADLINE")).toBeInTheDocument();
  });
});
