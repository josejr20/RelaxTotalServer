package com.andreutp.centromasajes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class CentromasajesApplicationTests {

    @Autowired
    ApplicationContext context;

	@Test
	void contextLoads() {
        // context must be injected by Spring
        assertNotNull(context);
	}

}
