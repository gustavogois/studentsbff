import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";

describe("API Client", () => {
  beforeEach(() => {
    localStorage.clear();
    vi.resetModules();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("should attach Authorization header when token exists", async () => {
    localStorage.setItem("token", "test-jwt-token");
    const { default: client } = await import("../client");

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const requestInterceptor = (client.interceptors.request as any)
      .handlers[0];
    const config = { headers: {} as Record<string, string> };
    const result = requestInterceptor.fulfilled(config);

    expect(result.headers.Authorization).toBe("Bearer test-jwt-token");
  });

  it("should redirect on 401 response", async () => {
    const { default: client } = await import("../client");

    const originalLocation = window.location.href;
    Object.defineProperty(window, "location", {
      value: { href: originalLocation },
      writable: true,
    });

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const responseInterceptor = (client.interceptors.response as any)
      .handlers[0];
    const error = { response: { status: 401 } };

    await expect(responseInterceptor.rejected(error)).rejects.toEqual(error);
    expect(window.location.href).toBe("/login");
  });
});
