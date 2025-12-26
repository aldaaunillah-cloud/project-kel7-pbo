package client_api;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WSClient {

    private Session session;
    private WebSocketContainer container;

    public void connect() {
        try {
            container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this,
                    new URI("ws://localhost:8080/realtime"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WebSocket connected");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message received: " + message);
    }

    @OnClose
    public void onClose() {
        System.out.println("WebSocket closed");
    }

    public void sendMessage(String message) {
        try {
            if (session != null) {
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
