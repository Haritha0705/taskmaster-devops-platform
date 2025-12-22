package com.taskmaster.common.enums;

import lombok.Getter;

@Getter
public enum TaskPriority {
    TASK_LOW("Low"),
    TASK_MEDIUM("Medium"),
    TASK_HIGH("High");

    private final String displayPriority;

    TaskPriority(String displayPriority) {
        this.displayPriority = displayPriority;
    }
}
