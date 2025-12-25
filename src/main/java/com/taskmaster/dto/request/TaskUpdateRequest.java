package com.taskmaster.dto.request;

import com.taskmaster.common.enums.TaskPriority;
import com.taskmaster.common.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {

    @Size(min = 3, max = 100)
    private String title;

    @Size(min = 10, max = 1000)
    private String description;

    private TaskPriority priority;
    private TaskStatus status;

    @FutureOrPresent
    private LocalDateTime dueDate;

    private Long assigneeId;

    private String location;
    private String remarks;
}
