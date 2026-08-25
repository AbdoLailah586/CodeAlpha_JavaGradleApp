package com.codealpha.app;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class MessageService {

    private final String appName;
    private final String version;
    private final Instant startTime;

    public MessageService(String appName, String version) {
        this.appName = appName;
        this.version = version;
        this.startTime = Instant.now();
    }

    public String formatGreeting(String name) {
        if (name == null || name.trim().isEmpty()) {
            name = "DevOps Engineer";
        }
        return "Welcome to " + appName + ", " + name.trim() + "!";
    }

    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", appName);
        health.put("version", version);
        health.put("startedAt", startTime.toString());
        health.put("timestamp", Instant.now().toString());
        return health;
    }

    public Map<String, Object> getAppInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", appName);
        info.put("version", version);
        info.put("buildTool", "Gradle");
        info.put("runtime", System.getProperty("java.version"));
        info.put("os", System.getProperty("os.name"));
        return info;
    }
}
