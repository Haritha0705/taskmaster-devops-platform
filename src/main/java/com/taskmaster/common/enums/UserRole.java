package com.taskmaster.common.enums;

/**
 * User roles in the system
 */
public enum UserRole {
    ROLE_USER("User"),
    ROLE_ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
