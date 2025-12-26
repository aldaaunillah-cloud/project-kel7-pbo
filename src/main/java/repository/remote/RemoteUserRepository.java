package repository.remote;

import app.AppConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.User;
import repository.DataAccessException;
import repository.UserRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class RemoteUserRepository implements UserRepository {

    private final Gson gson = new Gson();

    @Override
    public Optional<User> login(String username, String hashedPassword) throws DataAccessException {
        try {
            JsonObject req = new JsonObject();
            req.addProperty("username", username);
            req.addProperty("passwordHash", hashedPassword);

            JsonObject res = postJson(AppConfig.API_BASE_URL + "/auth/login", req);

            boolean ok = res.has("ok") && res.get("ok").getAsBoolean();
            if (!ok) return Optional.empty();

            JsonObject userObj = res.getAsJsonObject("user");
            User u = new User();
            u.setId(userObj.get("id").getAsInt());
            u.setUsername(userObj.get("username").getAsString());
            return Optional.of(u);

        } catch (Exception e) {
            throw new DataAccessException("Gagal login (3-tier). Cek server API.", e);
        }
    }

    @Override
    public boolean register(String username, String hashedPassword) throws DataAccessException {
        try {
            JsonObject req = new JsonObject();
            req.addProperty("username", username);
            req.addProperty("passwordHash", hashedPassword);

            JsonObject res = postJson(AppConfig.API_BASE_URL + "/auth/register", req);

            boolean ok = res.has("ok") && res.get("ok").getAsBoolean();
            return ok;

        } catch (Exception e) {
            throw new DataAccessException("Gagal register (3-tier). Cek server API.", e);
        }
    }

    @Override
    public Optional<String> getUsernameById(int userId) throws DataAccessException {
        try {
            JsonObject res = getJson(AppConfig.API_BASE_URL + "/users/" + userId);

            boolean ok = res.has("ok") && res.get("ok").getAsBoolean();
            if (!ok) return Optional.empty();

            String name = res.has("username") ? res.get("username").getAsString() : null;
            return Optional.ofNullable(name);

        } catch (Exception e) {
            throw new DataAccessException("Gagal ambil username (3-tier). Cek server API.", e);
        }
    }

    // =============================
    // HTTP Helpers
    // =============================

    private JsonObject postJson(String url, JsonObject body) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload);
        }

        int code = conn.getResponseCode();
        String raw = readResponse(conn, code);

        if (code < 200 || code >= 300) {
            throw new RuntimeException("HTTP " + code + " => " + raw);
        }

        return gson.fromJson(raw, JsonObject.class);
    }

    private JsonObject getJson(String url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);

        int code = conn.getResponseCode();
        String raw = readResponse(conn, code);

        if (code < 200 || code >= 300) {
            throw new RuntimeException("HTTP " + code + " => " + raw);
        }

        return gson.fromJson(raw, JsonObject.class);
    }

    private String readResponse(HttpURLConnection conn, int code) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream(),
                StandardCharsets.UTF_8
        ))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}
