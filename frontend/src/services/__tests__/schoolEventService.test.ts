import { describe, it, expect, vi, beforeEach } from "vitest";

const { mockGet, mockDelete } = vi.hoisted(() => ({
  mockGet: vi.fn(),
  mockDelete: vi.fn(),
}));

vi.mock("../client", () => ({
  default: {
    get: mockGet,
    delete: mockDelete,
  },
}));

import { getSchoolEvents, deleteSchoolEvent } from "../schoolEventService";

describe("schoolEventService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should fetch events", async () => {
    const events = [
      {
        id: "1",
        title: "Math Exam",
        eventType: "EXAM",
        subjectId: null,
        subjectName: null,
        description: "Exam on chapters 5-7",
        eventDate: "2025-03-14T10:00:00Z",
        source: "GMAIL",
        createdAt: "",
      },
    ];
    mockGet.mockResolvedValueOnce({ data: events });

    const result = await getSchoolEvents();

    expect(mockGet).toHaveBeenCalledWith("/api/school-events");
    expect(result).toEqual(events);
  });

  it("should delete event", async () => {
    mockDelete.mockResolvedValueOnce({});

    await deleteSchoolEvent("1");

    expect(mockDelete).toHaveBeenCalledWith("/api/school-events/1");
  });
});
