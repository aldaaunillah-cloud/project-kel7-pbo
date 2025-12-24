package ws;

import java.util.ArrayList;
import java.util.List;

public class RealtimeEndpoint {

    private static List<String> clients = new ArrayList<>();

    public static void connect(String clientName) {
        clients.add(clientName);
        System.out.println(clientName + " connected");
    }

    public static void sendMessage(String message) {
        for (String c : clients) {
            System.out.println("Send to " + c + ": " + message);
        }
    }

    public static void disconnect(String clientName) {
        clients.remove(clientName);
        System.out.println(clientName + " disconnected");
    }
}
