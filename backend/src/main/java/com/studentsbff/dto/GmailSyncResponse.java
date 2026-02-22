package com.studentsbff.dto;

public record GmailSyncResponse(int newEventsCount, int skippedDuplicates, int totalEmails) {}
