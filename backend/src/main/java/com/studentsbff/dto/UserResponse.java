package com.studentsbff.dto;

import com.studentsbff.model.Role;
import java.util.UUID;

public record UserResponse(UUID id, String name, String email, Role role, String avatarUrl) {}
