import { describe, it, expect, vi, beforeEach } from "vitest";

const { mockGet, mockPost, mockDelete } = vi.hoisted(() => ({
  mockGet: vi.fn(),
  mockPost: vi.fn(),
  mockDelete: vi.fn(),
}));

vi.mock("../client", () => ({
  default: {
    get: mockGet,
    post: mockPost,
    put: vi.fn(),
    delete: mockDelete,
  },
}));

import { getSubjects, createSubject, deleteSubject } from "../subjectService";

describe("subjectService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should fetch subjects", async () => {
    const subjects = [
      { id: "1", name: "Math", createdAt: "", updatedAt: "" },
    ];
    mockGet.mockResolvedValueOnce({ data: subjects });

    const result = await getSubjects();

    expect(mockGet).toHaveBeenCalledWith("/api/subjects");
    expect(result).toEqual(subjects);
  });

  it("should create subject", async () => {
    const subject = { id: "1", name: "Math", createdAt: "", updatedAt: "" };
    mockPost.mockResolvedValueOnce({ data: subject });

    const result = await createSubject({ name: "Math" });

    expect(mockPost).toHaveBeenCalledWith("/api/subjects", {
      name: "Math",
    });
    expect(result).toEqual(subject);
  });

  it("should delete subject", async () => {
    mockDelete.mockResolvedValueOnce({});

    await deleteSubject("1");

    expect(mockDelete).toHaveBeenCalledWith("/api/subjects/1");
  });
});
