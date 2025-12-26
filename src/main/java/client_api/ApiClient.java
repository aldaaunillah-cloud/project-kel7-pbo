package client_api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HTTP client sederhana untuk mode 3-tier.
 * (Sengaja simple untuk tugas PBO; di production sebaiknya pakai OkHttp/HttpClient).
 */
public class ApiClient {

    /** Deprecated: jangan hardcode; pakai baseUrl parameter atau AppConfig.API_BASE_URL. */
    @Deprecated
    protected static String BASE_URL = "http://localhost:8081/api";

    protected static String get(String baseUrl, String endpoint) throws Exception {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(6000);
        conn.setReadTimeout(12000);
        conn.setRequestMethod("GET");

        int status = conn.getResponseCode();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(status >= 400 ? conn.getErrorStream() : conn.getInputStream())
        );

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);
        reader.close();

        return response.toString();
    }

    /** Backward compatible */
    @SuppressWarnings("deprecation")
    protected static String get(String endpoint) throws Exception {
        return get(BASE_URL, endpoint);
    }
}
