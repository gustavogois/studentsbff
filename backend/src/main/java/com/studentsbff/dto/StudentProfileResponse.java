package com.studentsbff.dto;

import java.time.Instant;
import java.util.UUID;

public record StudentProfileResponse(UUID id, String grade, String school, Instant createdAt) {}
