package com.taskmaster.feature.auth.repository;

import com.taskmaster.feature.auth.entity.AuthEntity;

/**
 * Placeholder repository for auth-related data.
 * Intentionally implemented as a simple class (not a Spring Data repository) to avoid
 * changing the existing persistence/workflow which relies on `UserEntity` and `UserRepository`.
 *
 * If you later want DB-backed auth storage separate from users, convert this to an interface
 * that extends JpaRepository<AuthEntity, Long> and add JPA annotations to `AuthEntity`.
 */
public class AuthRepository {

    public AuthRepository() {
        // placeholder constructor
    }

    // Placeholder method signatures for future implementation
    public AuthEntity findByUsername(String username) {
        return null;
    }

    public AuthEntity save(AuthEntity entity) {
        return entity;
    }
}
