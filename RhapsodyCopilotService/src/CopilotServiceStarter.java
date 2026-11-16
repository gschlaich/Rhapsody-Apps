package de.schlaich.gunnar.rhapsody.copilotService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.copilot.CopilotClient;
import com.github.copilot.rpc.MessageOptions;
import com.github.copilot.rpc.SessionConfig;
import com.sun.net.httpserver.HttpServer;
import com.github.copilot.rpc.PermissionHandler;



public class CopilotServiceStarter implements AutoCloseable {

    private final ObjectMapper mapper = new ObjectMapper();
    private final CopilotClient client = new CopilotClient();
    private final HttpServer server;

    public CopilotServiceStarter(int port) throws Exception {

        client.start().get();

        server = HttpServer.create(
            new InetSocketAddress("127.0.0.1", port), 0);

        server.createContext("/health", exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                reply(exchange, 405, Map.of("error", "Method not allowed"));
                return;
            }
            reply(exchange, 200, Map.of("status", "ok"));
        });

        server.createContext("/ai/chat", this::handleChat);

        server.setExecutor(Executors.newCachedThreadPool());
    }

    private void handleChat(HttpExchange exchange) throws IOException {

        if (!"POST".equals(exchange.getRequestMethod())) {
            reply(exchange, 405, Map.of("error", "Method not allowed"));
            return;
        }

        try {
            Map<String, String> request = mapper.readValue(
                exchange.getRequestBody(),
                new TypeReference<Map<String, String>>() {});

            String prompt = request.get("prompt");

            if (prompt == null || prompt.isBlank()) {
                reply(exchange, 400, Map.of("error", "Missing prompt"));
                return;
            }

            // Separate session for each request
            var session = client.createSession(
                new SessionConfig()
                    .setModel("auto")
                    .setOnPermissionRequest(
                        PermissionHandler.APPROVE_ALL)
            ).get();

            try {
                var response = session.sendAndWait(
                    new MessageOptions().setPrompt(prompt),
                    120_000
                ).get();

                String answer = response != null
                    ? response.getData().content()
                    : "";

                reply(exchange, 200,
                    Map.of("response", answer != null ? answer : ""));

            } finally {
                session.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            reply(exchange, 500,
                Map.of("error", "Copilot request failed"));
        }
    }

    private void reply(HttpExchange exchange, int status,
                       Map<String, String> data) throws IOException {

        byte[] body = mapper.writeValueAsBytes(data);

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(status, body.length);

        try (var out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    public void start() {
        server.start();
        System.out.println("CopilotService listening on port 8766");
    }

    @Override
    public void close() throws Exception {
        server.stop(1);
        client.close();
    }

    public static void main(String[] args) throws Exception {

        CopilotServiceStarter service = new CopilotServiceStarter(8766);

        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> {
                try {
                    service.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
        );

        service.start();
    }
}
