package com.taskmaster.service;

import com.taskmaster.common.dto.PagedResponse;
import com.taskmaster.dto.request.UserCreateRequest;
import com.taskmaster.dto.request.UserUpdateRequest;
import com.taskmaster.dto.response.UserResponse;
import com.taskmaster.entity.UserEntity;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for User operations
 */
public interface UserService {

    /**
     * Get user by ID
     */
    UserResponse getUserById(Long id);

    /**
     * Get user by email
     */
    UserResponse getUserByEmail(String email);

    /**
     * Get user entity by email (internal use)
     */
    UserEntity getUserEntityByEmail(String email);

    /**
     * Create new user
     */
    UserResponse createUser(UserCreateRequest request);

    /**
     * Update existing user
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * Delete user (soft delete)
     */
    void deleteUser(Long id);

    void restoreUser(Long id);

    /**
     * Get all users with pagination
     */
    PagedResponse<UserResponse> getAllUsers(Pageable pageable);

    PagedResponse<UserResponse> getAllActiveUsers(Pageable pageable);

    /**
     * Search users
     */
    PagedResponse<UserResponse> searchUsers(String searchTerm, Pageable pageable);

    /**
     * Check if email exists
     */
    boolean emailExists(String email);

    /**
     * Get current logged-in user
     */
    UserResponse getCurrentUser();
}
