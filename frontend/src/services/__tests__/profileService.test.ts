import { describe, it, expect, vi, beforeEach } from "vitest";

const { mockGet, mockPut } = vi.hoisted(() => ({
  mockGet: vi.fn(),
  mockPut: vi.fn(),
}));

vi.mock("../client", () => ({
  default: {
    get: mockGet,
    put: mockPut,
  },
}));

import { getProfile, updateProfile } from "../profileService";

describe("profileService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should fetch profile", async () => {
    const profile = {
      id: "1",
      grade: "7th",
      school: "Lincoln Middle School",
      createdAt: "",
    };
    mockGet.mockResolvedValueOnce({ data: profile });

    const result = await getProfile();

    expect(mockGet).toHaveBeenCalledWith("/api/students/profile");
    expect(result).toEqual(profile);
  });

  it("should update profile", async () => {
    const profile = {
      id: "1",
      grade: "8th",
      school: "Washington Middle School",
      createdAt: "",
    };
    mockPut.mockResolvedValueOnce({ data: profile });

    const result = await updateProfile({
      grade: "8th",
      school: "Washington Middle School",
    });

    expect(mockPut).toHaveBeenCalledWith("/api/students/profile", {
      grade: "8th",
      school: "Washington Middle School",
    });
    expect(result).toEqual(profile);
  });
});
