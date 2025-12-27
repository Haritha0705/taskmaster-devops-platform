package com.taskmaster.config.security;

import com.taskmaster.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handles security checks for Task ownership
 */
@Component("taskSecurity")
@RequiredArgsConstructor
public class TaskSecurity {

    private final TaskRepository taskRepository;
    private final SecurityService securityService;

    public boolean isOwner(Long taskId) {
        Long currentUserId = securityService.getCurrentUserId();

        return taskRepository.findByIdIncludingDeleted(taskId)
                .map(task ->
                        task.getCreatedBy() != null &&
                                task.getCreatedBy().getId().equals(currentUserId)
                )
                .orElse(false);
    }
}
