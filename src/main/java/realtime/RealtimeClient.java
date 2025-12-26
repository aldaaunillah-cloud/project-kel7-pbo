package realtime;

import app.AppConfig;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.swing.*;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Client WebSocket untuk real-time update.
 * NOTE: callback WebSocket bukan di EDT, jadi wajib SwingUtilities.invokeLater saat update UI.
 */
public class RealtimeClient extends WebSocketClient implements RealtimePublisher {

    private final Gson gson = new Gson();
    private final List<Consumer<RealtimeEvent>> listeners = new CopyOnWriteArrayList<>();

    public RealtimeClient() throws Exception {
        super(new URI(AppConfig.WS_URL));
    }

    public void addListener(Consumer<RealtimeEvent> listener) {
        listeners.add(listener);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[WS] Connected: " + getURI());
    }

    @Override
    public void onMessage(String message) {
        try {
            RealtimeEvent event = gson.fromJson(message, RealtimeEvent.class);
            // dispatch ke listener (UI update harus via EDT)
            for (Consumer<RealtimeEvent> l : listeners) {
                SwingUtilities.invokeLater(() -> l.accept(event));
            }
        } catch (Exception e) {
            System.err.println("[WS] Failed to parse message: " + message);
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[WS] Closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("[WS] Error: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void publish(RealtimeEvent event) {
        try {
            send(gson.toJson(event));
        } catch (Exception e) {
            System.err.println("[WS] Failed to send event");
            e.printStackTrace();
        }
    }
}
