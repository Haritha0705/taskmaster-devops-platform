package com.taskmaster.dto.response;

import com.taskmaster.common.enums.TaskPriority;
import com.taskmaster.common.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskSummaryResponse(
        Long id,
        String title,
        TaskStatus status,
        TaskPriority priority,
        LocalDateTime dueDate
) {}

