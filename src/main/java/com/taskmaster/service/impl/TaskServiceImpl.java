package com.taskmaster.service.impl;

import com.taskmaster.common.dto.PagedResponse;
import com.taskmaster.common.enums.TaskPriority;
import com.taskmaster.common.enums.TaskStatus;
import com.taskmaster.common.exception.custom.ResourceNotFoundException;
import com.taskmaster.config.security.SecurityService;
import com.taskmaster.dto.request.TaskCreateRequest;
import com.taskmaster.dto.request.TaskUpdateRequest;
import com.taskmaster.dto.response.TaskResponse;
import com.taskmaster.entity.TaskEntity;
import com.taskmaster.entity.UserEntity;
import com.taskmaster.mapper.TaskMapper;
import com.taskmaster.repository.TaskRepository;
import com.taskmaster.repository.UserRepository;
import com.taskmaster.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final SecurityService securityService;

    /* =======================
       Helpers
       ======================= */

    private TaskEntity findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    private UserEntity getCurrentUser() {
        Long userId = securityService.getCurrentUserId();
        return userRepository.getReferenceById(userId);
    }

    /* =======================
       Read
       ======================= */

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        return taskMapper.toResponse(findTaskById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> getAllTasks(Pageable pageable) {
        Page<TaskEntity> page = taskRepository.findAll(pageable);
        return PagedResponse.of(page.map(taskMapper::toResponse));
    }

    /* =======================
       Create
       ======================= */

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        TaskEntity task = taskMapper.toEntity(request);

        UserEntity currentUser = getCurrentUser();
        UserEntity assignee = userRepository.getReferenceById(request.getAssigneeId());

        task.setAssignee(assignee);
        task.setCreatedBy(currentUser);
        task.setUpdatedBy(currentUser);

        TaskEntity saved = taskRepository.save(task);
        return taskMapper.toResponse(saved);
    }

    /* =======================
       Update
       ======================= */

    @Override
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        TaskEntity task = findTaskById(id);

        taskMapper.updateEntity(request, task);
        task.setUpdatedBy(getCurrentUser());

        TaskEntity updated = taskRepository.save(task);
        return taskMapper.toResponse(updated);
    }

    /* =======================
       Delete / Restore
       ======================= */

    @Override
    public void deleteTask(Long id) {
        TaskEntity task = findTaskById(id);
        task.softDelete(securityService.getCurrentUserId());
        taskRepository.save(task);
    }

    @Override
    public void restoreTask(Long id) {
        TaskEntity task = findTaskById(id);
        task.restore();
        taskRepository.save(task);
    }

    /* =======================
       Search / Filter (Admin)
       ======================= */

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> searchTasks(String term, Pageable pageable) {
        Page<TaskEntity> page = taskRepository.searchTasks(term, pageable);
        return PagedResponse.of(page.map(taskMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> filterTasks(
            TaskStatus status,
            TaskPriority priority,
            Pageable pageable
    ) {
        Page<TaskEntity> page = taskRepository.filterTasks(status, priority, pageable);
        return PagedResponse.of(page.map(taskMapper::toResponse));
    }

    /* =======================
       Current User
       ======================= */

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> getTasksByCurrentUser(Pageable pageable) {
        Long userId = securityService.getCurrentUserId();
        Page<TaskEntity> page = taskRepository.findByAssigneeId(userId, pageable);
        return PagedResponse.of(page.map(taskMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> searchTasksForCurrentUser(String term, Pageable pageable) {
        Long userId = securityService.getCurrentUserId();
        Page<TaskEntity> page = taskRepository.searchTasksForUser(term, userId, pageable);
        return PagedResponse.of(page.map(taskMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> filterTasksForCurrentUser(
            TaskStatus status,
            TaskPriority priority,
            Pageable pageable
    ) {
        Long userId = securityService.getCurrentUserId();
        Page<TaskEntity> page =
                taskRepository.filterTasksForUser(status, priority, userId, pageable);

        return PagedResponse.of(page.map(taskMapper::toResponse));
    }
}
