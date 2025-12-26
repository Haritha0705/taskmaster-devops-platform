package com.taskmaster.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmaster.common.constants.ApiPaths;
import com.taskmaster.dto.request.RegisterRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void full_register_flow_works() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("E2E");
        request.setLastName("Tester");
        request.setEmail("e2e@test.com");
        request.setPassword("password");
        request.setConfirmPassword("password");

        ResponseEntity<String> response =
                restTemplate.postForEntity(ApiPaths.AUTH+ApiPaths.AUTH_REGISTER, request, String.class);

        // Controller returns HttpStatus.CREATED (201)
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
