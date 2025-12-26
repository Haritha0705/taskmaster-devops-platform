package com.taskmaster.controller;

import com.taskmaster.common.constants.ApiPaths;
import com.taskmaster.common.constants.AppConstants;
import com.taskmaster.common.dto.ApiResponse;
import com.taskmaster.common.dto.PagedResponse;
import com.taskmaster.dto.request.UserUpdateRequest;
import com.taskmaster.dto.response.UserResponse;
import com.taskmaster.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User operations
 */
@RestController
@RequestMapping(ApiPaths.USERS)
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
@Validated
public class UserController {

    private final UserService userService;

    /**
     * Get current logged-in user profile
     */
    @GetMapping(ApiPaths.USER_ME)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Get current user", description = "Get the currently logged-in user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getCurrentUser())
        );
    }

    /**
     * Get user by ID
     */
    @GetMapping(ApiPaths.USER_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Get user by ID", description = "Get user details by their ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getUserById(id))
        );
    }

    /**
     * Update user
     */
    @PutMapping(ApiPaths.USER_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Update user", description = "Update user details")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User updated successfully",
                        userService.updateUser(id, request)
                )
        );
    }

    /**
     * Delete user (Admin only)
     */
    @DeleteMapping(ApiPaths.USER_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete user", description = "Soft delete a user (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully")
        );
    }

    /**
     * Restore soft-deleted user (Admin only)
     */
    @PutMapping(ApiPaths.USER_RESTORE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Restore user", description = "Restore a soft-deleted user")
    public ResponseEntity<ApiResponse<Void>> restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User restored successfully")
        );
    }

    /**
     * Get all Active users with pagination (Admin only)
     */
    @GetMapping(ApiPaths.USER_ACTIVE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all users", description = "Get all users with pagination (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllActiveUsers(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER)
            @Min(0) int page,

            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE)
            @Positive int size,

            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY)
            String sortBy,

            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION)
            String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                ApiResponse.success(userService.getAllActiveUsers(pageable))
        );
    }

    /**
     * Get all users with pagination (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all users", description = "Get all users with pagination (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER)
            @Min(0) int page,

            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE)
            @Positive int size,

            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY)
            String sortBy,

            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION)
            String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                ApiResponse.success(userService.getAllUsers(pageable))
        );
    }

    /**
     * Search users (Admin only)
     */
    @GetMapping(ApiPaths.USER_SEARCH)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Search users", description = "Search users by name or email (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> searchUsers(
            @RequestParam String query,

            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER)
            @Min(0) int page,

            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE)
            @Positive int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                ApiResponse.success(userService.searchUsers(query, pageable))
        );
    }
}
