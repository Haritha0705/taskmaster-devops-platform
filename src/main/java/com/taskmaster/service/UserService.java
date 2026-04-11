package com.taskmaster.service;

import com.taskmaster.common.dto.PagedResponse;
import com.taskmaster.dto.request.UserUpdateRequest;
import com.taskmaster.dto.response.UserDetailResponse;
import com.taskmaster.dto.response.UserSummaryResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for User operations
 */
public interface UserService {

    /**
     * Get user by ID
     */
    UserDetailResponse getUserById(Long id);

    /**
     * Get user by email
     */
    UserDetailResponse getUserByEmail(String email);

    /**
     * Update existing user
     */
    UserDetailResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * Delete user (soft delete)
     */
    void deleteUser(Long id);

    void restoreUser(Long id);

    /**
     * Get all users with pagination
     */
    PagedResponse<UserSummaryResponse> getAllUsers(Pageable pageable);

    PagedResponse<UserSummaryResponse> getAllActiveUsers(Pageable pageable);

    /**
     * Search users
     */
    PagedResponse<UserSummaryResponse> searchUsers(String searchTerm, Pageable pageable);

    /**
     * Get current logged-in user
     */
    UserDetailResponse getCurrentUser();
}