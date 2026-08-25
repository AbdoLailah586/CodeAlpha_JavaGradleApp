package com.codealpha.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class App {

    private static final int DEFAULT_PORT = 8080;
    private static final String APP_NAME = "CodeAlpha Java Gradle App";
    private static final String VERSION = "1.0.0";

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty()) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {}
        }

        MessageService messageService = new MessageService(APP_NAME, VERSION);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Root Dashboard
        server.createContext("/", new RootHandler());

        // Health Endpoint
        server.createContext("/api/health", exchange -> {
            Map<String, Object> health = messageService.getHealthStatus();
            String json = toJson(health);
            sendJsonResponse(exchange, 200, json);
        });

        // Info Endpoint
        server.createContext("/api/info", exchange -> {
            Map<String, Object> info = messageService.getAppInfo();
            String json = toJson(info);
            sendJsonResponse(exchange, 200, json);
        });

        // Greet Endpoint
        server.createContext("/api/greet", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String name = "DevOps Engineer";
            if (query != null && query.contains("name=")) {
                String[] parts = query.split("name=");
                if (parts.length > 1) {
                    name = parts[1].split("&")[0];
                }
            }
            String message = messageService.formatGreeting(name);
            String json = "{\"message\":\"" + message + "\",\"timestamp\":\"" + java.time.Instant.now() + "\"}";
            sendJsonResponse(exchange, 200, json);
        });

        server.setExecutor(null); // default executor
        System.out.println("=================================================");
        System.out.println("🚀 " + APP_NAME + " v" + VERSION + " started!");
        System.out.println("📡 Server listening on http://localhost:" + port);
        System.out.println("🩺 Healthcheck: http://localhost:" + port + "/api/health");
        System.out.println("=================================================");
        server.start();
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestURI().getPath().equals("/")) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>CodeAlpha DevOps - Task 3 | Java & Gradle</title>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #0f172a; color: #f8fafc; margin: 0; padding: 2rem; display: flex; justify-content: center; }
                        .card { background: #1e293b; border: 1px solid #334155; border-radius: 12px; max-width: 650px; width: 100%; padding: 2rem; box-shadow: 0 10px 25px rgba(0,0,0,0.3); }
                        .badge { background: #0284c7; color: white; padding: 4px 12px; border-radius: 20px; font-size: 0.8rem; font-weight: bold; text-transform: uppercase; }
                        h1 { font-size: 1.8rem; margin: 1rem 0 0.5rem; color: #38bdf8; }
                        p { color: #94a3b8; line-height: 1.6; }
                        .endpoint { background: #0b1120; border-left: 4px solid #10b981; padding: 0.75rem 1rem; margin: 0.75rem 0; border-radius: 4px; font-family: monospace; }
                        .endpoint a { color: #38bdf8; text-decoration: none; font-weight: bold; }
                        .endpoint a:hover { text-decoration: underline; }
                        .tag { float: right; color: #10b981; font-weight: bold; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <span class="badge">CodeAlpha DevOps Internship</span>
                        <h1>Task 3: Java Application using Gradle</h1>
                        <p>This Java application is built with <strong>Gradle</strong>, tested with <strong>JUnit 5</strong>, and automated via <strong>GitHub Actions CI/CD</strong>.</p>
                        
                        <h3>Available REST Endpoints:</h3>
                        <div class="endpoint">
                            <span class="tag">GET</span>
                            <a href="/api/health" target="_blank">/api/health</a> - Service health & uptime
                        </div>
                        <div class="endpoint">
                            <span class="tag">GET</span>
                            <a href="/api/info" target="_blank">/api/info</a> - Build & runtime metadata
                        </div>
                        <div class="endpoint">
                            <span class="tag">GET</span>
                            <a href="/api/greet?name=Intern" target="_blank">/api/greet?name=Intern</a> - Parameterized greeting API
                        </div>
                    </div>
                </body>
                </html>
                """;
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
