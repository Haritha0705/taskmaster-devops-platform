package com.taskmaster.service;

import com.taskmaster.common.dto.PagedResponse;
import com.taskmaster.common.enums.TaskPriority;
import com.taskmaster.common.enums.TaskStatus;
import com.taskmaster.dto.request.TaskCreateRequest;
import com.taskmaster.dto.request.TaskUpdateRequest;
import com.taskmaster.dto.response.TaskResponse;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponse getTaskById(Long id);

    TaskResponse createTask(TaskCreateRequest request);

    TaskResponse updateTask(Long id, TaskUpdateRequest request);

    void deleteTask(Long id);

    void restoreTask(Long id);

    PagedResponse<TaskResponse> getAllTasks(Pageable pageable);

    PagedResponse<TaskResponse> searchTasks(String searchTerm, Pageable pageable);

    PagedResponse<TaskResponse> filterTasks(TaskStatus status, TaskPriority priority, Pageable pageable);

    PagedResponse<TaskResponse> getTasksByCurrentUser(Pageable pageable);

    PagedResponse<TaskResponse> searchTasksForCurrentUser(String searchTerm, Pageable pageable);

    PagedResponse<TaskResponse> filterTasksForCurrentUser(TaskStatus status, TaskPriority priority, Pageable pageable);
}
