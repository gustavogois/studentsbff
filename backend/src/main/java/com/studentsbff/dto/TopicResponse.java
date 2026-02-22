package com.studentsbff.dto;

import java.time.Instant;
import java.util.UUID;

public record TopicResponse(
        UUID id, String name, int difficulty, Instant createdAt, Instant updatedAt) {}
