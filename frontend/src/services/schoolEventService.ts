import client from "./client";
import type { SchoolEvent, SchoolEventRequest } from "../types";

export async function getSchoolEvents(
  from?: string,
  to?: string,
): Promise<SchoolEvent[]> {
  const params = new URLSearchParams();
  if (from) params.set("from", from);
  if (to) params.set("to", to);
  const query = params.toString();
  const url = `/api/school-events${query ? `?${query}` : ""}`;
  const response = await client.get<SchoolEvent[]>(url);
  return response.data;
}

export async function getSchoolEvent(id: string): Promise<SchoolEvent> {
  const response = await client.get<SchoolEvent>(`/api/school-events/${id}`);
  return response.data;
}

export async function createSchoolEvent(
  data: SchoolEventRequest,
): Promise<SchoolEvent> {
  const response = await client.post<SchoolEvent>("/api/school-events", data);
  return response.data;
}

export async function updateSchoolEvent(
  id: string,
  data: SchoolEventRequest,
): Promise<SchoolEvent> {
  const response = await client.put<SchoolEvent>(
    `/api/school-events/${id}`,
    data,
  );
  return response.data;
}

export async function deleteSchoolEvent(id: string): Promise<void> {
  await client.delete(`/api/school-events/${id}`);
}
