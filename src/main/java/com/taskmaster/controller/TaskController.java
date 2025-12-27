package com.taskmaster.controller;

import com.taskmaster.common.constants.ApiPaths;
import com.taskmaster.common.constants.AppConstants;
import com.taskmaster.common.dto.ApiResponse;
import com.taskmaster.common.dto.PagedResponse;
import com.taskmaster.common.enums.TaskPriority;
import com.taskmaster.common.enums.TaskStatus;
import com.taskmaster.dto.request.TaskCreateRequest;
import com.taskmaster.dto.request.TaskUpdateRequest;
import com.taskmaster.dto.response.TaskResponse;
import com.taskmaster.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Task operations
 * Supports current-user and admin operations
 */
@RestController
@RequestMapping(ApiPaths.TASKS)
@RequiredArgsConstructor
@Tag(name = "Task", description = "Task management APIs")
@Validated
public class TaskController {

    private final TaskService taskService;

    /* ========================
       Create Task (User)
       ======================== */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Create task", description = "Create a new task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskCreateRequest request) {
        TaskResponse task = taskService.createTask(request);
        return ResponseEntity.ok(ApiResponse.success("Task created successfully", task));
    }

    /* ========================
       Get Task by ID
       ======================== */
    @GetMapping(ApiPaths.TASK_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @taskSecurity.isOwner(#id)")
    @Operation(summary = "Get task by ID", description = "Get task details by task ID")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable Long id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    /* ========================
       Update Task
       ======================== */
    @PutMapping(ApiPaths.TASK_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @taskSecurity.isOwner(#id)")
    @Operation(summary = "Update task", description = "Update task details")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request) {
        TaskResponse task = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", task));
    }

    /* ========================
       Delete Task (Soft Delete)
       ======================== */
    @DeleteMapping(ApiPaths.TASK_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @taskSecurity.isOwner(#id)")
    @Operation(summary = "Delete task", description = "Soft delete a task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(
                ApiResponse.success("Task deleted successfully")
        );
    }

    /* ========================
       Get All Tasks (Admin)
       ======================== */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all tasks", description = "Get all tasks with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> getAllTasks(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<TaskResponse> tasks = taskService.getAllTasks(pageable);

        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /* ========================
       Search Tasks (Admin)
       ======================== */
    @GetMapping(ApiPaths.TASK_SEARCH)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Search tasks", description = "Search tasks by title or description")
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> searchTasks(
            @RequestParam String query,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

                Sort sort = sortDir.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();
                Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<TaskResponse> tasks = taskService.searchTasks(query, pageable);

        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /* ========================
       Filter Tasks (Admin)
       ======================== */
    @GetMapping(ApiPaths.TASK_FILTER)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Filter tasks", description = "Filter tasks by status or priority")
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> filterTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir){

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<TaskResponse> tasks = taskService.filterTasks(status, priority, pageable);

        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /* ========================
       Current User: Get My Tasks
       ======================== */
    @GetMapping(ApiPaths.TASK_ME)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Get current user's tasks", description = "Get all tasks assigned to the current user")
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> getMyTasks(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<TaskResponse> tasks = taskService.getTasksByCurrentUser(pageable);

        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /* ========================
       Current User: Search My Tasks
       ======================== */
    @GetMapping(ApiPaths.USER_ME_SEARCH)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Search my tasks", description = "Search tasks assigned to current user")
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> searchMyTasks(
            @RequestParam String query,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir)
    {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<TaskResponse> tasks = taskService.searchTasksForCurrentUser(query, pageable);

        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /* ========================
       Current User: Filter My Tasks
       ======================== */
    @GetMapping(ApiPaths.TASK_ME_FILTER)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Filter my tasks", description = "Filter tasks assigned to current user")
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> filterMyTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir)
    {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<TaskResponse> tasks = taskService.filterTasksForCurrentUser(status, priority, pageable);

        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    // ======================== Restore Task ========================
//    @PutMapping(ApiPaths.TASK_RESTORE)
//    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @taskSecurity.isOwner(#id)")
//    @Operation(summary = "Restore task", description = "Restore a previously deleted task")
//    public ResponseEntity<ApiResponse<TaskResponse>> restoreTask(@PathVariable Long id) {
//        taskService.restoreTask(id);
//        return ResponseEntity.ok(
//                ApiResponse.success("Task restored successfully")
//        );
//    }

    @PutMapping(ApiPaths.TASK_RESTORE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @taskSecurity.isOwner(#id)")
    public ResponseEntity<ApiResponse<TaskResponse>> restoreTask(@PathVariable Long id) {
        taskService.restoreTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task restored successfully"));
    }

}