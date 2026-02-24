package com.studentsbff.dto;

import com.studentsbff.model.Grade;
import java.time.Instant;
import java.util.UUID;

public record StudentProfileResponse(
        UUID id, Grade grade, String school, String turma, Instant createdAt) {}
