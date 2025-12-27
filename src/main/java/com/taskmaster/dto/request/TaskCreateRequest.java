package com.taskmaster.dto.request;

import com.taskmaster.common.enums.TaskPriority;
import com.taskmaster.common.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank
    @Size(min = 10, max = 1000)
    private String description;

    @NotNull
    private TaskPriority priority;

    @NotNull
    private TaskStatus status;

    @NotNull
    @FutureOrPresent
    private LocalDateTime dueDate;

    private String location;
    private String remarks;
}
