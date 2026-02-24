import client from "./client";
import type { StudentProfile, StudentProfileRequest } from "../types";

export async function getProfile(): Promise<StudentProfile> {
  const response = await client.get<StudentProfile>("/api/students/profile");
  return response.data;
}

export async function updateProfile(
  data: StudentProfileRequest
): Promise<StudentProfile> {
  const response = await client.put<StudentProfile>(
    "/api/students/profile",
    data
  );
  return response.data;
}
