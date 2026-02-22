import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import SchoolEventsPage from "../SchoolEventsPage";

const mockGetSchoolEvents = vi.fn();
const mockDeleteSchoolEvent = vi.fn();
const mockSyncGmail = vi.fn();

vi.mock("../../services/schoolEventService", () => ({
  getSchoolEvents: () => mockGetSchoolEvents(),
  deleteSchoolEvent: (id: string) => mockDeleteSchoolEvent(id),
}));

vi.mock("../../services/gmailSyncService", () => ({
  syncGmail: (daysBack?: number) => mockSyncGmail(daysBack),
}));

describe("SchoolEventsPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    window.confirm = vi.fn(() => true);
  });

  it("should show sync button", async () => {
    mockGetSchoolEvents.mockResolvedValueOnce([]);

    render(<SchoolEventsPage />);

    await waitFor(() => {
      expect(screen.getByText("Sync Gmail")).toBeInTheDocument();
    });
  });

  it("should list events", async () => {
    mockGetSchoolEvents.mockResolvedValueOnce([
      {
        id: "1",
        title: "Math Exam",
        eventType: "EXAM",
        subjectId: null,
        subjectName: "Math",
        description: "Exam covering chapters 5-7",
        eventDate: "2025-03-14T10:00:00Z",
        source: "GMAIL",
        createdAt: "2025-03-10T10:00:00Z",
      },
      {
        id: "2",
        title: "Science Report Due",
        eventType: "ASSIGNMENT",
        subjectId: null,
        subjectName: null,
        description: "Submit by Friday",
        eventDate: "2025-03-15T23:59:00Z",
        source: "GMAIL",
        createdAt: "2025-03-10T10:00:00Z",
      },
    ]);

    render(<SchoolEventsPage />);

    await waitFor(() => {
      expect(screen.getByText("Math Exam")).toBeInTheDocument();
      expect(screen.getByText("Science Report Due")).toBeInTheDocument();
      expect(screen.getByText("EXAM")).toBeInTheDocument();
      expect(screen.getByText("ASSIGNMENT")).toBeInTheDocument();
    });
  });

  it("should show empty state", async () => {
    mockGetSchoolEvents.mockResolvedValueOnce([]);

    render(<SchoolEventsPage />);

    await waitFor(() => {
      expect(screen.getByText(/No events yet/)).toBeInTheDocument();
    });
  });

  it("should delete event", async () => {
    mockGetSchoolEvents
      .mockResolvedValueOnce([
        {
          id: "1",
          title: "Math Exam",
          eventType: "EXAM",
          subjectId: null,
          subjectName: null,
          description: "",
          eventDate: "2025-03-14T10:00:00Z",
          source: "GMAIL",
          createdAt: "",
        },
      ])
      .mockResolvedValueOnce([]);
    mockDeleteSchoolEvent.mockResolvedValueOnce(undefined);

    render(<SchoolEventsPage />);

    await waitFor(() => {
      expect(screen.getByText("Math Exam")).toBeInTheDocument();
    });

    await userEvent.click(screen.getByText("Delete"));

    await waitFor(() => {
      expect(mockDeleteSchoolEvent).toHaveBeenCalledWith("1");
    });
  });

  it("should show sync results", async () => {
    mockGetSchoolEvents.mockResolvedValue([]);
    mockSyncGmail.mockResolvedValueOnce({
      newEventsCount: 3,
      skippedDuplicates: 1,
      totalEmails: 10,
    });

    render(<SchoolEventsPage />);

    await waitFor(() => {
      expect(screen.getByText("Sync Gmail")).toBeInTheDocument();
    });

    await userEvent.click(screen.getByText("Sync Gmail"));

    await waitFor(() => {
      expect(screen.getByText(/Found 3 new events/)).toBeInTheDocument();
    });
  });

  it("should show loading during sync", async () => {
    mockGetSchoolEvents.mockResolvedValue([]);
    mockSyncGmail.mockReturnValue(new Promise(() => {}));

    render(<SchoolEventsPage />);

    await waitFor(() => {
      expect(screen.getByText("Sync Gmail")).toBeInTheDocument();
    });

    await userEvent.click(screen.getByText("Sync Gmail"));

    expect(screen.getByText("Syncing...")).toBeInTheDocument();
  });
});
