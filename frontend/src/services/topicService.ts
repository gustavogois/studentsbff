import client from "./client";
import type { Topic, TopicRequest } from "../types";

export async function getTopics(subjectId: string): Promise<Topic[]> {
  const response = await client.get<Topic[]>(
    `/api/subjects/${subjectId}/topics`
  );
  return response.data;
}

export async function createTopic(
  subjectId: string,
  data: TopicRequest
): Promise<Topic> {
  const response = await client.post<Topic>(
    `/api/subjects/${subjectId}/topics`,
    data
  );
  return response.data;
}

export async function updateTopic(
  subjectId: string,
  id: string,
  data: TopicRequest
): Promise<Topic> {
  const response = await client.put<Topic>(
    `/api/subjects/${subjectId}/topics/${id}`,
    data
  );
  return response.data;
}

export async function deleteTopic(
  subjectId: string,
  id: string
): Promise<void> {
  await client.delete(`/api/subjects/${subjectId}/topics/${id}`);
}
