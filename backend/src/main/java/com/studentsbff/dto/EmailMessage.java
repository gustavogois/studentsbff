package com.studentsbff.dto;

import java.time.Instant;

public record EmailMessage(
        String messageId, String from, String subject, String body, Instant receivedAt) {}
