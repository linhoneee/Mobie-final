package com.example.brandtests.service;

import android.util.Log;
import com.example.brandtests.model.ChatMessage;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketService {

    private static final String TAG = "WebSocketService";
    private WebSocket webSocket;
    private OkHttpClient client;
    private MessageCallback callback;
    private Gson gson = new Gson();

    // Interface để xử lý callback khi nhận tin nhắn
    public interface MessageCallback {
        void onMessage(String message);
    }

    public WebSocketService(MessageCallback callback) {
        this.callback = callback;
    }

    public void connectWebSocket(Long userId) {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://10.0.2.2:6010/native-ws")
                .addHeader("RoomID", userId.toString())
                .build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d(TAG, "WebSocket connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);
                if (callback != null) {
                    callback.onMessage(text);
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "Closing WebSocket: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e(TAG, "WebSocket error: " + t.getMessage(), t);
            }
        });
    }

    // Phương thức gửi tin nhắn qua WebSocket
    public void sendMessage(ChatMessage message) {
        String jsonMessage = gson.toJson(message);
        if (webSocket != null) {
            webSocket.send(jsonMessage);
            Log.d(TAG, "Sending message: " + jsonMessage);
        } else {
            Log.e(TAG, "WebSocket is not connected");
        }
    }


}
