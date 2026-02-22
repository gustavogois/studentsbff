import client from "./client";
import type { SchoolEvent } from "../types";

export async function getSchoolEvents(): Promise<SchoolEvent[]> {
  const response = await client.get<SchoolEvent[]>("/api/school-events");
  return response.data;
}

export async function deleteSchoolEvent(id: string): Promise<void> {
  await client.delete(`/api/school-events/${id}`);
}
