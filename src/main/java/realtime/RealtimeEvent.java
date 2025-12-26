package realtime;

import com.google.gson.JsonObject;

/** Payload real-time yang dibroadcast via WebSocket. */
public class RealtimeEvent {
    public String type;
    public JsonObject payload;

    public RealtimeEvent() {}

    public RealtimeEvent(String type, JsonObject payload) {
        this.type = type;
        this.payload = payload;
    }
}
