export interface User {
  id: string;
  name: string;
  email: string;
  role: "STUDENT" | "PARENT";
  avatarUrl: string | null;
}

export interface Subject {
  id: string;
  name: string;
  createdAt: string;
  updatedAt: string;
}

export interface Topic {
  id: string;
  name: string;
  difficulty: number;
  createdAt: string;
  updatedAt: string;
}

export interface SubjectRequest {
  name: string;
}

export interface TopicRequest {
  name: string;
  difficulty?: number;
}

export interface StudentProfile {
  id: string;
  grade: string | null;
  school: string | null;
  turma: string | null;
  createdAt: string;
}

export interface StudentProfileRequest {
  grade: string | null;
  school: string;
  turma: string;
}

export interface GradeOption {
  value: string;
  label: string;
}

export type EventType = "EXAM" | "ASSIGNMENT" | "DEADLINE";

export interface SchoolEvent {
  id: string;
  title: string;
  eventType: EventType;
  eventDate: string;
  subjectId: string | null;
  subjectName: string | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface SchoolEventRequest {
  title: string;
  eventType: EventType;
  eventDate: string;
  subjectId?: string | null;
  description?: string;
}
