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
  createdAt: string;
}

export interface StudentProfileRequest {
  grade: string;
  school: string;
}

export interface SchoolEvent {
  id: string;
  title: string;
  eventType: "EXAM" | "ASSIGNMENT" | "DEADLINE" | "OTHER";
  subjectId: string | null;
  subjectName: string | null;
  description: string;
  eventDate: string;
  source: "GMAIL" | "MANUAL";
  createdAt: string;
}

export interface GmailSyncResult {
  newEventsCount: number;
  skippedDuplicates: number;
  totalEmails: number;
}

export interface GmailSyncRequest {
  daysBack?: number;
}
