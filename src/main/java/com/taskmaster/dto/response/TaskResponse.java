package com.taskmaster.dto.response;

import com.taskmaster.common.enums.TaskPriority;
import com.taskmaster.common.enums.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDateTime dueDate;

    private String location;
    private String remarks;

    // Audit

    private Boolean isDeleted;
    private Long deletedBy;
    private LocalDateTime deletedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long createdBy;
    private Long updatedBy;
}
