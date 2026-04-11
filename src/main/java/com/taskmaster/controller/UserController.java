package com.taskmaster.controller;

import com.taskmaster.common.constants.ApiPaths;
import com.taskmaster.common.constants.AppConstants;
import com.taskmaster.common.dto.ApiResponse;
import com.taskmaster.common.dto.PagedResponse;
import com.taskmaster.dto.request.UserUpdateRequest;
import com.taskmaster.dto.response.UserDetailResponse;
import com.taskmaster.dto.response.UserSummaryResponse;
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

    @GetMapping(ApiPaths.USER_ME)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Get current user", description = "Get the currently logged-in user's profile")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getCurrentUser() {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getCurrentUser())
        );
    }

    @GetMapping(ApiPaths.USER_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Get user by ID", description = "Get user details by their ID")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getUserById(id))
        );
    }

    @PutMapping(ApiPaths.USER_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Update user", description = "Update user details")
    public ResponseEntity<ApiResponse<UserDetailResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User updated successfully",
                        userService.updateUser(id, request)
                )
        );
    }

    @DeleteMapping(ApiPaths.USER_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete user", description = "Soft delete a user (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully")
        );
    }

    @PutMapping(ApiPaths.USER_RESTORE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Restore user", description = "Restore a soft-deleted user")
    public ResponseEntity<ApiResponse<Void>> restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User restored successfully")
        );
    }

    @GetMapping(ApiPaths.USER_ACTIVE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all active users", description = "Get all active users with pagination (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<UserSummaryResponse>>> getAllActiveUsers(
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

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all users", description = "Get all users with pagination (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<UserSummaryResponse>>> getAllUsers(
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

    @GetMapping(ApiPaths.USER_SEARCH)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Search users", description = "Search users by name or email (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<UserSummaryResponse>>> searchUsers(
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