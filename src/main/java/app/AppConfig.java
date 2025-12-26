package app;

/**
 * Satu pintu konfigurasi app (untuk rubrik 2-tier vs 3-tier).
 * Ubah IS_3_TIER untuk switch mode tanpa ubah code UI/service.
 */
public final class AppConfig {
    private AppConfig(){}

    /** true  = 3-Tier (client -> HTTP API/WS server -> DB)
     *  false = 2-Tier (client langsung JDBC ke MySQL) */
    public static final boolean IS_3_TIER =
            Boolean.parseBoolean(System.getProperty("IS_3_TIER", "false"));

    /** Base URL HTTP API untuk mode 3-tier */
    public static final String API_BASE_URL =
            System.getProperty("API_BASE_URL", "http://localhost:8081/api");

    /** URL WebSocket (real-time) untuk 2-tier maupun 3-tier */
    public static final String WS_URL =
            System.getProperty("WS_URL", "ws://localhost:8080");
}
