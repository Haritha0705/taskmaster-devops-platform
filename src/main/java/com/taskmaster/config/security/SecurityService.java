package com.taskmaster.config.security;

import com.taskmaster.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Provides details about the currently logged-in user
 */
@Service
public class SecurityService {

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated user");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserEntity user) return user.getId();
        throw new RuntimeException("Principal is not UserEntity");
    }
}

