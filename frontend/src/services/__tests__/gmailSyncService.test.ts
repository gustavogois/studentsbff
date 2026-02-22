import { describe, it, expect, vi, beforeEach } from "vitest";

const { mockPost } = vi.hoisted(() => ({
  mockPost: vi.fn(),
}));

vi.mock("../client", () => ({
  default: {
    post: mockPost,
  },
}));

import { syncGmail } from "../gmailSyncService";

describe("gmailSyncService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should trigger sync", async () => {
    const syncResult = {
      newEventsCount: 3,
      skippedDuplicates: 1,
      totalEmails: 10,
    };
    mockPost.mockResolvedValueOnce({ data: syncResult });

    const result = await syncGmail(7);

    expect(mockPost).toHaveBeenCalledWith("/api/gmail/sync", { daysBack: 7 });
    expect(result).toEqual(syncResult);
  });

  it("should use default daysBack when not provided", async () => {
    const syncResult = {
      newEventsCount: 0,
      skippedDuplicates: 0,
      totalEmails: 0,
    };
    mockPost.mockResolvedValueOnce({ data: syncResult });

    await syncGmail();

    expect(mockPost).toHaveBeenCalledWith("/api/gmail/sync", { daysBack: 30 });
  });
});
