package com.taskmaster.common.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    BLOCKED("Blocked"),
    COMPLETED("Completed");

    private final String displayStatus;

    TaskStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }
}
