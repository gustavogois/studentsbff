package com.studentsbff.dto;

import com.studentsbff.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class SchoolEventRequest {

    @NotBlank
    private String title;

    @NotNull
    private EventType eventType;

    @NotNull
    private LocalDate eventDate;

    private UUID subjectId;

    private String description;
}
