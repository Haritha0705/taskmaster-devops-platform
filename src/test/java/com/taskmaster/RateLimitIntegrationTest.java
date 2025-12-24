package com.taskmaster;// File: `src/test/java/com/taskmaster/RateLimitIntegrationTest.java`


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(com.taskmaster.config.redis.TestRateLimiterConfig.class)
class RateLimitIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(RateLimitIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rateLimitIsEnforced() throws Exception {
        log.info("Starting RateLimitIntegrationTest using Java {}", System.getProperty("java.version"));
        String url = "/api/v1/auth/rate-limit-test"; // use relative path for MockMvc
        int limit = 100; // must match `application.rate-limit.requests`
        for (int i = 1; i <= limit + 1; i++) {
            if (i <= limit) {
                mockMvc.perform(get(url))
                        .andExpect(status().isOk());
            } else {
                mockMvc.perform(get(url))
                        .andExpect(status().isTooManyRequests());
            }
        }
    }
}
