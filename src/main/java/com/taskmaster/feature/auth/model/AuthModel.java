package com.taskmaster.feature.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple DTO for authentication requests/responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthModel {
    private String username;
    private String email;
    private String password;
}
