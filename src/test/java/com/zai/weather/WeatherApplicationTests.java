package com.zai.weather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for the main Spring Boot application.
 */
@SpringBootTest
@ActiveProfiles("test")
class WeatherApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads successfully
    }
}
