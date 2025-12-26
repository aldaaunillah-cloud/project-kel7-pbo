package server;

import realtime.server.RealtimeWebSocketServer;

import java.util.concurrent.CountDownLatch;

/**
 * Entry point untuk MODE 3-TIER:
 * - WebSocket server (real-time) port 8080
 * - HTTP API server port 8081
 */
public class ServerMain {

    public static void main(String[] args) {
        try {
            RealtimeWebSocketServer ws = new RealtimeWebSocketServer(8080);
            ws.start();

            ApiHttpServer http = new ApiHttpServer();
            http.start(8081);

            System.out.println("========================================");
            System.out.println("✅ SERVER 3-TIER BERJALAN");
            System.out.println("WebSocket : ws://localhost:8080");
            System.out.println("HTTP API  : http://localhost:8081/api");
            System.out.println("Tekan CTRL+C untuk stop.");
            System.out.println("========================================");

            // penting: tahan thread utama agar server tidak langsung berhenti
            new CountDownLatch(1).await();

        } catch (Exception e) {
            System.err.println("❌ Gagal menjalankan ServerMain");
            e.printStackTrace();
        }
    }
}
