import { describe, it, expect, vi, beforeEach } from "vitest";
import { renderHook, act, waitFor } from "@testing-library/react";
import type { ReactNode } from "react";
import { AuthProvider, useAuth } from "../AuthContext";

vi.mock("../../services/authService", () => ({
  fetchCurrentUser: vi.fn(),
}));

import { fetchCurrentUser } from "../../services/authService";
const mockedFetch = vi.mocked(fetchCurrentUser);

function wrapper({ children }: { children: ReactNode }) {
  return <AuthProvider>{children}</AuthProvider>;
}

describe("useAuth", () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it("should return null user when no token", async () => {
    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    expect(result.current.user).toBeNull();
  });

  it("should parse user from token", async () => {
    const mockUser = {
      id: "1",
      name: "Test User",
      email: "test@example.com",
      role: "STUDENT" as const,
      avatarUrl: null,
    };
    localStorage.setItem("token", "valid-token");
    mockedFetch.mockResolvedValueOnce(mockUser);

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    expect(result.current.user).toEqual(mockUser);
  });

  it("should clear token on logout", async () => {
    const mockUser = {
      id: "1",
      name: "Test User",
      email: "test@example.com",
      role: "STUDENT" as const,
      avatarUrl: null,
    };
    localStorage.setItem("token", "valid-token");
    mockedFetch.mockResolvedValueOnce(mockUser);

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.user).toEqual(mockUser);
    });

    act(() => {
      result.current.logout();
    });

    expect(result.current.user).toBeNull();
    expect(localStorage.getItem("token")).toBeNull();
  });
});
