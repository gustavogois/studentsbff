package com.studentsbff.dto;

import java.time.Instant;
import java.util.UUID;

public record SchoolEventResponse(
        UUID id,
        String title,
        String eventType,
        UUID subjectId,
        String subjectName,
        String description,
        Instant eventDate,
        String source,
        Instant createdAt) {}
