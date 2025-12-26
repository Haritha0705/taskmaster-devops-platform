package com.taskmaster.config.security;

import com.taskmaster.repository.TaskRepository;
import com.taskmaster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    public boolean isOwner(Long userId) {
        Long currentUserId = securityService.getCurrentUserId();
        return userId.equals(currentUserId);
    }
}

