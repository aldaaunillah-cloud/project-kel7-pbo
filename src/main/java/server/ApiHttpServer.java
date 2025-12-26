package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.User;
import repository.UserRepository;
import repository.jdbc.JdbcUserRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ApiHttpServer {

    private final Gson gson = new Gson();
    private HttpServer server;

    // Server tier ke-3 akses DB pakai JDBC
    private final UserRepository userRepo = new JdbcUserRepository();

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // ROUTE yang wajib ada (biar tidak 404)
        server.createContext("/api/ping", this::handlePing);
        server.createContext("/api/auth/login", this::handleLogin);
        server.createContext("/api/auth/register", this::handleRegister);

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();

        System.out.println("[HTTP] API started on http://localhost:" + port + "/api");
        System.out.println("[HTTP] GET  /api/ping");
        System.out.println("[HTTP] POST /api/auth/login");
        System.out.println("[HTTP] POST /api/auth/register");
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    // =========================
    // Handlers
    // =========================

    private void handlePing(HttpExchange ex) throws IOException {
        logReq(ex);
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            sendJson(ex, 405, jsonError("Method not allowed"));
            return;
        }
        JsonObject res = new JsonObject();
        res.addProperty("ok", true);
        res.addProperty("message", "pong");
        sendJson(ex, 200, res);
    }

    private void handleLogin(HttpExchange ex) throws IOException {
        logReq(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            sendCorsOk(ex);
            return;
        }
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            sendJson(ex, 405, jsonError("Method not allowed"));
            return;
        }

        try {
            JsonObject req = readBodyJson(ex);

            String username = req.has("username") ? req.get("username").getAsString() : "";
            String passwordHash = req.has("passwordHash") ? req.get("passwordHash").getAsString() : "";

            Optional<User> userOpt = userRepo.login(username, passwordHash);

            JsonObject res = new JsonObject();
            if (userOpt.isEmpty()) {
                res.addProperty("ok", false);
                sendJson(ex, 200, res);
                return;
            }

            User u = userOpt.get();
            res.addProperty("ok", true);

            JsonObject user = new JsonObject();
            user.addProperty("id", u.getId());
            user.addProperty("username", u.getUsername());
            res.add("user", user);

            sendJson(ex, 200, res);

        } catch (Exception e) {
            sendJson(ex, 500, jsonError("Server error saat login: " + e.getMessage()));
        }
    }

    private void handleRegister(HttpExchange ex) throws IOException {
        logReq(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            sendCorsOk(ex);
            return;
        }
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            sendJson(ex, 405, jsonError("Method not allowed"));
            return;
        }

        try {
            JsonObject req = readBodyJson(ex);

            String username = req.has("username") ? req.get("username").getAsString() : "";
            String passwordHash = req.has("passwordHash") ? req.get("passwordHash").getAsString() : "";

            boolean ok = userRepo.register(username, passwordHash);

            JsonObject res = new JsonObject();
            res.addProperty("ok", ok); // false kalau username sudah dipakai
            sendJson(ex, 200, res);

        } catch (Exception e) {
            sendJson(ex, 500, jsonError("Server error saat register: " + e.getMessage()));
        }
    }

    // =========================
    // Helpers
    // =========================

    private void logReq(HttpExchange ex) {
        System.out.println("[HTTP] " + ex.getRequestMethod() + " " + ex.getRequestURI());
    }

    private JsonObject readBodyJson(HttpExchange ex) throws IOException {
        byte[] bytes = ex.getRequestBody().readAllBytes();
        String raw = new String(bytes, StandardCharsets.UTF_8);
        if (raw.isBlank()) return new JsonObject();
        return gson.fromJson(raw, JsonObject.class);
    }

    private void sendCorsOk(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        ex.sendResponseHeaders(204, -1);
        ex.close();
    }

    private void sendJson(HttpExchange ex, int status, JsonObject obj) throws IOException {
        byte[] out = obj.toString().getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.sendResponseHeaders(status, out.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(out);
        }
    }

    private JsonObject jsonError(String msg) {
        JsonObject o = new JsonObject();
        o.addProperty("ok", false);
        o.addProperty("error", msg);
        return o;
    }
}
