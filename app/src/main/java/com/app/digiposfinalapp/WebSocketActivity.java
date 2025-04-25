package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketActivity extends AppCompatActivity {
    private MyWebSocketClient webSocketClient;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);

        // Get SharedPreferences correctly (no requireContext() in Activity)
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;
        dbPassword1 = Constants.PASSWORD;

        initWebSocket(); // Initialize WebSocket connection
    }

    private void initWebSocket() {
        // Use a dedicated WebSocket port instead of 1433
        String wsUrl = "ws://" + ipAddress1 + ":8080/ws"; // Changed from 1433 to 8080

        URI uri;
        try {
            uri = new URI(wsUrl);
            Log.d("WebSocket", "Attempting to connect to: " + wsUrl);
        } catch (URISyntaxException e) {
            Log.e("WebSocket", "Invalid URI: " + wsUrl, e);
            showToast("Invalid server address");
            return;
        }

        webSocketClient = new MyWebSocketClient(uri);
        try {
            webSocketClient.connect();
        } catch (Exception e) {
            Log.e("WebSocket", "Connection error", e);
            showToast("Connection failed: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    private class MyWebSocketClient extends WebSocketClient {
        public MyWebSocketClient(URI serverUri) {
            super(serverUri);
            setConnectionLostTimeout(30); // Add timeout
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d("WebSocket", "Connected to: " + getURI());
            runOnUiThread(() -> {
                showToast("Connected to server!"); // SUCCESS TOAST
                // Send authentication

                String authMessage = String.format("{\"db\":\"%s\",\"user\":\"%s\",\"pass\":\"%s\"}",
                        databaseName1, dbUsername1, dbPassword1);
                send(authMessage);
            });
        }

        @Override
        public void onMessage(String message) {
            Log.d("WebSocket", "Received: " + message);
            runOnUiThread(() -> {
                showToast("Message: " + message); // Show received messages
            });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.d("WebSocket", String.format(
                    "Connection closed by %s. Code: %d, Reason: %s",
                    remote ? "server" : "client", code, reason));
            runOnUiThread(() -> {
                showToast("Disconnected: " + reason); // Show disconnect reason
            });
        }

        @Override
        public void onError(Exception ex) {
            Log.e("WebSocket", "Error: " + ex.getMessage(), ex);
            runOnUiThread(() -> {
                showToast("Error: " + ex.getMessage()); // Show error message
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

