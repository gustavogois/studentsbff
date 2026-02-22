import client from "./client";
import type { Subject, SubjectRequest } from "../types";

export async function getSubjects(): Promise<Subject[]> {
  const response = await client.get<Subject[]>("/api/subjects");
  return response.data;
}

export async function getSubject(id: string): Promise<Subject> {
  const response = await client.get<Subject>(`/api/subjects/${id}`);
  return response.data;
}

export async function createSubject(data: SubjectRequest): Promise<Subject> {
  const response = await client.post<Subject>("/api/subjects", data);
  return response.data;
}

export async function updateSubject(
  id: string,
  data: SubjectRequest
): Promise<Subject> {
  const response = await client.put<Subject>(`/api/subjects/${id}`, data);
  return response.data;
}

export async function deleteSubject(id: string): Promise<void> {
  await client.delete(`/api/subjects/${id}`);
}
