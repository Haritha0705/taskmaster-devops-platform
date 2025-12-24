package com.taskmaster.common.constants;

public class ApiPaths {
    private ApiPaths() {
        throw new IllegalStateException("Constants class - cannot be instantiated");
    }

    // Base paths
    public static final String API_BASE = "/api";
    public static final String API_VERSION = "/v1";
    public static final String API_V1 = API_BASE + API_VERSION;

    // Authentication endpoints
    public static final String AUTH = API_V1 + "/auth";
    public static final String AUTH_LOGIN = "/login";
    public static final String AUTH_REGISTER = "/register";
    public static final String AUTH_REFRESH = "/refresh-token";
    public static final String AUTH_LOGOUT = "/logout";

    // User endpoints
    public static final String USERS = API_V1 + "/users";
    public static final String USER_ME = "/me";
    public static final String USER_BY_ID = "/{id}";
    public static final String USER_SEARCH = "/search";

    // Task endpoints
    public static final String TASKS = API_V1 + "/tasks";
    public static final String TASK_BY_ID = "/{id}";
    public static final String TASK_SEARCH = "/search";
}
