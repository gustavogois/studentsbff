package com.studentsbff.dto;

public record ParsedSchoolEvent(
        String title,
        String eventType,
        String eventDate,
        String description,
        String relatedSubjectName,
        String sourceEmailId) {}
