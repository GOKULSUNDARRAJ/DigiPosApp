package com.app.digiposfinalapp;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class MyWebSocketClient extends WebSocketClient {

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connected");
        // Send a message when connected
        send("Hello from Android!");
    }

    @Override
    public void onMessage(String message) {
        Log.d("WebSocket", "Received: " + message);
        // Handle incoming messages here
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.e("WebSocket", "Error: " + ex.getMessage());
    }
}