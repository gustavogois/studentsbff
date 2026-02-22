import client from "./client";
import type { GmailSyncResult } from "../types";

export async function syncGmail(daysBack?: number): Promise<GmailSyncResult> {
  const response = await client.post<GmailSyncResult>("/api/gmail/sync", {
    daysBack: daysBack ?? 30,
  });
  return response.data;
}
