package com.taskmaster.service;

import com.taskmaster.common.enums.UserRole;
import com.taskmaster.dto.request.RegisterRequest;
import com.taskmaster.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {
    @Mock
    private UserService userService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setPhone("1234567890");
        request.setRole("USER");
    }

//    @Test
//    void testCreateUser_Success() {
//        UserEntity userEntity = UserEntity.builder()
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(request.getEmail())
//                .phone(request.getPhone())
//                .role(UserRole.ROLE_USER)
//                .build();
//    }

}
