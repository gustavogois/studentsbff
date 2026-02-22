import client from "./client";
import type { User } from "../types";

export async function fetchCurrentUser(): Promise<User> {
  const response = await client.get<User>("/api/users/me");
  return response.data;
}
