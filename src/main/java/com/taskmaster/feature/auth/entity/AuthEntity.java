package com.taskmaster.feature.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Minimal POJO representing authentication-related data.
 * Kept as a simple class (no JPA annotations) so it does not affect the existing user workflow.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthEntity {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Instant createdAt;
}
