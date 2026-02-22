package com.studentsbff.dto;

import java.time.Instant;
import java.util.UUID;

public record SubjectResponse(UUID id, String name, Instant createdAt, Instant updatedAt) {}
