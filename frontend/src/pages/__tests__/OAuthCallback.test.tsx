import { describe, it, expect, vi, beforeEach } from "vitest";
import { render } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

const mockLogin = vi.fn().mockResolvedValue(undefined);
const mockNavigate = vi.fn();

vi.mock("../../contexts/AuthContext", () => ({
  useAuth: () => ({
    login: mockLogin,
  }),
}));

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

import OAuthCallback from "../OAuthCallback";

describe("OAuthCallback", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should extract token from URL and login", async () => {
    render(
      <MemoryRouter initialEntries={["/oauth/callback?token=abc123"]}>
        <OAuthCallback />
      </MemoryRouter>
    );

    expect(mockLogin).toHaveBeenCalledWith("abc123");
  });
});
