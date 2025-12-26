package realtime.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Server WebSocket sederhana untuk broadcast event antar client.
 * Untuk 2-tier: client yg sukses transaksi akan publish event ke server ini,
 * lalu server broadcast ke client lain => "real-time".
 * Untuk 3-tier: server HTTP bisa juga memanggil broadcast() setelah transaksi.
 */
public class RealtimeWebSocketServer extends WebSocketServer {

    public RealtimeWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("[WS-SERVER] client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("[WS-SERVER] client disconnected: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // minimal: broadcast ke semua client
        System.out.println("[WS-SERVER] message: " + message);
        broadcast(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("[WS-SERVER] error: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("[WS-SERVER] started on " + getAddress());
    }
}
