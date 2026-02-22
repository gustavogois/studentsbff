import { describe, it, expect, vi, beforeEach } from "vitest";

const { mockGet, mockPost } = vi.hoisted(() => ({
  mockGet: vi.fn(),
  mockPost: vi.fn(),
}));

vi.mock("../client", () => ({
  default: {
    get: mockGet,
    post: mockPost,
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import { getTopics, createTopic } from "../topicService";

describe("topicService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should fetch topics", async () => {
    const topics = [
      {
        id: "1",
        name: "Fractions",
        difficulty: 3,
        createdAt: "",
        updatedAt: "",
      },
    ];
    mockGet.mockResolvedValueOnce({ data: topics });

    const result = await getTopics("sub-1");

    expect(mockGet).toHaveBeenCalledWith("/api/subjects/sub-1/topics");
    expect(result).toEqual(topics);
  });

  it("should create topic", async () => {
    const topic = {
      id: "1",
      name: "Fractions",
      difficulty: 4,
      createdAt: "",
      updatedAt: "",
    };
    mockPost.mockResolvedValueOnce({ data: topic });

    const result = await createTopic("sub-1", {
      name: "Fractions",
      difficulty: 4,
    });

    expect(mockPost).toHaveBeenCalledWith("/api/subjects/sub-1/topics", {
      name: "Fractions",
      difficulty: 4,
    });
    expect(result).toEqual(topic);
  });
});
