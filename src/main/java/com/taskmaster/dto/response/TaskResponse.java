package com.taskmaster.dto.response;

import com.taskmaster.common.enums.TaskPriority;
import com.taskmaster.common.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskPriority priority,
        TaskStatus status,
        LocalDateTime dueDate,
        String location,
        String remarks,
        Boolean isDeleted,
        Long deletedBy,
        LocalDateTime deletedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long updatedBy
) {}
