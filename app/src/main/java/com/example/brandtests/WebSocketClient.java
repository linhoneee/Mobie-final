package com.example.brandtests;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient {

    private static final String TAG = "WebSocketClient";  // Tag cho logs
    private WebSocket webSocket;
    private OkHttpClient client;
    private MessageCallback callback;

    public interface MessageCallback {
        void onMessage(String message);
    }

    public WebSocketClient(MessageCallback callback) {
        this.callback = callback;
    }

    public void connect() {
        client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://10.0.2.2:6010/native-ws").build();
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

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
            Log.d(TAG, "Sending message: " + message);
        } else {
            Log.e(TAG, "WebSocket is not connected");
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing");
            Log.d(TAG, "Closing WebSocket");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }
}
