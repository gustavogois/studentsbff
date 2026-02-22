package com.studentsbff;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StudentsBffApplicationTest {

    @Test
    void contextLoads() {
        // Smoke test: verifies Spring context starts without errors
    }
}
