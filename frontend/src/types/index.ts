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
