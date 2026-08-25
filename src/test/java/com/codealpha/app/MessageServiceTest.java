package com.codealpha.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MessageService Unit Tests")
class MessageServiceTest {

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService("CodeAlpha Java Gradle App", "1.0.0");
    }

    @Test
    @DisplayName("Should format greeting with custom name")
    void testFormatGreetingWithCustomName() {
        String result = messageService.formatGreeting("Abdol");
        assertEquals("Welcome to CodeAlpha Java Gradle App, Abdol!", result);
    }

    @Test
    @DisplayName("Should format greeting with default name when empty or null")
    void testFormatGreetingWithDefaultName() {
        String resultNull = messageService.formatGreeting(null);
        assertEquals("Welcome to CodeAlpha Java Gradle App, DevOps Engineer!", resultNull);

        String resultBlank = messageService.formatGreeting("   ");
        assertEquals("Welcome to CodeAlpha Java Gradle App, DevOps Engineer!", resultBlank);
    }

    @Test
    @DisplayName("Should return valid health status")
    void testHealthStatus() {
        Map<String, Object> health = messageService.getHealthStatus();
        assertNotNull(health);
        assertEquals("UP", health.get("status"));
        assertEquals("CodeAlpha Java Gradle App", health.get("service"));
        assertEquals("1.0.0", health.get("version"));
        assertNotNull(health.get("startedAt"));
        assertNotNull(health.get("timestamp"));
    }

    @Test
    @DisplayName("Should return valid app metadata info")
    void testAppInfo() {
        Map<String, Object> info = messageService.getAppInfo();
        assertNotNull(info);
        assertEquals("CodeAlpha Java Gradle App", info.get("application"));
        assertEquals("Gradle", info.get("buildTool"));
        assertEquals("1.0.0", info.get("version"));
    }
}
