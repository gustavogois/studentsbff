-- Clear invalid free-text grade values (keep only valid enum values or NULL)
UPDATE students SET grade = NULL
  WHERE grade IS NOT NULL
  AND grade NOT IN ('GRADE_7','GRADE_8','GRADE_9','GRADE_10','GRADE_11','GRADE_12');

-- Add turma (class section) column
ALTER TABLE students ADD COLUMN turma VARCHAR(100);

-- Add check constraint to enforce valid grade enum values
ALTER TABLE students ADD CONSTRAINT chk_student_grade
  CHECK (grade IS NULL OR grade IN ('GRADE_7','GRADE_8','GRADE_9','GRADE_10','GRADE_11','GRADE_12'));
