# StudentsBFF — Data Model

## Entity Relationship Diagram

```mermaid
erDiagram
    USERS {
        uuid id PK
        varchar name
        varchar email UK
        varchar password
        varchar role "STUDENT | PARENT"
        varchar avatar_url
        timestamp created_at
        timestamp updated_at
    }

    STUDENTS {
        uuid id PK
        uuid user_id FK UK
        varchar grade
        varchar school
        timestamp created_at
    }

    PARENT_STUDENTS {
        uuid parent_id PK,FK
        uuid student_id PK,FK
        timestamp created_at
    }

    SUBJECTS {
        uuid id PK
        varchar name
        uuid student_id FK
        timestamp created_at
        timestamp updated_at
    }

    TOPICS {
        uuid id PK
        varchar name
        uuid subject_id FK
        integer difficulty "1-5"
        timestamp created_at
        timestamp updated_at
    }

    USERS ||--o| STUDENTS : "has profile"
    USERS ||--o{ PARENT_STUDENTS : "parent links"
    STUDENTS ||--o{ PARENT_STUDENTS : "linked to parents"
    STUDENTS ||--o{ SUBJECTS : "studies"
    SUBJECTS ||--o{ TOPICS : "contains"
```

## Entity Details

### users

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK, default gen_random_uuid() | Unique identifier |
| name | VARCHAR(255) | NOT NULL | Display name |
| email | VARCHAR(255) | NOT NULL, UNIQUE | Login email |
| password | VARCHAR(255) | nullable | BCrypt hash (null for OAuth-only users) |
| role | VARCHAR(20) | NOT NULL, CHECK (STUDENT, PARENT) | User role |
| avatar_url | VARCHAR(500) | nullable | Profile picture URL |
| created_at | TIMESTAMP | NOT NULL, default now() | Record creation |
| updated_at | TIMESTAMP | NOT NULL, default now() | Last update |

### students

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK, default gen_random_uuid() | Unique identifier |
| user_id | UUID | FK → users(id), NOT NULL, UNIQUE | One-to-one with users |
| grade | VARCHAR(20) | NOT NULL | School grade (e.g., "6th", "7th") |
| school | VARCHAR(255) | nullable | School name |
| created_at | TIMESTAMP | NOT NULL, default now() | Record creation |

### parent_students

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| parent_id | UUID | PK, FK → users(id) | Parent user |
| student_id | UUID | PK, FK → students(id) | Linked student |
| created_at | TIMESTAMP | NOT NULL, default now() | Link creation |

### subjects

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK, default gen_random_uuid() | Unique identifier |
| name | VARCHAR(255) | NOT NULL | Subject name (e.g., "Mathematics") |
| student_id | UUID | FK → students(id), NOT NULL | Owner student |
| created_at | TIMESTAMP | NOT NULL, default now() | Record creation |
| updated_at | TIMESTAMP | NOT NULL, default now() | Last update |

### topics

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK, default gen_random_uuid() | Unique identifier |
| name | VARCHAR(255) | NOT NULL | Topic name (e.g., "Fractions") |
| subject_id | UUID | FK → subjects(id) ON DELETE CASCADE, NOT NULL | Parent subject |
| difficulty | INTEGER | NOT NULL, default 3, CHECK (1-5) | Difficulty rating |
| created_at | TIMESTAMP | NOT NULL, default now() | Record creation |
| updated_at | TIMESTAMP | NOT NULL, default now() | Last update |

## Flyway Migrations

| Version | File | Description |
|---------|------|-------------|
| V1 | `V1__initial_schema.sql` | Creates all 5 tables |

## API Response Conventions

- Controllers never return JPA entities directly — always use response DTOs (Java Records)
- Entity → DTO mapping via MapStruct mappers
- Services return domain entities; controllers handle mapping
- All IDs are UUIDs
- Timestamps returned as ISO 8601 strings
