package com.taskmaster.e2e;

import com.taskmaster.common.constants.ApiPaths;
import com.taskmaster.dto.request.LoginRequest;
import com.taskmaster.dto.request.RegisterRequest;
import com.taskmaster.dto.response.AuthResponse;
import com.taskmaster.dto.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LogoutBlacklistE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void logout_should_blacklist_token_and_prevent_access() {
        // Register
        RegisterRequest reg = new RegisterRequest();
        reg.setFirstName("Logout");
        reg.setLastName("Tester");
        reg.setEmail("logout@test.com");
        reg.setPassword("password");
        reg.setConfirmPassword("password");

        ResponseEntity<String> regResp = restTemplate.postForEntity(ApiPaths.AUTH + ApiPaths.AUTH_REGISTER, reg, String.class);
        assertEquals(HttpStatus.CREATED, regResp.getStatusCode());

        // Login
        LoginRequest login = new LoginRequest();
        login.setEmail("logout@test.com");
        login.setPassword("password");

        ResponseEntity<String> loginResp = restTemplate.postForEntity(ApiPaths.AUTH + ApiPaths.AUTH_LOGIN, login, String.class);
        assertEquals(HttpStatus.OK, loginResp.getStatusCode());

        // extract token from response body (simple parse)
        // The API wraps AuthResponse in ApiResponse; we'll just look for accessToken string
        String body = loginResp.getBody();
        assertNotNull(body);
        int idx = body.indexOf("accessToken\":\"");
        assertTrue(idx > 0);
        int start = idx + "accessToken\":\"".length();
        int end = body.indexOf('"', start);
        String accessToken = body.substring(start, end);
        assertNotNull(accessToken);

        // Logout
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> logoutReq = new HttpEntity<>(headers);
        ResponseEntity<String> logoutResp = restTemplate.exchange(ApiPaths.AUTH + ApiPaths.AUTH_LOGOUT, HttpMethod.POST, logoutReq, String.class);
        assertEquals(HttpStatus.OK, logoutResp.getStatusCode());

        // Try to access /users/me with same token
        HttpHeaders h2 = new HttpHeaders();
        h2.setBearerAuth(accessToken);
        HttpEntity<Void> reqMe = new HttpEntity<>(h2);
        ResponseEntity<String> meResp = restTemplate.exchange(ApiPaths.USERS + ApiPaths.USER_ME, HttpMethod.GET, reqMe, String.class);

        // Expect 401 Unauthorized (blacklisted)
        assertTrue(meResp.getStatusCode()==HttpStatus.UNAUTHORIZED || meResp.getStatusCode()==HttpStatus.FORBIDDEN, "Expected 401/403 but was " + meResp.getStatusCode());
    }
}

