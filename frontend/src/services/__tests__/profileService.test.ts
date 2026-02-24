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

import { getProfile, updateProfile, getGrades } from "../profileService";

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
      grade: "GRADE_8",
      school: "Washington Middle School",
      turma: "B",
      createdAt: "",
    };
    mockPut.mockResolvedValueOnce({ data: profile });

    const result = await updateProfile({
      grade: "GRADE_8",
      school: "Washington Middle School",
      turma: "B",
    });

    expect(mockPut).toHaveBeenCalledWith("/api/students/profile", {
      grade: "GRADE_8",
      school: "Washington Middle School",
      turma: "B",
    });
    expect(result).toEqual(profile);
  });

  it("should fetch grades", async () => {
    const grades = [
      { value: "GRADE_7", label: "7th Grade" },
      { value: "GRADE_8", label: "8th Grade" },
    ];
    mockGet.mockResolvedValueOnce({ data: grades });

    const result = await getGrades();

    expect(mockGet).toHaveBeenCalledWith("/api/students/grades");
    expect(result).toEqual(grades);
  });
});
