-- Add OAuth token columns to users table for Gmail API access
ALTER TABLE users ADD COLUMN google_access_token TEXT;
ALTER TABLE users ADD COLUMN google_refresh_token TEXT;
ALTER TABLE users ADD COLUMN google_token_expiry TIMESTAMP;

-- Create school_events table for AI-extracted events from Gmail
CREATE TABLE school_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES students(id),
    title VARCHAR(500) NOT NULL,
    event_type VARCHAR(20) NOT NULL CHECK (event_type IN ('EXAM', 'ASSIGNMENT', 'DEADLINE', 'OTHER')),
    subject_id UUID REFERENCES subjects(id) ON DELETE SET NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    source VARCHAR(20) NOT NULL CHECK (source IN ('GMAIL', 'MANUAL')),
    source_email_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_school_events_student_id ON school_events(student_id);
CREATE INDEX idx_school_events_source_email_id ON school_events(source_email_id);
