import { describe, it, expect, vi, beforeEach } from "vitest";

const { mockGet, mockPost, mockPut, mockDelete } = vi.hoisted(() => ({
  mockGet: vi.fn(),
  mockPost: vi.fn(),
  mockPut: vi.fn(),
  mockDelete: vi.fn(),
}));

vi.mock("../client", () => ({
  default: {
    get: mockGet,
    post: mockPost,
    put: mockPut,
    delete: mockDelete,
  },
}));

import {
  getSchoolEvents,
  getSchoolEvent,
  createSchoolEvent,
  updateSchoolEvent,
  deleteSchoolEvent,
} from "../schoolEventService";

describe("schoolEventService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should fetch events without date range", async () => {
    const events = [
      {
        id: "1",
        title: "Math Exam",
        eventType: "EXAM",
        eventDate: "2026-03-15",
      },
    ];
    mockGet.mockResolvedValueOnce({ data: events });

    const result = await getSchoolEvents();

    expect(mockGet).toHaveBeenCalledWith("/api/school-events");
    expect(result).toEqual(events);
  });

  it("should fetch events with date range", async () => {
    const events = [
      {
        id: "1",
        title: "Math Exam",
        eventType: "EXAM",
        eventDate: "2026-03-15",
      },
    ];
    mockGet.mockResolvedValueOnce({ data: events });

    const result = await getSchoolEvents("2026-03-01", "2026-03-31");

    expect(mockGet).toHaveBeenCalledWith(
      "/api/school-events?from=2026-03-01&to=2026-03-31",
    );
    expect(result).toEqual(events);
  });

  it("should create event", async () => {
    const request = {
      title: "Math Exam",
      eventType: "EXAM" as const,
      eventDate: "2026-03-15",
    };
    const response = { id: "1", ...request };
    mockPost.mockResolvedValueOnce({ data: response });

    const result = await createSchoolEvent(request);

    expect(mockPost).toHaveBeenCalledWith("/api/school-events", request);
    expect(result).toEqual(response);
  });

  it("should update event", async () => {
    const request = {
      title: "Updated Exam",
      eventType: "ASSIGNMENT" as const,
      eventDate: "2026-04-01",
    };
    const response = { id: "1", ...request };
    mockPut.mockResolvedValueOnce({ data: response });

    const result = await updateSchoolEvent("1", request);

    expect(mockPut).toHaveBeenCalledWith("/api/school-events/1", request);
    expect(result).toEqual(response);
  });

  it("should delete event", async () => {
    mockDelete.mockResolvedValueOnce({});

    await deleteSchoolEvent("1");

    expect(mockDelete).toHaveBeenCalledWith("/api/school-events/1");
  });
});
