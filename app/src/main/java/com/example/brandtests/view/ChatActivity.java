package com.example.brandtests.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brandtests.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private WebSocketClient webSocketClient;
    private EditText inputMessage;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);

        // Thiết lập URI của WebSocket server
        URI uri;
        try {
            uri = new URI("ws://10.0.2.2:6010/ws");
            Log.d(TAG, "WebSocket URI: " + uri.toString());
        } catch (URISyntaxException e) {
            Log.e(TAG, "URI Syntax Error: ", e);
            return;
        }

        // Khởi tạo WebSocketClient
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "WebSocket Connected");
                runOnUiThread(() -> {
                    sendButton.setEnabled(true);
                    Toast.makeText(ChatActivity.this, "Connected to WebSocket", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "Received message: " + message);
                // Xử lý tin nhắn nhận được
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "WebSocket Closed: " + reason);
                runOnUiThread(() -> sendButton.setEnabled(false));
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "WebSocket Error: ", ex);
                runOnUiThread(() -> sendButton.setEnabled(false));
            }
        };

        try {
            webSocketClient.connectBlocking();
            Log.d(TAG, "WebSocket Connection Attempted");
        } catch (InterruptedException e) {
            Log.e(TAG, "WebSocket Connection Interrupted: ", e);
        }

        // Gửi tin nhắn khi nhấn nút gửi
        sendButton.setOnClickListener(v -> {
            String message = inputMessage.getText().toString();
            if (!message.isEmpty()) {
                try {
                    if (webSocketClient.isOpen()) {
                        webSocketClient.send(message);
                        Log.d(TAG, "Sent message: " + message);
                        inputMessage.setText(""); // Xóa nội dung sau khi gửi
                    } else {
                        Log.e(TAG, "WebSocket is not open");
                        Toast.makeText(ChatActivity.this, "WebSocket is not connected", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error sending message: ", e);
                }
            } else {
                Log.d(TAG, "Message is empty, not sending.");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            try {
                webSocketClient.close();
                Log.d(TAG, "WebSocket Closed in onDestroy.");
            } catch (Exception e) {
                Log.e(TAG, "Error closing WebSocket in onDestroy: ", e);
            }
        }
    }
}
