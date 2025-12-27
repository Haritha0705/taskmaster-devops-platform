package com.taskmaster.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.taskmaster.common.enums.UserRole;
import com.taskmaster.common.exception.custom.BadRequestException;
import com.taskmaster.common.exception.custom.DuplicateResourceException;
import com.taskmaster.config.security.JwtTokenProvider;
import com.taskmaster.dto.request.RegisterRequest;
import com.taskmaster.dto.response.AuthResponse;
import com.taskmaster.service.impl.AuthServiceImpl;
import com.taskmaster.dto.response.UserResponse;
import com.taskmaster.entity.UserEntity;
import com.taskmaster.mapper.UserMapper;
import com.taskmaster.mapper.AuthMapper;
import com.taskmaster.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private AuthServiceImpl authService;

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

    @Test
    void register_successful() {
        UserEntity userEntity = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(UserRole.ROLE_USER)
                .build();

        UserEntity savedUser = UserEntity.builder()
                .id(1L)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(UserRole.ROLE_USER)
                .build();

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(savedUser.getEmail());

        // Mock mapper to convert request to entity
            when(authMapper.toUserEntity(request)).thenReturn(userEntity);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateAccessToken(savedUser)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(savedUser)).thenReturn("refreshToken");
        when(userMapper.toResponse(savedUser)).thenReturn(userResponse);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(userResponse.getEmail(), response.getUser().getEmail());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).save(any(UserEntity.class));
        verify(passwordEncoder).encode(request.getPassword());
        verify(authMapper).toUserEntity(request); // Ensure mapper was called
    }

    @Test
    void register_passwordMismatch_throwsBadRequestException() {
        request.setConfirmPassword("differentPassword");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.register(request));

        assertEquals("Passwords do not match", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_duplicateEmail_throwsDuplicateResourceException() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> authService.register(request));

        assertTrue(exception.getMessage().contains(request.getEmail()));
        verify(userRepository, never()).save(any());
    }

    @Test
    void role() {
        request.setRole("differentPassword");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.register(request));

        assertEquals("Passwords do not match", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_successful() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email(request.getEmail())
                .password("encodedPassword")
                .build();

        UserEntity savedUser = UserEntity.builder()
                .id(1L)
                .email(request.getEmail())
                .build();

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(savedUser.getEmail());

        when(authMapper.toUserEntity(request)).thenReturn(userEntity);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateAccessToken(savedUser)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(savedUser)).thenReturn("refreshToken");
        when(userMapper.toResponse(savedUser)).thenReturn(userResponse);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(userResponse.getEmail(), response.getUser().getEmail());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).save(any(UserEntity.class));
        verify(passwordEncoder).encode(request.getPassword());
        verify(authMapper).toUserEntity(request);

    }

    @Test
    void logout_successful() {
        String token = "dummyToken";
        assertDoesNotThrow(() -> authService.logout(token));
    }
}
