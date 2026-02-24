package com.studentsbff.dto;

import com.studentsbff.model.EventType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SchoolEventResponse(
        UUID id,
        String title,
        EventType eventType,
        LocalDate eventDate,
        UUID subjectId,
        String subjectName,
        String description,
        Instant createdAt,
        Instant updatedAt) {}
