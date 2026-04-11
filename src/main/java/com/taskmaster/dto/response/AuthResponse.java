package com.taskmaster.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserSummaryResponse user
) {
    public static AuthResponse of(
            String accessToken,
            String refreshToken,
            Long expiresIn,
            UserSummaryResponse user) {

        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
